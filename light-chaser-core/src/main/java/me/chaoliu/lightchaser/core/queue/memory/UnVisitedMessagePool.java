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

package me.chaoliu.lightchaser.core.queue.memory;

import lombok.Getter;
import me.chaoliu.lightchaser.core.crawl.CrawlerMessage;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * not visited message pool which contains these messages have not been crawled.
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class UnVisitedMessagePool {

    @Getter
    private Queue<CrawlerMessage> msgQueue = new ConcurrentLinkedQueue<>();

    // 提前加载
    private static UnVisitedMessagePool unVisitedMsgPool = new UnVisitedMessagePool();

    private UnVisitedMessagePool() {
    }

    /**
     * 双重校验锁是不好方式
     *
     * @return unVisitedMsgPool instance
     */
    @Deprecated
//    public static UnVisitedMessagePool getInstance() {
//        if (unVisitedMsgPool == null) {
//            synchronized (UnVisitedMessagePool.class) {
//                if (unVisitedMsgPool == null) {
//                    unVisitedMsgPool = new UnVisitedMessagePool();
//                    return unVisitedMsgPool;
//                } else {
//                    return unVisitedMsgPool;
//                }
//            }
//        } else {
//            return unVisitedMsgPool;
//        }
//    }

    public static UnVisitedMessagePool getInstance(){
        return unVisitedMsgPool;
    }

    /**
     * add request message to pool
     *
     * @param crawlerMsg crawler message
     */
    public void addCrawlerMessage(CrawlerMessage crawlerMsg) {
        msgQueue.add(crawlerMsg);
    }

    /**
     * batch add request messages to pool
     *
     * @param crawlerMsg list of crawler messages
     */
    public void addCrawlerMessage(List<CrawlerMessage> crawlerMsg) {
        msgQueue.addAll(crawlerMsg);
    }

    /**
     * get one message from message pool
     *
     * @return crawler message
     */
    public CrawlerMessage getCrawlerMessage() {
        return msgQueue.poll();
    }

    /**
     * check whether request queue is empty or not.
     *
     * @return <tt>true</tt> if message pool contains no messages
     */
    public boolean isEmpty() {
        return msgQueue.isEmpty();
    }
}
