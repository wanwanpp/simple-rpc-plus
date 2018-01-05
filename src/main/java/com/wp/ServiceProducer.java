package com.wp;

import org.apache.log4j.BasicConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author 王萍
 * @date 2018/1/5 0005e
 */
public class ServiceProducer {
    public static void start() {
        //日志配置
        BasicConfigurator.configure();
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
