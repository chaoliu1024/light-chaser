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
//import group.chaoliu.lightchaser.core.transfer.netty.protobuf.CrawlerMsgProto;
//import group.chaoliu.lightchaser.core.transfer.netty.protobuf.HeartbeatProto;
//import group.chaoliu.lightchaser.core.transfer.netty.protobuf.MessageProto;
//import io.netty.channel.ChannelHandlerAdapter;
//import io.netty.channel.ChannelHandlerContext;
//
///**
// * @author chao liu
// * @since Light Chaser 0.0.1
// */
//public class LightChaserClientHandler<T> extends ChannelHandlerAdapter {
//
//    ChannelHandlerContext ctx;
//
//    public void send(T msg) {
//        if (null != ctx) {
//            ctx.writeAndFlush(msg);
//        } else {
//            System.out.println("can not send message");
//        }
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
//        ctx.close();
//    }
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        this.ctx = ctx;
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//
//        MessageProto.Message message = (MessageProto.Message) msg;
//
//        CrawlerMsgProto.CrawlerMsg body = message.getCrawlerMsg();
//        HeartbeatProto.Heartbeat heartbeat = message.getHeartbeat();
//
//        System.out.println("收到服务端反馈...");
//
//        if (!body.getAllFields().isEmpty()) {
//            System.out.println("url level is: " + body.getURLLevel());
//            System.out.println("job name is: " + body.getJob().getName());
//            System.out.println("job type is: " + body.getJob().getType());
//            System.out.println("url is: " + body.getRequestMessage().getURL());
//            System.out.println("is post request: " + body.getRequestMessage().getIsPostRequest());
//            System.out.println();
//        }
//
//        if (!heartbeat.getAllFields().isEmpty()) {
//            System.out.println("code is: " + heartbeat.getCode());
//            System.out.println("HeapMemory is: " + heartbeat.getHeapMemory());
//            System.out.println("port is: " + heartbeat.getPort());
//            System.out.println();
//        }
//
//    }
//}