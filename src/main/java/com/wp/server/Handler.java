package com.wp.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author 王萍
 * @date 2018/1/5 0004
 */
public class Handler extends SimpleChannelInboundHandler<Request> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);

    // 存放接口名与服务Bean之间的映射关系
    private final Map<String, Object> handlerMap;

    public Handler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    //request是经过Decoder处理后得到的对象
    @Override
    public void channelRead0(final ChannelHandlerContext ctx, Request request) throws Exception {
        Response response = new Response();
        response.setRequestId(request.getRequestId());
        try {
            //执行服务器端的服务
            Object result = handle(request);
            //设置结果
            response.setResult(result);
        } catch (Throwable t) {
            //设置error
            response.setError(t);
        }
        //返回
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

//    使用反射或cglib调用服务
    private Object handle(Request request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

//        jdk反射执行方法
//        Method method = serviceClass.getMethod(methodName, parameterTypes);
//        method.setAccessible(true);
//        return method.invoke(serviceBean, parameters);

//        cglib调用方法，貌似使用cglib可加速，但事实也不一定。
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server handler caught exception", cause);
        ctx.close();
    }
}
