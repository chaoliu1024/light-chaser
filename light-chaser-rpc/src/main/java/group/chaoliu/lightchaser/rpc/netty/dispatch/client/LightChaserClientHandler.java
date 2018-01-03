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

import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.rpc.netty.dispatch.MessageUtil;
import group.chaoliu.lightchaser.rpc.netty.protobuf.MessageProto;
import group.chaoliu.lightchaser.rpc.netty.protobuf.QueueMsgProto;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class LightChaserClientHandler extends ChannelHandlerAdapter {

    private ChannelHandlerContext ctx;

    private Queue<QueueMessage> msgQueue;

    public LightChaserClientHandler(Queue<QueueMessage> msgQueue) {
        this.msgQueue = msgQueue;
    }

    public void send(MessageProto.Message msg) {
        if (null != ctx) {
            ctx.writeAndFlush(msg);
        } else {
            log.info("can not send message");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        MessageProto.Message message = (MessageProto.Message) msg;
        MessageProto.Message.MsgType msgType = message.getMsgType();
        if (null != msgType) {
            if (msgType == MessageProto.Message.MsgType.CRAWL_MSG) {
                log.info("服务器返回爬取消息");
                System.out.println("服务器返回爬取消息");
                QueueMsgProto.QueueMsg queueMsg = message.getQueueMsg();
                QueueMessage qMsg = MessageUtil.queueMessageBuilder(queueMsg);
                System.out.println(qMsg);
                this.msgQueue.add(qMsg);
            } else if (msgType == MessageProto.Message.MsgType.HEART_BEAT) {
                log.info("服务器返回心跳消息");
            }
        }
    }
}