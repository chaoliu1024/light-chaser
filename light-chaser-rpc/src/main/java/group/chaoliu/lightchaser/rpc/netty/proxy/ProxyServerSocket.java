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
import group.chaoliu.lightchaser.rpc.netty.protobuf.ProxyProto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author chao liu
 * @since Light Chaser 0.0.1
 */
@Slf4j
public class ProxyServerSocket implements Runnable {

    private int port = 30900;

    public ProxyServerSocket(Map proxyConfig) {
        Object port = proxyConfig.get(ProxyConstants.PROXY_SOCKET_SERVER_PORT);
        if (null != port && port instanceof Integer) {
            this.port = (int) port;
        }
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workGroup);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, 1024);

            b.childHandler(new ChildChannelHandler());

            ChannelFuture channelFuture = b.bind(port).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("Bootstrap Proxy Server error. {}", e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
            ch.pipeline().addLast(new ProtobufDecoder(ProxyProto.ProxyMsg.getDefaultInstance()));
            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
            ch.pipeline().addLast(new ProtobufEncoder());
            ch.pipeline().addLast(new ProxyServerHandler());
        }
    }
}