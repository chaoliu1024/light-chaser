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

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.core.daemon.Deamon;
import group.chaoliu.lightchaser.core.daemon.Initiator;
import group.chaoliu.lightchaser.core.fission.common.service.SiteService;
import group.chaoliu.lightchaser.core.util.SpringBeanUtil;
import group.chaoliu.lightchaser.rpc.netty.dispatch.server.NettyServer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 恒星，光的产生，相当于Master
 * Star interface to start crawler
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class Star extends Deamon {

    private SiteService siteService = SpringBeanUtil.siteServiceBean();

    private NettyServer nettyServer;

    public void initServer() {
        this.nettyServer = new NettyServer();
        try {
            this.nettyServer.startServer(8080);
        } catch (Exception e) {
            log.error("start netty server failed... {}", e);
        }
    }

    public void run(Category category, Map lightChaserConfig) {
        Initiator initiator = new Initiator();
        initiator.init(category, lightChaserConfig);
    }

    public static void main(String[] args) {
        Star star = new Star();
        star.initServer();
    }
}