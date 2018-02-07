package com.wp.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 王萍
 * @date 2018/1/5 0004
 * 封装了返回给远程调用端的响应数据
 */
@Getter
@Setter
public class Response {

    private String requestId;
    private Throwable error;
    private Object result;
    public boolean isError() {
        return error != null;
    }
}
