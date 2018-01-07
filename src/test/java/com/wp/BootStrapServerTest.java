package com.wp;

import com.wp.server.RpcServer;
import com.wp.service.HelloServiceImpl;
import org.junit.Test;

/**
 * @author 王萍
 * @date 2018/1/5 0005
 */
public class BootStrapServerTest {
    @Test
    public void startServer() {
        RpcServer server = new RpcServer();
        server.prepareService(new HelloServiceImpl());
        server.start();
    }
}
