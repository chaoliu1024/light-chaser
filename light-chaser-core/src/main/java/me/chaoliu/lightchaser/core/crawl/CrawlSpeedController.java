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

package me.chaoliu.lightchaser.core.crawl;

import java.util.HashMap;
import java.util.Map;

/**
 * control the speed of crawling one site
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class CrawlSpeedController {

    // TODO 需要多线程安全
    private Map<String, SiteSpeed> speedCache = new HashMap<>();

    public Map<String, SiteSpeed> getSpeedCache() {
        return this.speedCache;
    }

    // TODO 需要多线程安全？
    public void setSiteSpeed(String site, SiteSpeed speed) {
        speedCache.put(site, speed);
    }

    /**
     * TODO 根据抓取状态，动态控制抓取速率，防止被屏蔽
     */
    public void updateSiteSpeed(){

    }
}