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
import group.chaoliu.lightchaser.core.crawl.CrawlSpeedController;
import group.chaoliu.lightchaser.core.daemon.photon.Radiator;
import group.chaoliu.lightchaser.core.filter.BloomFilter;
import group.chaoliu.lightchaser.mq.IMessagePool;
import lombok.Getter;
import lombok.Setter;

/**
 * Flection Space Time (弯曲时空)
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class FlectionSpaceTime {

    @Getter
    @Setter
    private CrawlSpeedController speedController;

    @Getter
    @Setter
    private BloomFilter<String> bloomFilter;

    @Getter
    @Setter
    private IMessagePool messagePool;

    @Getter
    @Setter
    private Category category;

    @Getter
    @Setter
    private boolean isLocal;

    /**
     * 获取所有的任务，利用Redis Key获取，Reids Key的个数
     */
    public void allFlectionSpaceTime() {

    }
}