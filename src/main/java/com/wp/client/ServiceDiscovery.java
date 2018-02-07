package com.wp.client;

import com.wp.server.Constant;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 王萍
 * @date 2018/1/4 0004
 */
public class ServiceDiscovery {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private volatile List<String> serviceAddressList = new ArrayList<>();
    // 注册中心的地址
    private String registryAddress;

    public ServiceDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;

        ZooKeeper zk = connectServer();
        if (zk != null) {
            watchNode(zk);
        }
    }

    public String discover() {
        String data = null;
        int size = serviceAddressList.size();
        if (size > 0) {
            if (size == 1) {//只有一个服务提供方
                data = serviceAddressList.get(0);
                LOGGER.debug("unique service address : {}", data);
            } else {
                //使用随机分配法。简单的负载均衡法
                data = serviceAddressList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("choose an address : {}", data);
            }
        }
        return data;
    }

    //连接zookeeper
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("", e);
        }
        return zk;
    }

    //获取服务地址列表
    private void watchNode(final ZooKeeper zk) {
        try {
            //获取子节点列表
            List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        //发生子节点变化时再次调用此方法更新服务地址
                        watchNode(zk);
                    }
                }
            });
            List<String> dataList = new ArrayList<>();
            for (String node : nodeList) {
                byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(bytes));
            }
            LOGGER.debug("node data: {}", dataList);
            this.serviceAddressList = dataList;
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }
}
