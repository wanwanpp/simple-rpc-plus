package com.wp;

import com.wp.client.RpcProxy;
import com.wp.service.HelloService;
import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceTest.class);

    @Test
    public void test() {
        //日志配置
        BasicConfigurator.configure();
        RpcProxy rpcProxy = new RpcProxy();
        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello("World");
        LOGGER.info(result);
        System.out.println(result);
        Assert.assertEquals("Hello! World", result);
        System.out.println(helloService.hello("World"));
    }
}