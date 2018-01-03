/*
 * Copyright (c) 2016, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
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

package group.chaoliu.lightchaser.core.daemon.planet;

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.config.YamlConfig;
import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.core.crawl.Crawler;
import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.crawl.SiteSpeed;
import group.chaoliu.lightchaser.core.crawl.template.CategoryTemplate;
import group.chaoliu.lightchaser.core.crawl.template.CrawlTemplate;
import group.chaoliu.lightchaser.core.crawl.template.TemplateCache;
import group.chaoliu.lightchaser.core.crawl.template.WrapperTemplate;
import group.chaoliu.lightchaser.core.daemon.Deamon;
import group.chaoliu.lightchaser.core.daemon.FlectionSpaceTime;
import group.chaoliu.lightchaser.core.util.SpringBeanUtil;
import group.chaoliu.lightchaser.mq.IMessagePool;
import group.chaoliu.lightchaser.rpc.netty.dispatch.client.NettyClient;
import group.chaoliu.lightchaser.rpc.netty.proxy.ProxySocket;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 一个处理worker
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class Planet extends Deamon {

    private Crawler crawler = SpringBeanUtil.crawlerBean();

    public static TemplateCache templateCache = new TemplateCache();

    /**
     * light-chaser.yaml
     */
    private Map lightChaserConfig;

    private FlectionSpaceTime flectionST;

    private NettyClient nettyClient;

    /**
     * 获取代理的客户端
     */
    private ProxySocket proxyClient;

    /**
     * 环绕-->工作任务开启
     */
    public void encircle(FlectionSpaceTime flectionST) {

        lightChaserConfig = YamlConfig.readLightChaserConfig();
        this.flectionST = flectionST;

        Category category = flectionST.getCategory();
        loadTemplate(category);
        siteSpeed(category);

        crawler.setConfig(lightChaserConfig);
        crawler.setFlectionST(flectionST);
        crawler.setProxyClient(proxyClient);

        if (flectionST.isLocal()) {
            // 本地模式
            IMessagePool pool = flectionST.getMessagePool();
            while (!pool.isEmpty()) {
                QueueMessage msg = pool.getMessage(category.key());
                CrawlerMessage crawlerMgs = new CrawlerMessage();
                crawlerMgs.setQueueMessage(msg);
                crawler.run(crawlerMgs);
            }
        } else {
            // 分布式模式
        }
    }

    /**
     * load craw_path.xml and wrapper.xml template file
     *
     * @param category job
     */
    public void loadTemplate(Category category) {
        log.info("Load the {} template file.", category);
        // instance crawl_path.xml template jobName
        CrawlTemplate crawlTemplate = new CrawlTemplate(category);
        // instance wrapper.xml template file
        WrapperTemplate wrapperTemplate = new WrapperTemplate(category);

        CategoryTemplate template = new CategoryTemplate(category, crawlTemplate, wrapperTemplate);
        templateCache.putCategoryTemplate(category, template);
    }

    /**
     * initialize the initial speed of one site
     *
     * @param category job
     */
    public void siteSpeed(Category category) {
        CrawlTemplate crawlConfig = templateCache.getTemplates().get(category).getCrawlTemplate();
        int minInterval = crawlConfig.getMinInterval();
        int maxInterval = crawlConfig.getMaxInterval();
        SiteSpeed speed = new SiteSpeed(category, minInterval, maxInterval);
        flectionST.getSpeedController().setSiteSpeed(category, speed);
        log.info("Set the {} speed finished. The min interval is {}, " +
                "and the max interval is {}", category, minInterval, maxInterval);
    }

    public void connectProxyServer() {
        Map proxyConfig = YamlConfig.readProxyConfig();
        this.proxyClient = new ProxySocket(proxyConfig);
        Thread thread = new Thread(this.proxyClient);
        thread.start();
        while (true) {
            if (null != this.proxyClient.getFuture()) {
                if (this.proxyClient.getFuture().isSuccess()) {
                    break;
                }
            }
        }
        log.info("Proxy client has been connected with server...");
    }

    public static void main(String[] args) {

        Planet planet = new Planet();
        planet.connectProxyServer();

    }
}