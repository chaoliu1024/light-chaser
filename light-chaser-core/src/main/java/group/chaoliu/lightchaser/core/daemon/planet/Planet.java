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

import group.chaoliu.lightchaser.core.config.LoadConfig;
import group.chaoliu.lightchaser.core.crawl.Crawler;
import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.crawl.SiteSpeed;
import group.chaoliu.lightchaser.core.crawl.template.CrawlTemplate;
import group.chaoliu.lightchaser.core.crawl.template.JobTemplate;
import group.chaoliu.lightchaser.core.crawl.template.TemplateCache;
import group.chaoliu.lightchaser.core.crawl.template.WrapperTemplate;
import group.chaoliu.lightchaser.core.daemon.Deamon;
import group.chaoliu.lightchaser.core.daemon.FlectionSpaceTime;
import group.chaoliu.lightchaser.core.daemon.Job;
import group.chaoliu.lightchaser.core.queue.IMessagePool;
import group.chaoliu.lightchaser.core.util.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class Planet extends Deamon {

    private Crawler crawler = SpringBeanUtil.crawlerBean();

    public static TemplateCache templateCache = new TemplateCache();

    // light-chaser.yaml
    private Map lightChaserConfig;

    private FlectionSpaceTime flectionST;

    /**
     * 环绕-->工作任务开启
     */
    public void encircle(FlectionSpaceTime flectionST) {

        lightChaserConfig = LoadConfig.readLightChaserConfig();
        this.flectionST = flectionST;

        loadTemplate(flectionST.getJob());
        siteSpeed(flectionST.getJob());

        crawler.setConfig(lightChaserConfig);
        crawler.setFlectionST(flectionST);

        if (flectionST.isLocal()) {
            // 本地模式
            IMessagePool pool = flectionST.getMessagePool();
            while (!pool.isEmpty()) {
                CrawlerMessage crawlerMgs = pool.getMessage();
                crawler.run(crawlerMgs);
            }
        } else {
            // 分布式模式
        }
    }

    /**
     * load craw_path.xml and wrapper.xml template file
     *
     * @param job job
     */
    public void loadTemplate(Job job) {
        log.info("Load the {} template file.", job);
        // instance crawl_path.xml template jobName
        CrawlTemplate crawlTemplate = new CrawlTemplate(job);
        // instance wrapper.xml template file
        WrapperTemplate wrapperTemplate = new WrapperTemplate(job);

        JobTemplate template = new JobTemplate(job, crawlTemplate, wrapperTemplate);
        templateCache.putSiteTemplate(job, template);
    }

    /**
     * initialize the initial speed of one site
     *
     * @param job job
     */
    public void siteSpeed(Job job) {
        CrawlTemplate crawlConfig = templateCache.getTemplates().get(job).getCrawlTemplate();
        int minInterval = crawlConfig.getMinInterval();
        int maxInterval = crawlConfig.getMaxInterval();
        SiteSpeed speed = new SiteSpeed(job, minInterval, maxInterval);
        flectionST.getSpeedController().setSiteSpeed(job, speed);
        log.info("Set the {} speed finished. The min interval is {}, " +
                "and the max interval is {}", job, minInterval, maxInterval);
    }

    public static void main(String[] args) {

        Planet planet = new Planet();


    }
}