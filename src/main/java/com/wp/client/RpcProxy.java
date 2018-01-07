package com.wp.client;

import com.wp.server.Request;
import com.wp.server.Response;
import com.wp.utils.PropertiesUtil;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 王萍
 * @date 2018/1/4 0004
 */
public class RpcProxy {

    //服务提供方地址，由ServiceDiscover组件中获得。
    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;
    private Map<String, RpcClient> rpcClientMap = new ConcurrentHashMap<>();

    {
        Properties properties = PropertiesUtil.loadProps("client-config.properties");
        serviceDiscovery = new ServiceDiscovery(properties.getProperty("discovery.address"));
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Request request = new Request(); // 创建并初始化 RPC 请求
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        if (serviceDiscovery != null) {
                            serverAddress = serviceDiscovery.discover(); // 发现服务
                        }

                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);

                        RpcClient client = new RpcClient(host, port); // 初始化 RPC 客户端
                        Response response = client.send(request); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应

                        if (response.isError()) {
                            throw response.getError();
                        } else {

                            return response.getResult();
                        }
                    }
                }
        );
    }
}
