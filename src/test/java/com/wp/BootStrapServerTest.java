package com.wp;

import com.wp.server.RpcServer;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

/**
 * @author 王萍
 * @date 2018/1/5 0005
 */
public class BootStrapServerTest {
    @Test
    public void startServer() {
        BasicConfigurator.configure();
        RpcServer server = new RpcServer();
        System.out.println(server.getActiveServiceName());
        server.start();
    }
}
