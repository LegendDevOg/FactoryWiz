package com.crabcode.factory.entities;

import com.crabcode.factory.Versioned;
import com.crabcode.factory.data.io.DataIn;
import com.crabcode.factory.data.io.DataOut;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface EntityRenderer extends Versioned {

    //SETTERS
    public EntityRenderer setOnFire(boolean onFire);

    public EntityRenderer setCrouched(boolean crouch);

    public EntityRenderer setSprinting(boolean sprinting);

    public EntityRenderer setInvisible(boolean invisible);

    //1.9+
    public EntityRenderer setGlowing(boolean glowing);

    public EntityRenderer setElytraFlying(boolean elytraFlying);

    //1.13+
    public EntityRenderer setSwimming(boolean swimming);

    //idk version
    public EntityRenderer setIsRiding(boolean riding);

    public EntityRenderer setAirTime(int airTime);

    public EntityRenderer setCustomName(String customName);

    public EntityRenderer setCustomNameVisible(boolean customNameVisible);

    public EntityRenderer setSilent(boolean isSilent);

    public EntityRenderer setGravity(boolean hasGravity);


    public boolean addMount(org.bukkit.entity.Entity entity);

    public boolean hasMount(org.bukkit.entity.Entity entity);

    public boolean removeMount(org.bukkit.entity.Entity entity);

    public boolean addMount(int entityId);

    public boolean hasMount(int entityId);

    public boolean removeMount(int entityId);

    //GETTERS
    public boolean isOnFire();

    public boolean isCrouched();

    public boolean isSprinting();

    public boolean isInvisible();

    //1.9+
    public boolean isGlowing();

    public boolean isElytraFlying();

    //1.13+
    public boolean isSwimming();

    //idk version
    public boolean isRiding();

    public int getAirTime();

    public String getCustomName();

    public boolean isCustomNameVisible();

    public boolean isSilent();

    public boolean hasGravity();


    //BASE METHODS
    public <T extends IFakeEntity> void attachTo(T entity);

    public <T extends IFakeEntity> T getAttached();

    public void tick(Player[] players);

    public void spawnFor(Player player);

    public void spawnFor(List<Player> player);

    public void despawnFor(Player player);

    public void despawnFor(List<Player> player);

    public Set<UUID> getViewingUUID();

    public default List<Player> getViewing() {
        return getViewingUUID().stream().map(uuid -> Bukkit.getPlayer(uuid)).filter(p -> p != null).collect(Collectors.toList());
    }

    public double getRenderDistance();

    public EntityRenderer setRenderDistance(double distance);


    public default boolean isRenderedFor(Player player) {
        return getViewingUUID().contains(player.getUniqueId());
    }

    public default boolean shouldRenderFor(Player player) {
        Location l = getLocation();
        return player.getWorld().equals(l.getWorld()) && l.distance(player.getLocation()) <= this.getRenderDistance() - (this.getRenderDistance() / 10);
    }

    public default boolean shouldUnrenderFor(Player player) {
        Location l = getLocation();
        return ((!player.getWorld().equals(l.getWorld()) || l.distance(player.getLocation()) > this.getRenderDistance() + (this.getRenderDistance() / 10)));
    }

    public default Location getLocation() {
        return this.getAttached().getLocation();
    }


    public boolean hasEntity();

    public EntityRenderer setEntity(IFakeEntity entity);

    public void serialize(DataOut out);

    public void deserialize(DataIn in);

    public void resetRenderedFor(Player player);

}
