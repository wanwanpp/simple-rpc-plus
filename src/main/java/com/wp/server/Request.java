package com.wp.server;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 王萍
 * @date 2018/1/5 0004
 * 封装了rpc调用端发来的请求数据
 */
@Getter
@Setter
public class Request {

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
