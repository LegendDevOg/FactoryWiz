package com.crabcode.factory.v1_20_R1;

import com.crabcode.factory.util.Logger;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Entity;

import java.lang.reflect.Field;

public class NMSDataWatcherObject {


    public static <K, T extends EntityDataAccessor<K>> T get(Class<? extends Entity> ent, String fieldName) {
        for (Field f : ent.getDeclaredFields()) {
            if (f.getName().equals(fieldName)) {
                f.setAccessible(true);
                try {
                    return (T) f.get(null);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    Logger.severe(e);
                }
            }
        }
        Logger.severe("Could not locate DataWatcherObject by the name of " + fieldName + " in class " + ent.getSimpleName());
        return null;
    }

}
