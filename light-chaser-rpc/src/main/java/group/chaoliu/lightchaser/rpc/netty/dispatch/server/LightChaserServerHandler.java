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

package group.chaoliu.lightchaser.rpc.netty.dispatch.server;

import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.common.util.SerializeUtil;
import group.chaoliu.lightchaser.mq.redis.RedisPool;
import group.chaoliu.lightchaser.rpc.netty.dispatch.MessageUtil;
import group.chaoliu.lightchaser.rpc.netty.protobuf.HeartbeatProto;
import group.chaoliu.lightchaser.rpc.netty.protobuf.MessageProto;
import group.chaoliu.lightchaser.rpc.netty.protobuf.QueueMsgProto;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.Set;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class LightChaserServerHandler extends ChannelHandlerAdapter {

    private Jedis redis = null;

    public LightChaserServerHandler() {
        this.redis = RedisPool.redis();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        MessageProto.Message receive = (MessageProto.Message) msg;

        MessageProto.Message.MsgType msgType = receive.getMsgType();

        if (msgType == MessageProto.Message.MsgType.CRAWL_MSG) {
            log.info("服务端收到抓取消息...");
            QueueMsgProto.QueueMsg queueMsg = receive.getQueueMsg();
            redisPut(queueMsg);

        } else if (msgType == MessageProto.Message.MsgType.REQ_MSG) {
            log.info("服务端收到抓取请求消息...");
            QueueMessage queueMsg = redisGet();
            if (null != queueMsg) {
                QueueMsgProto.QueueMsg qMsg = MessageUtil.protoQueueMsg(queueMsg);
                MessageProto.Message message = MessageUtil.protoMessage(qMsg);
                ctx.writeAndFlush(message);
            }
        } else if (msgType == MessageProto.Message.MsgType.HEART_BEAT) {
            log.info("服务端收到心跳消息...");
            HeartbeatProto.Heartbeat heartbeat = receive.getHeartbeat();
            heartBeatHandler(ctx, heartbeat);
        }
    }

    /**
     * 存储消息至Redis
     *
     * @param protoMsg msg
     */
    public void redisPut(QueueMsgProto.QueueMsg protoMsg) {
        QueueMessage msg = MessageUtil.queueMessageBuilder(protoMsg);
        String key = msg.getCategory().key();
        redis.lpush(key.getBytes(), SerializeUtil.serialize(msg));
    }

    /**
     * 先获取所有的key，在随机选一个key，获取消息
     */
    public QueueMessage redisGet() {
        // 循环5次
        for (int i = 0; i < 5; i++) {
            String key = randomKey("*");
            if (StringUtils.isNotBlank(key)) {
                byte[] bytes = redis.lpop(key.getBytes());
                return (QueueMessage) SerializeUtil.unserialize(bytes);
            }
        }
        return null;
    }

    private String randomKey(String pattern) {
        Set<String> keys = redis.keys(pattern);
        int size = keys.size();
        Random r = new Random();
        int i = r.nextInt(size);
        Object obj = keys.toArray()[i];
        return obj.toString();
    }

    public void heartBeatHandler(ChannelHandlerContext ctx,
                                 HeartbeatProto.Heartbeat heartbeat) {
        System.out.println("code is: " + heartbeat.getCode());
        System.out.println("HeapMemory is: " + heartbeat.getHeapMemory());
        System.out.println("port is: " + heartbeat.getPort());
        System.out.println();
        HeartbeatProto.Heartbeat beartbeat = MessageUtil.protoHeartbeat(3, 22222, 1020);
        MessageProto.Message message = MessageUtil.protoHeartBeatMsg(beartbeat);

        ctx.writeAndFlush(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}
