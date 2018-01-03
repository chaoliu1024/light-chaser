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

import group.chaoliu.lightchaser.common.config.ProxyConstants;
import group.chaoliu.lightchaser.common.protocol.http.Proxy;
import group.chaoliu.lightchaser.rpc.netty.protobuf.ProxyProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * proxy client
 *
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class ProxySocket implements Runnable {

    private ChannelFuture future;

    private SyncWriter writer = new SyncWriter();

    private String host = "127.0.0.1";

    private int port = 30900;

    public ProxySocket(Map proxyConfig) {
        Object host = proxyConfig.get(ProxyConstants.PROXY_SOCKET_SERVER_HOST);
        if (null != host && host instanceof String) {
            this.host = (String) host;
        }

        Object port = proxyConfig.get(ProxyConstants.PROXY_SOCKET_SERVER_PORT);
        if (null != port && port instanceof Integer) {
            this.port = (int) port;
        }
    }

    @Override
    public void run() {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            b.group(workGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChildChannelHandler());
            b.option(ChannelOption.SO_KEEPALIVE, true);
            future = b.connect(this.host, this.port).sync();
            if (future.isSuccess()) {
                setFuture(future);
            }
        } catch (InterruptedException e) {
            log.error("proxy client connect to server error. {}", e);
        }
    }

    public ChannelFuture getFuture() {
        return future;
    }

    public void setFuture(ChannelFuture future) {
        this.future = future;
    }

    public Proxy requestProxy(String domainKey) {

        // 同步请求等待的超时时间
        final int syncWriteTimeout = 10000;
        try {
            Proxy proxy = writer.syncWrite(future.channel(), syncWriteTimeout, domainKey);
            log.info("socket return {}", proxy);
            return proxy;
        } catch (Exception e) {
            log.error("获取代理异常...");
            return null;
        }
    }

    /**
     * 反馈代理状态
     *
     * @param proxy
     */
    public void feedback(Proxy proxy, ProxyCode code, String domainKey) {

        ProxyProto.ProxyMsg.Builder builder = ProxyProto.ProxyMsg.newBuilder();
        builder.setMsgType(ProxyProto.ProxyMsg.MsgType.FEEDBACK_MSG);
        builder.setProxy(ProxyProtobufUtil.protoProxy(proxy, code.getCode()));
        builder.setDomainKey(domainKey);
        ProxyProto.ProxyMsg msg = builder.build();
        future.channel().writeAndFlush(msg);
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
            ch.pipeline().addLast(new ProtobufDecoder(ProxyProto.ProxyMsg.getDefaultInstance()));
            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
            ch.pipeline().addLast(new ProtobufEncoder());
            ch.pipeline().addLast(new ProxySocketHandler());
        }
    }
}
