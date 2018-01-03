package group.chaoliu.lightchaser.rpc.netty.dispatch.client;

import group.chaoliu.lightchaser.common.queue.message.QueueMessage;
import group.chaoliu.lightchaser.rpc.netty.protobuf.MessageProto;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientBuilder implements Runnable {
    private String host;
    private int port = 8080;
    private final LightChaserClientHandler clientHandler;

    @Getter
    private volatile boolean isRunning = false;
    private ExecutorService executor = null;

    private Queue<QueueMessage> msgQueue = new LinkedBlockingQueue<>();

    public QueueMessage getCrawlerMessage() {
        if (!msgQueue.isEmpty()) {
            return msgQueue.poll();
        }
        return null;
    }

    public ClientBuilder(String host, int port) {
        this.host = host;
        this.port = port;
        clientHandler = new LightChaserClientHandler(msgQueue);
    }

    public synchronized void startClient() {
        if (!isRunning) {
            executor = Executors.newFixedThreadPool(1);
            executor.execute(this);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public synchronized boolean stopClient() {
        boolean bReturn = true;
        if (isRunning) {
            if (executor != null) {
                executor.shutdown();
                try {
                    executor.shutdownNow();
                    if (executor.awaitTermination(calcTime(10, 0.66667), TimeUnit.SECONDS)) {
                        if (!executor.awaitTermination(calcTime(10, 0.33334), TimeUnit.SECONDS)) {
                            bReturn = false;
                        }
                    }
                } catch (InterruptedException ie) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                } finally {
                    executor = null;
                }
            }
            isRunning = false;
        }
        return bReturn;
    }

    private long calcTime(int nTime, double dValue) {
        return (long) ((double) nTime * dValue);
    }

    private void buildChannel() {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientBuilder.ChannelHandler());
            ChannelFuture f = bootstrap.connect(host, port).sync();
            isRunning = true;
            f.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
        } finally {
            group.shutdownGracefully();
        }
    }

    private class ChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
            ch.pipeline().addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()));
            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
            ch.pipeline().addLast(new ProtobufEncoder());
            ch.pipeline().addLast(clientHandler);
        }
    }

    @Override
    public void run() {
        buildChannel();
    }

    public void send(MessageProto.Message msg) {
        clientHandler.send(msg);
    }

}
