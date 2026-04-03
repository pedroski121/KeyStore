package com.base.hashring;


import java.util.Objects;

public class NodeInfo {
    private final String host;
    private final int port;
    private final String nodeId;
    private final int capacity; // Higher capacity more virtual nodes

    public NodeInfo(String host, int port, int capacity) {
        this.host = host;
        this.port = port;
        this.capacity = capacity;
        this.nodeId = host + ":" + port;
    }

    public NodeInfo(String host, int port) {
        this(host, port, 1);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getNodeId() {
        return nodeId;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        NodeInfo nodeInfo = (NodeInfo) o;
        return port == nodeInfo.getPort() && Objects.equals(host, nodeInfo.getHost());
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return "NodeInfo{" + nodeId + ", capacity=" + capacity + "}";
    }

}
