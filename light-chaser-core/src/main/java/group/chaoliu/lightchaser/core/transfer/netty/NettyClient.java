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
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.ChannelFuture;
//import io.netty.channel.ChannelInitializer;
//import io.netty.channel.ChannelOption;
//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.codec.protobuf.ProtobufDecoder;
//import io.netty.handler.codec.protobuf.ProtobufEncoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
//import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author chao liu
// * @since Light Chaser 0.0.1
// */
//public class NettyClient implements Runnable {
//
//    private String host;
//    private int port = 8080;
//    private final LightChaserClientHandler<MessageProto.Message> clientHandler = new LightChaserClientHandler<>();
//    private boolean isRunning = false;
//    private ExecutorService executor = null;
//
//    public NettyClient(String host, int port) {
//        this.host = host;
//        this.port = port;
//    }
//
//    public synchronized void startClient() {
//        if (!isRunning) {
//            executor = Executors.newFixedThreadPool(1);
//            executor.execute(this);
//            isRunning = true;
//        }
//    }
//
//    public boolean isRunning() {
//        return isRunning;
//    }
//
//    public synchronized boolean stopClient() {
//        boolean bReturn = true;
//        if (isRunning) {
//            if (executor != null) {
//                executor.shutdown();
//                try {
//                    executor.shutdownNow();
//                    if (executor.awaitTermination(calcTime(10, 0.66667), TimeUnit.SECONDS)) {
//                        if (!executor.awaitTermination(calcTime(10, 0.33334), TimeUnit.SECONDS)) {
//                            bReturn = false;
//                        }
//                    }
//                } catch (InterruptedException ie) {
//                    executor.shutdownNow();
//                    Thread.currentThread().interrupt();
//                } finally {
//                    executor = null;
//                }
//            }
//            isRunning = false;
//        }
//        return bReturn;
//    }
//
//    private long calcTime(int nTime, double dValue) {
//        return (long) ((double) nTime * dValue);
//    }
//
//    private void buildChannel() {
//        // 配置客户端NIO线程组
//        EventLoopGroup group = new NioEventLoopGroup();
//        try {
//            Bootstrap bootstrap = new Bootstrap();
//            bootstrap.group(group)
//                    .channel(NioSocketChannel.class)
//                    .option(ChannelOption.TCP_NODELAY, true)
//                    .handler(new ChannelHandler());
//            ChannelFuture f = bootstrap.connect(host, port).sync();
//            f.channel().closeFuture().sync();
//        } catch (InterruptedException ex) {
//        } finally {
//            group.shutdownGracefully();
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
//            ch.pipeline().addLast(clientHandler);
//        }
//    }
//
//    @Override
//    public void run() {
//        buildChannel();
//    }
//
//    public void send(MessageProto.Message msg) {
//        clientHandler.send(msg);
//    }
//
//    public static void main(String[] args) {
//        String host = "127.0.0.1";
//        int port = 8080;
//        NettyClient client = new NettyClient(host, port);
//        client.startClient();
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        Job job = new Job("OTA", "ctrip");
//        RequestMessage requestMessage = new RequestMessage();
//        requestMessage.setURL("www.test.com");
//        requestMessage.setPostRequest(true);
//        Map<String, String> head = new HashMap<>();
//        head.put("userAgent", "firefox");
//        requestMessage.setHeaders(head);
//
//        Map<String, String> cookie = new HashMap<>();
//        cookie.put("host", "localhost");
//        requestMessage.setCookie(cookie);
//
//        CrawlerMsgProto.CrawlerMsg crawlerMsg = MessageUtil.crawlerMsg(100, job, requestMessage);
//
//        MessageProto.Message message = MessageUtil.message(crawlerMsg);
//
//        System.out.println("发送抓取消息.....");
//        client.send(message);
//
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        HeartbeatProto.Heartbeat beartbeat = MessageUtil.beartbeat(1, 111111, 8090);
//        MessageProto.Message message2 = MessageUtil.message(beartbeat);
//
//        System.out.println("发送心跳消息.....");
//        client.send(message2);
//    }
//
//}