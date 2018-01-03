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

package group.chaoliu.lightchaser.mq;


import group.chaoliu.lightchaser.common.queue.message.QueueMessage;

import java.util.List;

/**
 * TODO
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class KafkaMessagePool implements IMessagePool {

    @Override
    public void addMessage(QueueMessage msg) {

    }

    @Override
    public void addMessage(List<QueueMessage> msgs) {

    }

    @Override
    public QueueMessage getMessage(String key) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}