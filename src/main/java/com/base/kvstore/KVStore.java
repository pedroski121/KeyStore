package com.base.kvstore;

import java.util.Optional;
public interface KVStore {

    /**
     * Retrieves the value associated with the given key
     *
     * @param key the key to look up
     * @param context the request context containing vector clocks
     * @return Optional containing the VersionedValue if present
     */
    Optional<VersionedValue> get(String key, Context context);

    
    VersionedValue put(String key, byte[] value, Context context);
}
