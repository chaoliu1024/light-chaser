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

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.config.Constants;
import group.chaoliu.lightchaser.common.config.YamlConfig;
import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.core.crawl.CrawlSpeedController;
import group.chaoliu.lightchaser.core.crawl.template.SeedMsgTemplate;
import group.chaoliu.lightchaser.core.daemon.photon.Radiator;
import group.chaoliu.lightchaser.core.daemon.planet.Planet;
import group.chaoliu.lightchaser.core.daemon.star.Star;
import group.chaoliu.lightchaser.core.filter.RAMBloomFilter;
import group.chaoliu.lightchaser.mq.RAMMessagePool;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    public List<QueueMessage> initSeedMsgs(Category category) {
        SeedMsgTemplate seedMsgTemplate = new SeedMsgTemplate();
        return seedMsgTemplate.initSeedMsgs(category);
    }

    public void initFlectionSpaceTime(Category category) {
        flectionST = new FlectionSpaceTime();
        flectionST.setLocal(true);
        flectionST.setCategory(category);
        flectionST.setSpeedController(new CrawlSpeedController());
        flectionST.setMessagePool(RAMMessagePool.getInstance());
        flectionST.setBloomFilter(new RAMBloomFilter<>(0.0001, 100000));
    }

    public void shutdown() {
    }

    public void submitFlection(Map lightChaserConfig, FlectionSpaceTime flectionST) {
        initTemplateRootPath(lightChaserConfig);
        List<QueueMessage> initMsgs = initSeedMsgs(flectionST.getCategory());
        flectionST.getMessagePool().addMessage(initMsgs);
        planet = new Planet();
        if (!Objects.equals("proxy", flectionST.getCategory().getType())
                && (boolean) lightChaserConfig.get(Constants.PROXY_USED)) {
            planet.connectProxyServer();
        }
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
        Category category = new Category(type, name);
        Map lightChaserConfig = YamlConfig.readLightChaserConfig();
        LocalDaemon localDaemon = new LocalDaemon();

        Star star = new Star();
        star.run(category, lightChaserConfig);

        localDaemon.initFlectionSpaceTime(category);
        localDaemon.submitFlection(lightChaserConfig, localDaemon.flectionST);
    }

}