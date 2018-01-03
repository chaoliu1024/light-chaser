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

package group.chaoliu.lightchaser.core.crawl;

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.config.YamlConfig;
import group.chaoliu.lightchaser.common.protocol.http.RequestMessage;
import group.chaoliu.lightchaser.common.protocol.http.ResponseMessage;
import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.core.crawl.template.CrawlTemplate;
import group.chaoliu.lightchaser.core.crawl.template.Template;
import group.chaoliu.lightchaser.core.daemon.FlectionSpaceTime;
import group.chaoliu.lightchaser.core.daemon.LocalDaemon;
import group.chaoliu.lightchaser.core.daemon.photon.Radiator;
import group.chaoliu.lightchaser.core.daemon.planet.Planet;
import group.chaoliu.lightchaser.core.filter.RAMBloomFilter;
import group.chaoliu.lightchaser.mq.RAMMessagePool;
import org.junit.Test;

import java.util.Map;

/**
 * crawler function test, very important!
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class CrawlerTest {

    @Test
    public void testRun() {

        Template.templateRootPath = "D:\\space-time\\light-chaser\\template\\";

        LocalDaemon localDaemon = new LocalDaemon();
        Crawler crawler = new Crawler();
        Category category = new Category("ota", "ctrip");

        localDaemon.initFlectionSpaceTime(category);

        crawler.setFlectionST(localDaemon.flectionST);

        Planet planet = new Planet();

        planet.loadTemplate(category);

        CrawlTemplate crawlTemplate = Planet.templateCache.getTemplates().get(category).getCrawlTemplate();

        // 设置速度
        int minInterval = crawlTemplate.getMinInterval();
        int maxInterval = crawlTemplate.getMaxInterval();
        SiteSpeed speed = new SiteSpeed(category, minInterval, maxInterval);
        localDaemon.flectionST.getSpeedController().setSiteSpeed(category, speed);

        CrawlerMessage crawlerMessage = new CrawlerMessage();

        ResponseMessage responseMsg = new ResponseMessage();

        QueueMessage queueMessage = new QueueMessage();
        queueMessage.setCategory(category);
        queueMessage.setUrlLevel(100);

        RequestMessage requestMsg = new RequestMessage();
        requestMsg.setHeaders(crawlTemplate.getHeaders());
        requestMsg.setURL("http://taocan.ctrip.com/freetravel/p8793949s12.html?kwd=%E4%B8%89%E4%BA%9A");

        queueMessage.setRequestMsg(requestMsg);

        crawlerMessage.setResponseMsg(responseMsg);
        crawlerMessage.setQueueMessage(queueMessage);

        crawler.run(crawlerMessage);
    }
}
