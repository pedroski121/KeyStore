package com.base.kvstore;


import java.util.Arrays;
import java.util.Objects;

public class VersionedValue {
    private final byte[] value;
    private final VectorClock vectorClock;
    private final long timestamp;

    public VersionedValue(byte[] value, VectorClock vectorClock) {
        this.value = value.clone();
        this.vectorClock = vectorClock;
        this.timestamp = System.currentTimeMillis();
    }

    public VersionedValue(byte[] value, VectorClock vectorClock, long timestamp){
        this.value = value.clone();
        this.vectorClock = vectorClock;
        this.timestamp = timestamp;
    }

    public byte[] getValue() {
        return value.clone();
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isConcurrentWith(VersionedValue other) {
        ComparisonResult result = this.vectorClock.compare(other.vectorClock);
        return result == ComparisonResult.CONCURRENT;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        VersionedValue that = (VersionedValue) o;
        return timestamp == that.timestamp && Arrays.equals(value, that.value) && Objects.equals(vectorClock, that.vectorClock);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(vectorClock, timestamp);
        return 31 * result + Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return "VersionedValue{" +
                "value=" + Arrays.toString(value) +
                ", vectorClock=" + vectorClock +
                ", timestamp=" + timestamp + '}';
    }



}
