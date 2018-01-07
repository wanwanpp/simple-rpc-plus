package com.wp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 王萍 on 2017/7/31 0031.
 */
//标有此注解的class，表示它是一个远程接口。
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    //value代表提供服务的接口
    Class<?> value();
}
