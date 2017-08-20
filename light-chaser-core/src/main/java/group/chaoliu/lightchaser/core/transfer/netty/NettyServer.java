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
//import group.chaoliu.lightchaser.core.transfer.netty.protobuf.MessageProto;
//import io.netty.bootstrap.ServerBootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioServerSocketChannel;
//import io.netty.handler.codec.protobuf.ProtobufDecoder;
//import io.netty.handler.codec.protobuf.ProtobufEncoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
//import io.netty.handler.logging.LogLevel;
//import io.netty.handler.logging.LoggingHandler;
//
///**
// * @author chao liu
// * @since Light Chaser 0.0.1
// */
//public class NettyServer {
//
//    public void bind(int port) throws Exception {
//
//        // accepts an incoming connection
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//
//        // handles the traffic of the accepted connection once the boss accepts the connection
//        // and registers the accepted connection to the worker
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try {
//            ServerBootstrap bootstrap = new ServerBootstrap();
//            bootstrap.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .option(ChannelOption.SO_BACKLOG, 1024)
//                    .handler(new LoggingHandler(LogLevel.INFO))
//                    .childHandler(new ChannelHandler());
//            // 绑定端口，同步等待成功
//            ChannelFuture f = bootstrap.bind(port).sync();
//
//            // 等待服务端监听端口关闭
//            f.channel().closeFuture().sync();
//        } finally {
//            // 优雅退出，释放线程池资源
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
//    }
//
//    private class ChannelHandler extends ChannelInitializer<SocketChannel> {
//        @Override
//        protected void initChannel(SocketChannel ch) throws Exception {
//            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
//            ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
//            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
//            ch.pipeline().addLast(new ProtobufEncoder());
//            ch.pipeline().addLast(new LightChaserServerHandler());
//        }
//    }
//
//    /**
//     * @param args
//     * @throws Exception
//     */
//    public static void main(String[] args) throws Exception {
//        int port = 8080;
//        new NettyServer().bind(port);
//    }
//}
