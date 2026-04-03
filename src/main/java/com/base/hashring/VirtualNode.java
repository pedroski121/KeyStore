package com.base.hashring;

import java.util.Objects;

public class VirtualNode {
    private final long token; // Position on the hash ring
    private final NodeInfo physicalNode;
    private final int virtualNodeIndex;


    public VirtualNode(long token, NodeInfo physicalNode, int virtualNodeIndex) {
        this.token = token;
        this.physicalNode = physicalNode;
        this.virtualNodeIndex = virtualNodeIndex;
    }

    public long getToken() {
        return token;
    }

    public NodeInfo getPhysicalNode() {
        return physicalNode;
    }

    public int getVirtualNodeIndex() {
        return virtualNodeIndex;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        VirtualNode that = (VirtualNode) o;
        return token == that.token;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return "VirtualNode{" + physicalNode.getNodeId() + "#v" + virtualNodeIndex + ", token=" + token + "}";
    }
}
