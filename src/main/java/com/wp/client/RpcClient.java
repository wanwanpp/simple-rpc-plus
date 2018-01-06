package com.wp.client;

import com.wp.server.Decoder;
import com.wp.server.Encoder;
import com.wp.server.Request;
import com.wp.server.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 王萍
 * @date 2018/1/5 0004
 */
public class RpcClient extends SimpleChannelInboundHandler<Response> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private String host;
    private int port;
    private Response response;
    private Channel channel;

    private final Object obj = new Object();

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new Encoder(Request.class))
                                    .addLast(new Decoder(Response.class))
                                    .addLast(RpcClient.this);
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            //连接服务提供方的地址
            ChannelFuture future = bootstrap.connect(host, port).sync();
            this.channel = future.channel();
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        this.response = response;

        synchronized (obj) {
            // 收到响应，唤醒线程
            obj.notifyAll();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("client caught exception", cause);
        ctx.close();
    }

    public Response send(Request request) throws Exception {
        connect();
        channel.writeAndFlush(request).sync();
        //线程等待
        synchronized (obj) {
            obj.wait(); // 未收到响应，使线程等待
        }
//        if (response != null) {
//            channel.closeFuture().sync();
//        }
        return response;
    }
}
