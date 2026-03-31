package com.base.kvstore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class VectorClock {
    private final Map<String, Integer> clock;

    public VectorClock() {
        this.clock = new ConcurrentHashMap<>();
    }

    public VectorClock(Map<String, Integer> clock) {
        this.clock = new ConcurrentHashMap<>();
    }

    /**
     * Increments the clock for a specific node
     * @Param nodeId the node identifier
     * @return this VectorClock for chaining
     */
    public VectorClock increments(String nodeId){
        clock.merge(nodeId, 1, Integer::sum);
        return this;
    }

    /**
     * Merges another vector clock into this one (takes the maximum for each node)
     * @Param other the other vector clock to merge
     * @return this VectorClock for chaining
     */
    public VectorClock merge(VectorClock other) {
        other.clock.forEach((nodeId, version)->
                clock.merge(nodeId, version, Math::max));
        return this;
    }

    /**
     * Compares this vector clock with another
     * @Param other the other vector clock to compare
     * @return ComparisonResult indicating BEFORE, AFTER, CONCURRENT, or EQUAL
     */
    public ComparisonResult compare(VectorClock other) {
        boolean thisGreater = false;
        boolean otherGreater = false;

        // Check all nodes in the clock
        for(Map.Entry<String, Integer> entry: clock.entrySet()){
            String nodeId = entry.getKey();
            int thisVersion = entry.getValue();
            int otherVersion = other.clock.getOrDefault(nodeId, 0);

            if(thisVersion > otherVersion){
                thisGreater = true;
            } else if(thisVersion < otherVersion) {
                otherGreater = true;
            }

        }

        for(String nodeId: other.clock.keySet()){
            if(!clock.containsKey(nodeId)){
                otherGreater = true;
            }
        }
        if (!thisGreater && !otherGreater) {
            return ComparisonResult.EQUAL;
        } else if (thisGreater && !otherGreater) {
            return ComparisonResult.AFTER;
        } else if (!thisGreater && otherGreater) {
            return ComparisonResult.BEFORE;
        } else {
            return ComparisonResult.CONCURRENT;
        }

    }

    public Map<String, Integer> getClock() {
        return new ConcurrentHashMap<>(clock);
    }

    @Override
    public String toString() {
        return clock.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }



}

