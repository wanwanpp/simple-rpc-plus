package com.wp.client;

import com.wp.server.Request;
import com.wp.server.Response;
import com.wp.utils.PropertiesUtil;
import net.sf.cglib.proxy.Proxy;

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
                (proxy, method, args) -> {
                    // 创建并初始化 RPC 请求
                    Request request = new Request();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setClassName(method.getDeclaringClass().getName());
                    request.setMethodName(method.getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);

//                    if (serviceDiscovery != null) {
                    //获取服务地址
                    serverAddress = serviceDiscovery.discover();
//                    }
                    RpcClient rpcClient = null;
                    if (rpcClientMap.containsKey(serverAddress)) {
                        rpcClient = rpcClientMap.get(serverAddress);
                    } else {
                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        rpcClient = new RpcClient(host, port);
                        rpcClientMap.put(serverAddress, rpcClient);
                    }
                    Response response = rpcClient.send(request);

                    if (response.isError()) {
                        throw response.getError();
                    } else {
                        return response.getResult();
                    }
                }
        );
    }
}
