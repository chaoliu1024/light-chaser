/*
 * Copyright (c) 2017, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package group.chaoliu.lightchaser.core.daemon.photon.thread;

import group.chaoliu.lightchaser.common.config.ProxyConstants;
import group.chaoliu.lightchaser.common.protocol.http.Proxy;
import group.chaoliu.lightchaser.core.fission.BaseFission;
import group.chaoliu.lightchaser.core.fission.proxy.ProxyFission;
import group.chaoliu.lightchaser.core.fission.proxy.ProxyService;
import group.chaoliu.lightchaser.core.fission.proxy.domain.ProxyPO;
import group.chaoliu.lightchaser.core.util.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class ValidateProxyThread implements Runnable {

    private int intervalHour = 1;

    private int failedCount = 5;

    public ValidateProxyThread(Map proxyConfig) {
        Object hour = proxyConfig.get(ProxyConstants.PROXY_VERIFY_INTERVAL_HOUR);
        if (null != hour && hour instanceof Integer) {
            intervalHour = (int) hour;
        }

        Object count = proxyConfig.get(ProxyConstants.PROXY_VERIFY_FAILED_COUNT);
        if (null != count && count instanceof Integer) {
            failedCount = (int) count;
        }
    }

    @Override
    public void run() {

        BaseFission fission = SpringBeanUtil.fissionBean("proxy" + BaseFission.FISSION_BEAN_SUFFIX);
        ProxyService proxyService = SpringBeanUtil.proxyServiceBean();

        while (true) {

            log.info("删除失效代理...");
            proxyService.deleteIneffectiveProxies(failedCount);

            log.info("验证代理...");
            List<Proxy> cacheProxy = proxyService.fetchAllProxies();
            log.info("捞取代理总量为: " + cacheProxy.size());

            if (fission instanceof ProxyFission) {
                ProxyFission proxyFission = (ProxyFission) fission;
                List<ProxyPO> proxies = proxyFission.validateProxy(cacheProxy);
                log.info("通过验证的代理数量: " + proxies.size());
                if (!proxies.isEmpty()) {
                    proxyFission.insertProxy(proxies);
                }
            }

            try {
                log.info("开始休眠了....");
                TimeUnit.HOURS.sleep(intervalHour);
                log.info("休眠结束了....");
            } catch (InterruptedException e) {
                log.error("sleep error...");
            }
        }
    }
}
