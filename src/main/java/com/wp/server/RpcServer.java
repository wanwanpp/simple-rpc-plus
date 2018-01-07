package com.wp.server;

import com.wp.registry.ServiceRegistry;
import com.wp.utils.PropertiesUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 王萍
 * @date 2018/1/4 0004
 */

public class RpcServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    //    服务器地址
    private String serverAddress;
    private ServiceRegistry serviceRegistry;
    // 存放接口名与服务Bean之间的映射关系
    private Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    {
        Properties properties = PropertiesUtil.loadProps("server-config.properties");
        serverAddress = properties.getProperty("server.address");
        serviceRegistry = new ServiceRegistry(properties.getProperty("registry.address"));
    }

//    //利用ctx得到有指定注解的Bean
//    public void initService() {
//        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
//        if (serviceBeanMap != null && serviceBeanMap.size() > 0) {
//            for (Object serviceBean : serviceBeanMap.values()) {
//                //获取@RpcService的value指定的className
//                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
//                serviceMap.put(interfaceName, serviceBean);
//            }
//        }
//    }

    public void prepareService(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class c : interfaces) {
            serviceMap.put(c.getName(), service);
        }
    }

    public void addService(Object service) {
        prepareService(service);
    }

    public List<String> getActiveServiceName() {
        return new ArrayList<>(serviceMap.keySet());
    }

    public Object removeService(String serviceName) {
        return serviceMap.remove(serviceName);
    }

    public void start() {
        //启动Rpc服务
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    // 解码请求
                                    .addLast(new Decoder(Request.class))
                                    //编码响应
                                    .addLast(new Encoder(Response.class))
                                    //处理请求
                                    .addLast(new ServerHandler(serviceMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            ChannelFuture future = bootstrap.bind(host, port).sync();
            LOGGER.debug("server started in {} on port {}", host, port);

            if (serviceRegistry != null) {
                // 向注册中心注册服务地址
                serviceRegistry.register(serverAddress);
            }

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
