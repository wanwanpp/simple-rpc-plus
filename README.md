# simple-rpc-plus
## 项目介绍
基于**netty**和**zookeeper**实现的一款简单rpc调用框架。

## 项目结构
### 1.调用端组件
  - **RpcClient**  
  使用Netty向服务提供方发起服务调用连接。
  - **ServiceDiscovery**  
  连接Zookeeper，获取服务地址。
  - **RpcProxy**  
  创建远程服务调用代理。

### 2.服务端组件
  - **RpcServer**  
    使用Netty启动通信服务器，接收调用端发起的请求。
  - **ServiceRegistry**
    注册服务地址到zookeeper。
  - **ServerHandler**  
    解析调用端请求，反射执行调用的服务，并返回结果至调用方。

### 3.编解码器
  - Encoder和Decoder类，使用Protostuff进行序列化。
   

## 扩展 
  - 分离出**注册中心组件**，对服务进行更细致的管理。  
    比如：  
    1. 做服务的负载均衡，目前项目中使用了简单的随机法进行负载。  
    2. 目前只在注册中心记录了服务提供方的地址，但是不能保证每个服务器上都提供了相同的服务。

## 示例
  示例请参照 [**simple-rpc-example**](https://github.com/wanwanpp/simple-rpc-example)


