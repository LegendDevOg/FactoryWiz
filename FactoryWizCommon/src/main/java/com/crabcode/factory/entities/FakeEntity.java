package com.crabcode.factory.entities;

import com.crabcode.factory.Versioned;
import com.crabcode.factory.util.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class FakeEntity implements IFakeEntity {

    final Set<String> tags = new HashSet<>();
    final Map<Class<? extends EntityBehavior>, EntityBehavior> behaviors = new HashMap<>();
    EntityRenderer renderer;

    final int id;
    final UUID myUUID;
    private Location location;
    private boolean onGround = true;
    boolean remove = false;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    FakeEntity(int id, UUID uuid, Location location) {
        this.id = id;
        this.myUUID = uuid;
        this.location = location.clone();
    }

    void gameplayTick(Player[] players) {
        try {
            lock.readLock().lock();
            this.behaviors.values().removeIf(eb -> {

                try {
                    return eb.tick0(players);
                } catch (Exception e) {
                    Logger.severe(e);
                    this.remove();
                    return false;
                }
            });
        } finally  {
            lock.readLock().unlock();
        }
    }

    void renderTick(Player[] players) {
        if (this.renderer != null) {
            try {
                this.renderer.tick(players);
            } catch (Exception e) {
                Logger.severe(e);
                this.remove();
            }
        }
    }

    @Override
    public Location getLocation() {
        return this.location.clone();
    }

    @Override
    public World getWorld() {
        return this.location.getWorld();
    }

    @Override
    public org.bukkit.util.Vector getSpot() {
        return this.location.toVector();
    }

    @Override
    public double getX() {
        return this.location.getX();
    }

    @Override
    public double getY() {
        return this.location.getY();
    }

    @Override
    public double getZ() {
        return this.location.getZ();
    }

    @Override
    public float getYaw() {
        return this.location.getYaw();
    }

    @Override
    public float getPitch() {
        return this.location.getPitch();
    }

    @Override
    public double getYHeadRot() {
        return 0;
    }

    @Override
    public boolean isOnGround() {
        return this.onGround;
    }

    @Override
    public UUID getUUID() {
        return this.myUUID;
    }

    @Override
    public int getEntityID() {
        return this.id;
    }

    @Override
    public List<EntityBehavior> getBehaviors() {
        try {
            this.lock.readLock().lock();
            return new ArrayList<>(this.behaviors.values());
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public <T extends EntityRenderer> T setRenderer(Class<T> rendererC) {
        if (rendererC == null) {
            Logger.severe("Renderer was null", new Error());
            return null;
        }
        EntityRenderer renderer = null;
        try {
            renderer = Versioned.getInstance(rendererC);
        } catch (Throwable t) {
            Logger.severe("Renderer[" + rendererC.getSimpleName() + "] threw ", t);
        }
        if (renderer == null) {
            throw new RuntimeException("Version didnt exist for EntityRenderer[" + rendererC.getSimpleName() + "]");
        }
        if (this.renderer != null) {
            //TODO: Unspawn old renderer
            this.renderer.despawnFor(this.renderer.getViewing());
        }
        this.renderer = renderer;
        this.renderer.setEntity(this).attachTo(this);
        return (T) this.renderer;
    }

    @Override
    public EntityRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public <T extends EntityBehavior> IFakeEntity addBehavior(T behavior) {
        try {
            this.lock.writeLock().lock();
            T previous = (T) this.behaviors.put(behavior.getClass(), behavior);
            if (previous != null) {
                previous.onDetach();
            }
        } finally {
            this.lock.writeLock().unlock();
        }
        behavior.attached = this;
        try {
            behavior.onAttach();
        } catch (Exception e) {
            Logger.severe(e);
            this.remove();
        }
        return this;
    }

    @Override
    public <T extends EntityBehavior> T getBehavior(Class<T> behaviorClass) {
        return (T) this.behaviors.get(behaviorClass);
    }

    /**
     * Prefer to do get or add yourself, but if you are too lazy use this method!
     * @param <T> Some class that extends EntityBehavior
     * @param behavior The EntityBehavior for which you wish to add/get on this entity.
     * @return behavior Either the behavior already attached to this entity, or the behavior supplied in the arguments.
     */
    @Override
    public <T extends EntityBehavior> T getOrAdd(T behavior) {
        T tempBehavior = (T) getBehavior(behavior.getClass());
        if (tempBehavior != null) {
            return tempBehavior;
        }
        this.addBehavior(behavior);
        return behavior;
    }


    @Override
    public <T> void callOnBehaviorsIfMatch(Class<T> clazz, Consumer<T> consumer) {
        this.behaviors.values().stream()
                .filter(clazz::isInstance)
                .forEach(beh -> consumer.accept((T) beh));
    }


    @Override
    public void remove() {
        this.remove = true;
    }


    @Override
    public IFakeEntity setLocation(Location location) {
        this.location = location.clone();
        return this;
    }

    @Override
    public IFakeEntity setPosition(Location location) {
        this.location.setX(location.getX());
        this.location.setY(location.getY());
        this.location.setZ(location.getZ());
        return this;
    }

    @Override
    public IFakeEntity setPosition(Vector position) {
        this.location.setX(position.getX());
        this.location.setY(position.getY());
        this.location.setZ(position.getZ());
        return this;
    }

    @Override
    public IFakeEntity setRotation(float yaw, float pitch) {
        this.location.setYaw(yaw);
        this.location.setPitch(pitch);
        return this;
    }

    @Override
    public void callOnBehaviors(Consumer<EntityBehavior> call) {
        this.behaviors.values().forEach(call);
    }

    @Override
    public Set<String> getTags() {
        return new HashSet<>(this.tags);
    }

    @Override
    public boolean hasTag(String tag) {
        return this.tags.contains(tag);
    }

    @Override
    public boolean addTag(String tag) {
        return this.tags.add(tag);
    }

    @Override
    public boolean removeTag(String tag) {
        return this.tags.remove(tag);
    }

    @Override
    public boolean isRemoved() {
        return this.remove;
    }

    boolean gameplayTick0(Player[] players) {
        if (this.remove) {
            return true;
        }
        this.gameplayTick(players);
        return false;
    }

    void renderTick0(Player[] players) {
        this.renderTick(players);
    }

    void despawn() {
        if (this.renderer != null) {
            this.renderer.despawnFor(this.renderer.getViewing());
        }
        //TODO Maybe call despawn event on behaviors?
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof FakeEntity && ((FakeEntity) obj).id == id;
    }
}
