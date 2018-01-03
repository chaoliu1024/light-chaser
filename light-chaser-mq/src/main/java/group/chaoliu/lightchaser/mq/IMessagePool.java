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

package group.chaoliu.lightchaser.mq;


import group.chaoliu.lightchaser.common.queue.message.QueueMessage;

import java.util.List;

/**
 * message pool
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public interface IMessagePool {

    /**
     * Add one message to pool.
     *
     * @param msg message
     */
    void addMessage(QueueMessage msg);

    /**
     * Batch add messages to pool.
     *
     * @param msg list of messages
     */
    void addMessage(List<QueueMessage> msg);

    /**
     * Get one message from message pool.
     *
     * @return message
     */
    QueueMessage getMessage(String key);

    /**
     * Check whether request queue is empty or not.
     *
     * @return <tt>true</tt> if message pool contains no messages
     */
    boolean isEmpty();

}