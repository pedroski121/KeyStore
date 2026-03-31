package com.base.kvstore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Context {
    private final VectorClock vectorClock;
    private final Map<String, String> metadata;
    private final long requestId;
    private final String clientId;


    public Context(String clientId) {
        this.clientId = clientId;
        this.vectorClock = new VectorClock();
        this.metadata = new ConcurrentHashMap<>();
        this.requestId = System.currentTimeMillis();
    }

    public Context(VectorClock vectorClock, String clientId) {
        this.vectorClock = vectorClock;
        this.clientId = clientId;
        this.metadata = new ConcurrentHashMap<>();
        this.requestId = System.currentTimeMillis();
    }


    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public Map<String, String> getMetadata () {
        return metadata;
    }

    public String getClientId () {
        return clientId;
    }

    public void addMetadata(String key, String value) {
        metadata.put(key, value);
    }

    public String getMetadata(String key) {
        return metadata.get(key);
    }


}
