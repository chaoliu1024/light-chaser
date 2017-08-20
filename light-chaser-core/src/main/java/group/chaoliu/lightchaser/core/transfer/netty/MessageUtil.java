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
//
///**
// * @author chao liu
// * @since Light Chaser 0.0.1
// */
//public class MessageUtil {
//
//    public static CrawlerMsgProto.CrawlerMsg crawlerMsg(int level, Job j, RequestMessage rmsg) {
//        MessageProto.Message.Builder message = MessageProto.Message.newBuilder();
//
//        CrawlerMsgProto.CrawlerMsg.Builder crawlerBuilder = message.getCrawlerMsgBuilder();
//        CrawlerMsgProto.Job.Builder job = CrawlerMsgProto.Job.newBuilder();
//        CrawlerMsgProto.RequestMessage.Builder requestMessage = CrawlerMsgProto.RequestMessage.newBuilder();
//
//        crawlerBuilder.setURLLevel(level);
//
//        job.setName(j.getName());
//        job.setType(j.getType());
//        crawlerBuilder.setJob(job);
//
//        requestMessage.setURL(rmsg.getURL());
//        requestMessage.setIsPostRequest(rmsg.isPostRequest());
//        requestMessage.putAllHeaders(rmsg.getHeaders());
//        requestMessage.putAllCookie(rmsg.getCookie());
//        crawlerBuilder.setRequestMessage(requestMessage);
//
//        return crawlerBuilder.build();
//    }
//
//    public static HeartbeatProto.Heartbeat beartbeat(int code, int heapMemory, int port) {
//        MessageProto.Message.Builder message = MessageProto.Message.newBuilder();
//        HeartbeatProto.Heartbeat.Builder heartbeatBuilder = message.getHeartbeatBuilder();
//        heartbeatBuilder.setCode(code);
//        heartbeatBuilder.setHeapMemory(heapMemory);
//        heartbeatBuilder.setPort(port);
//        return heartbeatBuilder.build();
//    }
//
//    public static MessageProto.Message message(CrawlerMsgProto.CrawlerMsg crawlerMsg) {
//        MessageProto.Message.Builder message = MessageProto.Message.newBuilder();
//        message.setCrawlerMsg(crawlerMsg);
//
//        return message.build();
//    }
//
//    public static MessageProto.Message message(HeartbeatProto.Heartbeat heartbeat) {
//        MessageProto.Message.Builder message = MessageProto.Message.newBuilder();
//        message.setHeartbeat(heartbeat);
//        return message.build();
//    }
//
//}