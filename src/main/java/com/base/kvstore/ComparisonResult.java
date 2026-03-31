package com.base.kvstore;

public enum ComparisonResult {
    BEFORE, // this happens before other
    AFTER, // this happens after other
    CONCURRENT, // this and other are concurrent
    EQUAL // this and other are identical
}