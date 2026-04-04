package com.base.hashring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class ConsistentHashRingTest {
    private ConsistentHashRing ring;
    private NodeInfo node1, node2, node3, node4;

    @BeforeEach
    void setup () {
        ring = new ConsistentHashRing("MD5");
        node1 = new NodeInfo("192.168.1.1", 8080, 2);
        node2 = new NodeInfo("192.168.1.2", 8080, 1);
        node3 = new NodeInfo("192.168.1.3", 8080, 2);
        node4 = new NodeInfo("192.168.1.4", 8080, 1);
    }

    @Test
    @DisplayName("Should add nodes with capacity-aware token allocation")
    void shouldAddNodesWithCapacityAwareTokens() {
        int baseVirtualNodes = 100;
        ring.addNode(node1, baseVirtualNodes); // capacity 2 -> 200 virtual nodes
        ring.addNode(node2, baseVirtualNodes); // capacity 1 -> 100 virtual nodes

        assertThat(ring.getVirtualNodeCount()).isEqualTo(300);
        assertThat(ring.getAllNodes()).hasSize(2);

        // Count tokens per node
        Map<NodeInfo, Integer> tokenCount = countTokensPerNode();

        assertThat(tokenCount.get(node1)).isEqualTo(200);

        assertThat(tokenCount.get(node2)).isEqualTo(100);
    }

    @Test
    @DisplayName("Should remove node and all its virtual nodes")
    void shouldRemoveNodeAndAllVirtualNodes() {
        int capacity = 50;
        ring.addNode(node1, capacity); // capacity 2 -> 100 virtual nodes
        ring.addNode(node2, capacity); // capacity 1 -> 50 virtual nodes
        int initialSize = ring.getVirtualNodeCount();
        ring.removeNode(node2);
        assertThat(ring.getVirtualNodeCount()).isEqualTo(initialSize - capacity);
        assertThat(ring.getAllNodes()).doesNotContain(node2);
        assertThat(ring.getAllNodes()).contains(node1);
    }

    @Test
    @DisplayName("Should return correct preference list")
    void shouldReturnCorrectPreferenceList() {
        int capacity = 100;
        ring.addNode(node1, capacity);
        ring.addNode(node2, capacity);
        ring.addNode(node3, capacity);

        String key = "user:12345";
        List<NodeInfo> preferenceList = ring.getPreferenceList(key, 2);

        assertThat(preferenceList).hasSize(2);
        assertThat(preferenceList).doesNotHaveDuplicates();

        // Primary node should be consistent
        NodeInfo primary = ring.getNode(key);
        assertThat(preferenceList.get(0)).isEqualTo(primary);
    }



    private Map<NodeInfo, Integer> countTokensPerNode() {
        Map<NodeInfo, Integer> tokenCount = new HashMap<>();

        for(VirtualNode vNode: ring.getOrderedVirtualNodes()){
            tokenCount.merge(vNode.getPhysicalNode(), 1, Integer::sum);
        }
        return tokenCount;
    }
}
