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

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.config.ProxyConstants;
import group.chaoliu.lightchaser.core.crawl.template.Template;
import group.chaoliu.lightchaser.core.daemon.LocalDaemon;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Crawl proxy thread.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class CrawlProxyThread implements Runnable {

    private Map lightChaserConfig;

    private Map proxyConfig;

    private int intervalHour = 2;

    public CrawlProxyThread(Map lightChaserConfig, Map proxyConfig) {
        this.lightChaserConfig = lightChaserConfig;
        this.proxyConfig = proxyConfig;

        Object hour = proxyConfig.get(ProxyConstants.PROXY_CRAWL_INTERVAL_HOUR);
        if (null != hour && hour instanceof Integer) {
            intervalHour = (int) hour;
        }
    }

    @Override
    public void run() {
        while (true) {
            log.info("线程中抓取代理...");
            List<String> proxies = initProxyJob();
            for (String proxy : proxies) {
                Category category = new Category("proxy", proxy);
                // TODO 这里设计的非常有问题，不应该每个任务都 new LocalDaemon();
                LocalDaemon localDaemon = new LocalDaemon();
                localDaemon.initFlectionSpaceTime(category);
                localDaemon.submitFlection(lightChaserConfig, localDaemon.flectionST);
            }

            try {
                TimeUnit.HOURS.sleep(intervalHour);
                log.info("抓取线程休眠中...");
            } catch (InterruptedException e) {
                log.error("sleep error...");
            }
        }
    }

    public List<String> initProxyJob() {
        String proxySites = Template.templateRootPath + "proxy" + File.separator + "proxy_sites.xml";
        File file = new File(proxySites);
        SAXReader reader = new SAXReader();
        List<String> proxies = new ArrayList<>();
        try {
            Document document = reader.read(file);
            Element root = document.getRootElement();

            @SuppressWarnings("unchecked")
            List<Node> sites = root.selectNodes("/proxy_sites/site");
            for (Node site : sites) {
                Element ele = (Element) site;
                if ("true".endsWith(ele.attributeValue("enable"))) {
                    proxies.add(ele.getTextTrim());
                }
            }
        } catch (DocumentException e) {
            log.error("Read proxy_sites.xml error: {}", e);
        }
        return proxies;
    }
}
