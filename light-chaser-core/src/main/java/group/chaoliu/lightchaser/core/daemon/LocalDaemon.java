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

package group.chaoliu.lightchaser.core.daemon;

import group.chaoliu.lightchaser.core.config.LoadConfig;
import group.chaoliu.lightchaser.core.crawl.CrawlSpeedController;
import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import group.chaoliu.lightchaser.core.crawl.template.SeedMsgTemplate;
import group.chaoliu.lightchaser.core.daemon.photon.Radiator;
import group.chaoliu.lightchaser.core.daemon.planet.Planet;
import group.chaoliu.lightchaser.core.daemon.star.Star;
import group.chaoliu.lightchaser.core.filter.RAMBloomFilter;
import group.chaoliu.lightchaser.core.queue.RAMMessagePool;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class LocalDaemon extends Deamon {

    public FlectionSpaceTime flectionST;

    @Getter
    @Setter
    private Star star;

    @Getter
    @Setter
    private Planet planet;

    public List<CrawlerMessage> initSeedMsgs(Job job) {
        SeedMsgTemplate seedMsgTemplate = new SeedMsgTemplate();
        return seedMsgTemplate.initSeedMsgs(job);
    }

    public void initSpaceTime(Job job) {
        flectionST = new FlectionSpaceTime();
        flectionST.setLocal(true);
        flectionST.setJob(job);
        flectionST.setSpeedController(new CrawlSpeedController());
        flectionST.setMessagePool(RAMMessagePool.getInstance());
        flectionST.setBloomFilter(new RAMBloomFilter<>(0.0000001, Integer.MAX_VALUE));
        flectionST.setRadiator(new Radiator());
    }

    public void shutdown() {
        flectionST.getRadiator().shutdown();
    }

    public void submitFlection(Map lightChaserConfig, FlectionSpaceTime flectionST) {
        initTemplateRootPath(lightChaserConfig);
        List<CrawlerMessage> initMsgs = initSeedMsgs(flectionST.getJob());
        flectionST.getMessagePool().addMessage(initMsgs);
        planet = new Planet();
        planet.encircle(flectionST);
        shutdown();
        log.info("job finish...");
    }

    public static void usage() {
        System.out.println("Job type or name is empty.\t Please input...");
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Invalid parameter");
            usage();
            return;
        }
        String type = args[0];
        String name = args[1];
        Job job = new Job(type, name);
        Map lightChaserConfig = LoadConfig.readLightChaserConfig();
        LocalDaemon localDaemon = new LocalDaemon();
        localDaemon.star = new Star();
        localDaemon.star.init(job, lightChaserConfig);
        localDaemon.initSpaceTime(job);
        localDaemon.submitFlection(lightChaserConfig, localDaemon.flectionST);
    }
}