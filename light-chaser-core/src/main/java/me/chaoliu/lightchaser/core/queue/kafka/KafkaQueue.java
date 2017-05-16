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

package me.chaoliu.lightchaser.core.queue.kafka;

import me.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import me.chaoliu.lightchaser.core.queue.AbstractCrawlerQueue;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class KafkaQueue extends AbstractCrawlerQueue {


    @Override
    public void addMessage() {

    }

    @Override
    public CrawlerMessage getMessage() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
