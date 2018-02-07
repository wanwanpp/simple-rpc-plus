package com.wp.server;

public interface Constant {

    int ZK_SESSION_TIMEOUT = 20000;

    String ZK_REGISTRY_PATH = "/registry";
    String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
}