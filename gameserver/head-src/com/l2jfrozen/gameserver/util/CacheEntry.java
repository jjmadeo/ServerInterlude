package com.l2jfrozen.gameserver.util;

public   class CacheEntry {
    private final Object value;
    private final long time;

    public CacheEntry(Object value, long time) {
        this.value = value;
        this.time = time;
    }

    public Object getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }
}