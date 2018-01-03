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

package group.chaoliu.lightchaser.rpc.netty.dispatch;

import group.chaoliu.lightchaser.common.Category;
import group.chaoliu.lightchaser.common.protocol.http.RequestMessage;
import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.rpc.netty.protobuf.HeartbeatProto;
import group.chaoliu.lightchaser.rpc.netty.protobuf.MessageProto;
import group.chaoliu.lightchaser.rpc.netty.protobuf.QueueMsgProto;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class MessageUtil {

    private static final String MAP_SEPARATOR = "@@@^_^@@@";

    public static QueueMsgProto.QueueMsg protoQueueMsg(QueueMessage queueMsg) {
        MessageProto.Message.Builder message = MessageProto.Message.newBuilder();

        QueueMsgProto.QueueMsg.Builder queueMsgBuilder = message.getQueueMsgBuilder();
        QueueMsgProto.Category.Builder category = QueueMsgProto.Category.newBuilder();
        QueueMsgProto.RequestMessage.Builder requestMessage = QueueMsgProto.RequestMessage.newBuilder();


        Category c = queueMsg.getCategory();
        RequestMessage rMsg = queueMsg.getRequestMsg();

        queueMsgBuilder.setURLLevel(queueMsg.getUrlLevel());

        category.setName(c.getName());
        category.setType(c.getType());
        queueMsgBuilder.setCategory(category);

        requestMessage.setURL(rMsg.getURL());
        requestMessage.setIsPostRequest(rMsg.isPostRequest());

        Map<String, String> headers = rMsg.getHeaders();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            requestMessage.addHeaders(key + MAP_SEPARATOR + value);
        }

        Map<String, String> cookies = rMsg.getCookie();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            requestMessage.addCookie(key + MAP_SEPARATOR + value);
        }

        queueMsgBuilder.setRequestMessage(requestMessage);

        return queueMsgBuilder.build();
    }

    public static HeartbeatProto.Heartbeat protoHeartbeat(int code, int heapMemory, int port) {
        MessageProto.Message.Builder message = MessageProto.Message.newBuilder();
        HeartbeatProto.Heartbeat.Builder heartbeatBuilder = message.getHeartbeatBuilder();
        heartbeatBuilder.setCode(code);
        heartbeatBuilder.setHeapMemory(heapMemory);
        heartbeatBuilder.setPort(port);
        return heartbeatBuilder.build();
    }

    public static MessageProto.Message protoReqMessage() {
        MessageProto.Message.Builder message = MessageProto.Message.newBuilder();
        message.setMsgType(MessageProto.Message.MsgType.REQ_MSG);
        return message.build();
    }

    public static MessageProto.Message protoMessage(QueueMsgProto.QueueMsg queueMsg) {
        MessageProto.Message.Builder message = MessageProto.Message.newBuilder();
        message.setMsgType(MessageProto.Message.MsgType.CRAWL_MSG);
        message.setQueueMsg(queueMsg);
        return message.build();
    }

    public static MessageProto.Message protoMessage(QueueMessage queueMsg) {
        QueueMsgProto.QueueMsg msg = protoQueueMsg(queueMsg);
        MessageProto.Message.Builder message = MessageProto.Message.newBuilder();
        message.setMsgType(MessageProto.Message.MsgType.CRAWL_MSG);
        message.setQueueMsg(msg);
        return message.build();
    }

    public static MessageProto.Message protoHeartBeatMsg(HeartbeatProto.Heartbeat heartbeat) {
        MessageProto.Message.Builder message = MessageProto.Message.newBuilder();
        message.setMsgType(MessageProto.Message.MsgType.HEART_BEAT);
        message.setHeartbeat(heartbeat);
        return message.build();
    }

    public static QueueMessage queueMessageBuilder(QueueMsgProto.QueueMsg queueMsg) {

        QueueMessage qMsg = new QueueMessage();

        int urlLevel = queueMsg.getURLLevel();

        QueueMsgProto.Category category = queueMsg.getCategory();
        Category c = categoryBuilder(category);

        QueueMsgProto.RequestMessage requestMsg = queueMsg.getRequestMessage();
        RequestMessage reqMsg = new RequestMessage();
        reqMsg.setURL(requestMsg.getURL());
        reqMsg.setPostRequest(requestMsg.getIsPostRequest());
        List<String> headersList = requestMsg.getHeadersList();
        if (!headersList.isEmpty()) {
            Map<String, String> map = mapBuilder(headersList);
            reqMsg.setHeaders(map);
        }

        List<String> cookieList = requestMsg.getCookieList();
        if (!cookieList.isEmpty()) {
            Map<String, String> map = mapBuilder(cookieList);
            reqMsg.setCookie(map);
        }

        qMsg.setCategory(c);
        qMsg.setUrlLevel(urlLevel);
        qMsg.setRequestMsg(reqMsg);

        return qMsg;
    }

    public static Category categoryBuilder(QueueMsgProto.Category category) {
        return new Category(category.getType(), category.getName());
    }

    private static Map<String, String> mapBuilder(List<String> list) {

        Map<String, String> map = new HashMap<>();
        for (String header : list) {
            if (StringUtils.isNotEmpty(header)) {
                String[] h = header.split(MAP_SEPARATOR);
                if (h.length == 2) {
                    map.put(h[0], h[1]);
                }
            }
        }
        return map;
    }
}