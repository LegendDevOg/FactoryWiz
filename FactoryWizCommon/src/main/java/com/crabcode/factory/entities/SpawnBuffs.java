package com.crabcode.factory.entities;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public class SpawnBuffs {
    private static final Map<UUID, Long> BUFFS = Maps.newHashMap();

    public static Long get(UUID uniqueId) {
        return BUFFS.getOrDefault(uniqueId, System.currentTimeMillis());
    }

    public static void update(UUID uniqueId, long addition) {
        BUFFS.put(uniqueId, System.currentTimeMillis() + addition);
    }
}
