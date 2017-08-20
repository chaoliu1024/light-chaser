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

package group.chaoliu.lightchaser.core.queue;

import group.chaoliu.lightchaser.core.crawl.CrawlerMessage;
import lombok.Getter;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * not visited message pool which contains these messages have not been crawled.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class RAMMessagePool implements IMessagePool {

    @Getter
    private Queue<CrawlerMessage> msgQueue = new ConcurrentLinkedQueue<>();

    // 提前加载
    private static RAMMessagePool memoryMessagePool = new RAMMessagePool();

    private RAMMessagePool() {
    }

    public static RAMMessagePool getInstance() {
        return memoryMessagePool;
    }

    @Override
    public void addMessage(CrawlerMessage crawlerMsg) {
        msgQueue.add(crawlerMsg);
    }

    @Override
    public void addMessage(List<CrawlerMessage> crawlerMsg) {
        msgQueue.addAll(crawlerMsg);
    }

    @Override
    public CrawlerMessage getMessage() {
        return msgQueue.poll();
    }

    @Override
    public boolean isEmpty() {
        return msgQueue.isEmpty();
    }

}