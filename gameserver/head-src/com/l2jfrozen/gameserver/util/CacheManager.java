package com.l2jfrozen.gameserver.util;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    private final Map<String, CacheEntry> cache;
    private final long timeToLive = 10 * 60 * 1000;

    private static volatile CacheManager instance;

    private CacheManager() {
        this.cache = new HashMap<>();
    }

    public static CacheManager getInstance() {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }

    public synchronized void put(String key, Object value) {
        long currentTime = System.currentTimeMillis();
        cache.put(key, new CacheEntry(value, currentTime));
    }

    public synchronized Object get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && System.currentTimeMillis() - entry.getTime() < timeToLive) {
            return entry.getValue();
        } else {
            cache.remove(key);
            return null;
        }
    }
}