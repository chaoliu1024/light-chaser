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

package group.chaoliu.lightchaser.core.daemon.photon;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import group.chaoliu.lightchaser.common.config.Constants;
import group.chaoliu.lightchaser.common.config.ProxyConstants;
import group.chaoliu.lightchaser.common.config.YamlConfig;
import group.chaoliu.lightchaser.core.daemon.Deamon;
import group.chaoliu.lightchaser.core.daemon.photon.thread.CrawlProxyThread;
import group.chaoliu.lightchaser.core.daemon.photon.thread.ValidateProxyThread;
import group.chaoliu.lightchaser.core.fission.proxy.ProxyValidator;
import group.chaoliu.lightchaser.core.fission.proxy.ProxyWeb;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class Photon extends Deamon {


    public void initProxyWeb(final Map proxyConfig) {

        Map<String, List> proxyWebs = (Map) proxyConfig.get(ProxyConstants.PROXY_VERIFIERS);
        for (Map.Entry<String, List> web : proxyWebs.entrySet()) {
            ProxyWeb proxyWeb = new ProxyWeb();
            ProxyValidator.WEB_PROXY.put(web.getKey(), proxyWeb);
            for (Object webInfos : web.getValue()) {
                Map<String, String> info = (Map) webInfos;
                if (null != info.get(ProxyValidator.WEB_PROXY_URL)) {
                    proxyWeb.setWebURL(info.get(ProxyValidator.WEB_PROXY_URL));
                }
                if (null != info.get(ProxyValidator.WEB_PROXY_XPATH)) {
                    proxyWeb.setValidatorXpath(info.get(ProxyValidator.WEB_PROXY_XPATH));
                }
                if (null != info.get(ProxyValidator.WEB_PROXY_VALUE)) {
                    proxyWeb.setXpathValue(info.get(ProxyValidator.WEB_PROXY_VALUE));
                }
            }
        }
    }

    /**
     * 抓取代理任务
     *
     * @param lightChaserConfig light chaser config
     */
    public void crawlProxy(Map lightChaserConfig, Map proxyConfig) {
        initTemplateRootPath(lightChaserConfig);
        CrawlProxyThread crawlProxyThread = new CrawlProxyThread(lightChaserConfig, proxyConfig);

        ThreadFactory thread = new ThreadFactoryBuilder().setNameFormat("crawl-proxy-thread").build();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), thread, new ThreadPoolExecutor.AbortPolicy());

        singleThreadPool.execute(crawlProxyThread);
    }

    /**
     * 验证代理
     */
    public void validateProxy(Map proxyConfig) {
        ThreadFactory thread = new ThreadFactoryBuilder().setNameFormat("validate-proxy-thread").build();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), thread, new ThreadPoolExecutor.AbortPolicy());
        singleThreadPool.execute(new ValidateProxyThread(proxyConfig));
    }

    public static void main(String[] args) {

        Map lightChaserConfig = YamlConfig.readLightChaserConfig();
        Map proxyConfig = YamlConfig.readProxyConfig();

        Photon photon = new Photon();
        photon.initProxyWeb(proxyConfig);
        log.info("抓取代理服务启动...");
        photon.crawlProxy(lightChaserConfig, proxyConfig);
        log.info("验证代理服务启动...");
        photon.validateProxy(proxyConfig);
//        Radiator radiator = new Radiator();
//        radiator.run();
    }
}