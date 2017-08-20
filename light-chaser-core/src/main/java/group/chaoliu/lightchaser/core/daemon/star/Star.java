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

package group.chaoliu.lightchaser.core.daemon.star;

import group.chaoliu.lightchaser.core.config.Constants;
import group.chaoliu.lightchaser.core.config.LoadConfig;
import group.chaoliu.lightchaser.core.daemon.Deamon;
import group.chaoliu.lightchaser.core.daemon.FlectionSpaceTime;
import group.chaoliu.lightchaser.core.daemon.Job;
import group.chaoliu.lightchaser.core.daemon.LocalDaemon;
import group.chaoliu.lightchaser.core.fission.common.service.SiteService;
import group.chaoliu.lightchaser.core.persistence.ImageStore;
import group.chaoliu.lightchaser.core.persistence.hbase.HBaseClient;
import group.chaoliu.lightchaser.core.persistence.hbase.StarJobFamily;
import group.chaoliu.lightchaser.core.util.SpringBeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;

/**
 * 恒星，光的产生，相当于一个处理worker
 * Star interface to start crawler
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class Star extends Deamon {

    private SiteService siteService = SpringBeanUtil.siteServiceBean();

    /**
     * 初始化job的相关信息
     */
    public void init(Job job, Map config) {
        log.info("Start {} job...", job.getType());
        if ((boolean) config.get(Constants.STORE_WEB_PAGE)) {
            HBaseClient hbase;
            try {
                hbase = new HBaseClient(job);
                hbase.createTable(job.getType(), StarJobFamily.HBASE_FAMILIES, false);
            } catch (IOException e) {
                log.error("create HBase table {} error! error info: {}", job.getType(), e);
            }
        }
        ImageStore.BASEPATH = config.get(Constants.IMAGE_BASEPATH).toString();

        if ((boolean) config.get(Constants.STORE_MYSQL)) {
            siteService.siteLog(job);
        }
    }

    public static void main(String[] args) {

        if (null == args || args.length < 1) {
            throw new InvalidParameterException("need to input job name");
        }

        String type = args[0];
        String name = args[1];

        Job job = new Job(type, name);

        Map lightChaserConfig = LoadConfig.readLightChaserConfig();

        // TODO 一个job在一个线程里面跑
        if (!(boolean) lightChaserConfig.get(Constants.LIGHT_CHASER_CLUSTER_MODE)) {

            FlectionSpaceTime flectionTS = new FlectionSpaceTime();

            flectionTS.setJob(job);

            LocalDaemon localCluster = new LocalDaemon();
            localCluster.submitFlection(lightChaserConfig, flectionTS);
        }
    }
}