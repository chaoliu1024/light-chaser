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

package group.chaoliu.lightchaser.core.queue;

import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;

import java.util.List;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public interface IMessagePool {

    /**
     * Add one crawler message to pool.
     *
     * @param crawlerMsg crawler message
     */
    void addMessage(CrawlerMessage crawlerMsg);

    /**
     * Batch add crawler messages to pool.
     *
     * @param crawlerMsg list of crawler messages
     */
    void addMessage(List<CrawlerMessage> crawlerMsg);

    /**
     * Get one message from message pool.
     *
     * @return crawler message
     */
    CrawlerMessage getMessage();

    /**
     * Check whether request queue is empty or not.
     *
     * @return <tt>true</tt> if message pool contains no messages
     */
    boolean isEmpty();

}