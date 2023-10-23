package com.crabcode.factory.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface IFakeEntity{

    public Location getLocation();

    public World getWorld();

    public Vector getSpot();

    public double getX();

    public double getY();

    public double getZ();

    public float getYaw();

    public float getPitch();

    public boolean isOnGround();

    public UUID getUUID();

    public int getEntityID();

    public boolean isRemoved();

    public default Vector getVelocity() {
        return new Vector();
    }

    public void remove();

    public IFakeEntity setLocation(Location location);

    public IFakeEntity setPosition(Location location);


    public IFakeEntity setPosition(Vector position);

    public IFakeEntity setRotation(float yaw, float pitch);

    public List<EntityBehavior> getBehaviors();

    public void callOnBehaviors(Consumer<EntityBehavior> call);

    public <T extends EntityRenderer> T setRenderer(Class<T> renderer);

    public EntityRenderer getRenderer();

    public <T extends EntityBehavior> IFakeEntity addBehavior(T behavior);

    public <T extends EntityBehavior> T getBehavior(Class<T> behaviorClass);

    /**
     * Prefer to do get or add yourself, but if you are too lazy use this method!
     * @param <T> Some class that extends EntityBehavior
     * @param behavior The EntityBehavior for which you wish to add/get on this entity.
     * @return behavior Either the behavior already attached to this entity, or the behavior supplied in the arguments.
     */
    public <T extends EntityBehavior> T getOrAdd(T behavior);

    public <T> void callOnBehaviorsIfMatch(Class<T> clazz, Consumer<T> consumer);


    public Set<String> getTags();

    public boolean hasTag(String tag);

    public boolean addTag(String tag);

    public boolean removeTag(String tag);

}
