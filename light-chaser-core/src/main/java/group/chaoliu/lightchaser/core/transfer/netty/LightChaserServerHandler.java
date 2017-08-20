///*
// * Copyright (c) 2017, Chao Liu (chaoliu1024@gmail.com). All rights reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package group.chaoliu.lightchaser.core.transfer.netty;
//
//import group.chaoliu.lightchaser.core.daemon.Job;
//import group.chaoliu.lightchaser.core.protocol.http.RequestMessage;
//import group.chaoliu.lightchaser.core.transfer.netty.protobuf.CrawlerMsgProto;
//import group.chaoliu.lightchaser.core.transfer.netty.protobuf.HeartbeatProto;
//import group.chaoliu.lightchaser.core.transfer.netty.protobuf.MessageProto;
//import io.netty.channel.ChannelHandlerAdapter;
//import io.netty.channel.ChannelHandlerContext;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author chao liu
// * @since Light Chaser 0.0.1
// */
//public class LightChaserServerHandler extends ChannelHandlerAdapter {
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg)
//            throws Exception {
//        MessageProto.Message receive = (MessageProto.Message) msg;
//
//        CrawlerMsgProto.CrawlerMsg body = receive.getCrawlerMsg();
//
//        HeartbeatProto.Heartbeat heartbeat = receive.getHeartbeat();
//
//        if (!body.getAllFields().isEmpty()) {
//            System.out.println("url level is: " + body.getURLLevel());
//            System.out.println("job name is: " + body.getJob().getName());
//            System.out.println("job type is: " + body.getJob().getType());
//
//            System.out.println("url is: " + body.getRequestMessage().getURL());
//            System.out.println("is post request: " + body.getRequestMessage().getIsPostRequest());
//            System.out.println();
//            Job job = new Job("OTA", "tuniu");
//            RequestMessage requestMessage = new RequestMessage();
//            requestMessage.setURL("www.tuniu.com");
//            requestMessage.setPostRequest(true);
//            Map<String, String> head = new HashMap<>();
//            head.put("userAgent", "tuniu");
//            requestMessage.setHeaders(head);
//            Map<String, String> cookie = new HashMap<>();
//            cookie.put("host", "127.0.0.1");
//            requestMessage.setCookie(cookie);
//            CrawlerMsgProto.CrawlerMsg crawlerMsg = MessageUtil.crawlerMsg(200, job, requestMessage);
//            MessageProto.Message message = MessageUtil.message(crawlerMsg);
//
//            ctx.writeAndFlush(message);
//        }
//
//        if (!heartbeat.getAllFields().isEmpty()) {
//            System.out.println("code is: " + heartbeat.getCode());
//            System.out.println("HeapMemory is: " + heartbeat.getHeapMemory());
//            System.out.println("port is: " + heartbeat.getPort());
//            System.out.println();
//            HeartbeatProto.Heartbeat beartbeat = MessageUtil.beartbeat(3, 22222, 1020);
//            MessageProto.Message message = MessageUtil.message(beartbeat);
//
//            ctx.writeAndFlush(message);
//        }
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        ctx.close();
//    }
//
//}
