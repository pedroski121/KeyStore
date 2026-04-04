package com.base.hashring;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConsistentHashRing {

    private final TreeMap<Long, VirtualNode> ring;
    private final MessageDigest md;
    private final ReadWriteLock lock;
    private final String hashAlgorithm;
    private final Map<String, Set<Long>> nodeToTokens;

    public ConsistentHashRing(String hashAlgorithm){
        this.ring = new TreeMap<>();
        this.nodeToTokens = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.hashAlgorithm = hashAlgorithm;

        try {
            this.md = MessageDigest.getInstance(hashAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw  new RuntimeException(hashAlgorithm + "algorithm not available", e);
        }
    }

    /**
     * Adds a node with capacity-aware virtual node allocation
     * @param nodeInfo the physical node to add
     * @param baseVirtualNodes baseline number of virtual nodes per unit capacity
     */
    public void addNode(NodeInfo nodeInfo, int baseVirtualNodes) {
        lock.writeLock().lock();
        try {
            int totalVirtualNodes = baseVirtualNodes * nodeInfo.getCapacity();
            Set<Long> tokens = new HashSet<>();
            for(int i = 0; i < totalVirtualNodes; i++) {
                String virtualNodeKey = nodeInfo.getNodeId() + "#vnode" + i;
                long token = hash(virtualNodeKey);
                VirtualNode vNode = new VirtualNode(token, nodeInfo, i);
                ring.put(token, vNode);
                tokens.add(token);
            }
            nodeToTokens.put(nodeInfo.getNodeId(), tokens);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Removes a node and all its virtual nodes.
     * @param nodeInfo the physical node to remove
     */
    public void removeNode(NodeInfo nodeInfo) {
        lock.writeLock().lock();
        try {
            Set<Long> tokens = nodeToTokens.remove(nodeInfo.getNodeId());
            if(tokens != null) {
                for(Long token:tokens){
                    ring.remove(token);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns N successor nodes clockwise from the hash of the key
     * @Param key the key to lookup
     * @Param n number of successor nodes to return
     * @return list of N unique physical nodes in clockwise order
     */
    public List<NodeInfo> getPreferenceList(String key, int n) {
        lock.readLock().lock();

        try {
            if(ring.isEmpty() || n <= 0){
                return Collections.emptyList();
            }

            List<NodeInfo> preferenceList = new ArrayList<>();

            Set<NodeInfo> seen = new HashSet<>();

            long hash = hash(key);

            // Get the tail map from the hash position
            SortedMap<Long, VirtualNode> tailMap = ring.tailMap(hash);

            // Iterate through the ring clockwise
            for(VirtualNode vNode: tailMap.values()) {
                NodeInfo physicalNode = vNode.getPhysicalNode();
                if(seen.add(physicalNode)) {
                    preferenceList.add(physicalNode);
                    if(preferenceList.size() == n){
                        return preferenceList;
                    }
                }
            }

            // Wrap around to the beginning of the ring
            for(VirtualNode vNode: ring.values()){
                NodeInfo physicalNode = vNode.getPhysicalNode();
                if(seen.add(physicalNode)) {
                    preferenceList.add(physicalNode);
                    if(preferenceList.size() == n){
                        return preferenceList;
                    }
                }
            }
            return preferenceList;

        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Hashes a string using the configured algorithm(MD5 or SHA-1)
     * Returns a 64-bit hash value (0 to 2^64 - 1)
     */
    private long hash(String key) {
        byte[] digest = md.digest(key.getBytes());
        // Take first 8 bytes of the digest (64 bits)
        long hash = 0;
        for (int i = 0; i < 8; i++) {
            hash <<= 8;
            hash |= (digest[i] & 0xFF);
        }
        return hash & 0xFFFFFFFFFFFFFFFFL;

    }
}
