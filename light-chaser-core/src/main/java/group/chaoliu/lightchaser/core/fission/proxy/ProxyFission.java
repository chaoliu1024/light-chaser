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

package group.chaoliu.lightchaser.core.fission.proxy;

import group.chaoliu.lightchaser.common.protocol.http.Proxy;
import group.chaoliu.lightchaser.core.fission.BaseFission;
import group.chaoliu.lightchaser.core.fission.proxy.domain.ProxyPO;
import group.chaoliu.lightchaser.core.util.RegexUtil;
import group.chaoliu.lightchaser.core.wrapper.template.Wrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Validate proxies by request the webs in proxy.yaml,
 * and store the useful proxy into db.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
@Component("proxy" + BaseFission.FISSION_BEAN_SUFFIX)
public class ProxyFission extends BaseFission {

    public static final String PROXY_KEY = "proxy";

    public static final String PROXY_HOST = "host";
    public static final String PROXY_PORT = "port";
    public static final String PROXY_USERNAME = "user";
    public static final String PROXY_PASSWORD = "password";
    public static final String PROXY_TYPE = "type";

    @Autowired
    ProxyService proxyServer;

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @Override
    public void fission(Map data) {

        if (null != data.get(Wrapper.DATA_KEY)) {

            List<Map<String, String>> proxies = (List<Map<String, String>>) ((Map) data.get(Wrapper.DATA_KEY)).get(PROXY_KEY);
            String domainKey = (String) ((Map) data.get(Wrapper.DATA_KEY)).get(Wrapper.DOMAIN_KEY);
            Date crawlTime = (Date) ((Map) data.get(Wrapper.DATA_KEY)).get(Wrapper.CRAWL_TIME);

            if (CollectionUtils.isNotEmpty(proxies)) {

                List<Proxy> _proxies = new ArrayList<>();

                for (Map<String, String> p : proxies) {
                    if (validateProxy(p)) {
                        Proxy proxy = new Proxy(p.get(PROXY_HOST), Integer.parseInt(p.get(PROXY_PORT)));
                        if (StringUtils.isNotBlank(domainKey)) {
                            proxy.setDomainKey(domainKey);
                        }

                        if (null != crawlTime) {
                            proxy.setCrawlTime(crawlTime);
                        }

                        if (null != p.get(PROXY_USERNAME)) {
                            proxy.setUserName(p.get(PROXY_USERNAME));
                        }
                        if (null != p.get(PROXY_PASSWORD)) {
                            proxy.setPassword(p.get(PROXY_PASSWORD));
                        }
                        if (null != p.get(PROXY_TYPE)) {
                            proxy.setProxyType(p.get(PROXY_TYPE));
                        }
                        _proxies.add(proxy);
                    }
                }

                List<ProxyPO> cacheProxy = validateProxy(_proxies);

                if (!cacheProxy.isEmpty()) {
                    insertProxy(cacheProxy);
                }
            }
        }
    }

    /**
     * validate list of proxy.
     *
     * @param proxies proxy
     * @return the OK proxy list
     */
    public List<ProxyPO> validateProxy(List<Proxy> proxies) {
        List<ProxyPO> usefulProxy = new ArrayList<>();
        List<Future<ProxyPO>> result = new ArrayList<>();
        for (Proxy proxy : proxies) {
            ProxyValidator proxyValidator = new ProxyValidator(proxy);
            Future<ProxyPO> future = executor.submit(proxyValidator);
            result.add(future);
        }
        for (Future<ProxyPO> f : result) {
            try {
                ProxyPO proxyPO = f.get();
                if (proxyPO.isNotNULL()) {
                    usefulProxy.add(proxyPO);
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("Future get validator proxies exception. {}", e);
            }
        }
        return usefulProxy;
    }


    @Override
    public void finish() {
        if (!executor.isShutdown()) {
            log.info("shut down proxy thread pool");
            executor.shutdown();
            executor = null;
        }
    }

    public void insertProxy(List<ProxyPO> cacheProxy) {
        proxyServer.batchInsertProxy(cacheProxy);
    }

    /**
     * validate the proxy is legal form or not
     *
     * @param proxy proxy map
     * @return if legal return true
     */
    private boolean validateProxy(Map<String, String> proxy) {
        String ip = proxy.get(PROXY_HOST);
        String port = proxy.get(PROXY_PORT);

        if (StringUtils.isNotBlank(ip) && StringUtils.isNotBlank(port)) {
            return (RegexUtil.isLegalIP(ip) && RegexUtil.isNumber(port));
        } else {
            return false;
        }
    }
}