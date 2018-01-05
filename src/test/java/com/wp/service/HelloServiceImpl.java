package com.wp.service;

import com.wp.annotation.RpcService;

/**
 * Created by 王萍 on 2017/7/31 0031.
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! "+name;
    }
}
