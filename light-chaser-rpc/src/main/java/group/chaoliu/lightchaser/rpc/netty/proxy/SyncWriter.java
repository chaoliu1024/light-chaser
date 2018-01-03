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

package group.chaoliu.lightchaser.rpc.netty.proxy;

import group.chaoliu.lightchaser.common.protocol.http.Proxy;
import group.chaoliu.lightchaser.rpc.netty.protobuf.ProxyProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 伪同步发送
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
public class SyncWriter {

    public static Map<String, SyncWriteFuture> syncWriteKey = new ConcurrentHashMap<>();

    /**
     * 同步发送请求
     *
     * @return
     */
    public Proxy syncWrite(final Channel channel, final long timeout, final String domainKey) throws Exception {

        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (timeout <= 0) {
            throw new IllegalArgumentException("timeout <= 0");
        }

        String reqId = UUID.randomUUID().toString();

        ProxyProto.ProxyMsg.Builder builder = ProxyProto.ProxyMsg.newBuilder();
        builder.setMsgType(ProxyProto.ProxyMsg.MsgType.REQ_MSG);
        builder.setDomainKey(domainKey);
        builder.setReqId(reqId);

        ProxyProto.ProxyMsg proxyMsg = builder.build();

        SyncWriteFuture<ProxyProto.ProxyMsg> future = new SyncWriteFuture<>(proxyMsg.getReqId());
        syncWriteKey.put(reqId, future);

        Proxy proxy = doSyncWrite(channel, proxyMsg, timeout, future);

        syncWriteKey.remove(proxyMsg.getReqId());

        return proxy;
    }

    private Proxy doSyncWrite(final Channel channel, final ProxyProto.ProxyMsg msg,
                              final long timeout, final SyncWriteFuture<ProxyProto.ProxyMsg> writeFuture) throws Exception {

        channel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                writeFuture.setWriteResult(future.isSuccess());
                writeFuture.setCause(future.cause());

                if (!writeFuture.isWriteSuccess()) {
                    syncWriteKey.remove(writeFuture.getReqId());
                }
            }
        });

        ProxyProto.ProxyMsg receiveMsg = writeFuture.get(timeout, TimeUnit.MILLISECONDS);
        if (receiveMsg == null) {
            if (writeFuture.isTimeout()) {
                throw new TimeoutException();
            } else {
                throw new Exception(writeFuture.cause());
            }
        }
        ProxyProto.Proxy proxyProto = receiveMsg.getProxy();

        if (StringUtils.isNotBlank(proxyProto.getType())) {
            return new Proxy(proxyProto.getHost(), proxyProto.getPort(), proxyProto.getType(), null, null, null);
        } else {
            return new Proxy(proxyProto.getHost(), proxyProto.getPort());
        }
    }
}
