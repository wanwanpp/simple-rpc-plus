package com.wp;

import org.apache.log4j.BasicConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 */
public class RpcBootstrap {
    public static void main(String[] args) {
        //日志配置
        BasicConfigurator.configure();
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
