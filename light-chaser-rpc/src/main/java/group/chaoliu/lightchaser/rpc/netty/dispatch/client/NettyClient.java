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

package group.chaoliu.lightchaser.rpc.netty.dispatch.client;

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.protocol.http.RequestMessage;
import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.rpc.netty.dispatch.MessageUtil;
import group.chaoliu.lightchaser.rpc.netty.protobuf.HeartbeatProto;
import group.chaoliu.lightchaser.rpc.netty.protobuf.MessageProto;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class NettyClient {

    private static String host = "127.0.0.1";
    private static int port = 8080;
    private ClientBuilder client;

    public NettyClient() {
        client = new ClientBuilder(host, port);
        client.startClient();
        while (true) {
            if (isStarted()) {
                break;
            }
        }
    }

    public boolean isStarted() {
        return client.isRunning();
    }

    public QueueMessage getCrawlerMsg() {
//        MessageProto.Message message = MessageUtil.requestMsg();
//        client.send(message);
//        return client.getCrawlerMessage();
        return null;
    }

    public void sendHeartbeat() {
        HeartbeatProto.Heartbeat heartbeat = MessageUtil.protoHeartbeat(1, 111111, 8090);
        MessageProto.Message msg = MessageUtil.protoHeartBeatMsg(heartbeat);
        System.out.println("发送心跳消息.....");
        client.send(msg);
    }

    public void sendCrawlMsg() {
        RequestMessage requestMessage = RequestMessage.requestMsgDemo(1);
        Category category = new Category("ota", "tuniu");
        QueueMessage qMsg = new QueueMessage();
        qMsg.setCategory(category);
        qMsg.setUrlLevel(100);
        qMsg.setRequestMsg(requestMessage);
        MessageProto.Message msg = MessageUtil.protoMessage(qMsg);
        client.send(msg);
    }

    public void sendReqMsg() {
        MessageProto.Message message = MessageUtil.protoReqMessage();
        client.send(message);
    }

    public static void main(String[] args) {
        NettyClient client = new NettyClient();
//        client.sendCrawlMsg();
        client.sendReqMsg();
    }
}