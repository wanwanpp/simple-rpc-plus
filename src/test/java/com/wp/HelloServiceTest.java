package com.wp;

import com.wp.client.RpcProxy;
import com.wp.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceTest.class);

    public static void main(String[] args) throws InterruptedException {
        RpcProxy rpcProxy = new RpcProxy();
        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello("World");
        LOGGER.info(result);
        Thread.sleep(2000);
        LOGGER.info(result);
        Thread.sleep(2000);
        LOGGER.info(result);
    }
//    @Test
//    public void test() throws InterruptedException, IOException {
//        //日志配置,用来log4j.properties就不用这一句
////        BasicConfigurator.configure(new RollingFileAppender(new PatternLayout("%r [%t] %p %c %x - %m%n"), "client.log"));
//        RpcProxy rpcProxy = new RpcProxy();
//        HelloService helloService = rpcProxy.create(HelloService.class);
//        String result = helloService.hello("World");
//        LOGGER.info(result);
//        Thread.sleep(2000);
//        LOGGER.info(result);
//        Thread.sleep(2000);
//        LOGGER.info(result);
//    }
}