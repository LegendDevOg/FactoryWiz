package com.crabcode.factory.v1_20_R1;

import com.crabcode.factory.data.io.DataIn;
import com.crabcode.factory.data.io.DataOut;
import com.crabcode.factory.entities.IFakeEntity;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class EntityRenderer implements com.crabcode.factory.entities.EntityRenderer {

    private static Method GET_DATAWATCHER_OBJECT;
    private static Field UPDATE_FIELD;
    private static Field MAP_FIELD;

    private static final EntityDataAccessor<Byte> MASK = NMSDataWatcherObject.get(Entity.class, "an");
    private static final EntityDataAccessor<Integer> AIR_TICKS = NMSDataWatcherObject.get(Entity.class, "aT");
    private static final EntityDataAccessor<Optional<Component>> CUSTOM_NAME = NMSDataWatcherObject.get(Entity.class, "aU");
    private static final EntityDataAccessor<Boolean> IS_CUSTOM_NAME_VISIBLE = NMSDataWatcherObject.get(Entity.class, "aV");
    private static final EntityDataAccessor<Boolean> IS_SILENT = NMSDataWatcherObject.get(Entity.class, "aW");
    private static final EntityDataAccessor<Boolean> NO_GRAVITY = NMSDataWatcherObject.get(Entity.class, "aX");

    private static final byte ON_FIRE_FLAG = 0x01;
    private static final byte CROUCH_FLAG = 0x02;
    private static final byte RIDING_FLAG = 0x04;
    private static final byte SPRINTING_FLAG = 0x08;
    private static final byte SWIMMING_FLAG = 0x10;
    private static final byte INVISIBLE_FLAG = 0x20;
    private static final byte GLOWING_FLAG = 0x40;
    private static final byte ELYTRA_FLAG = (byte) 0x80;


    static {
        Class<?> clazz = SynchedEntityData.class;
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equalsIgnoreCase("c") && m.getReturnType() != null && m.getReturnType().getSimpleName().toLowerCase().contains("item")
                    && m.getParameterCount() == 1) {
                m.setAccessible(true);
                GET_DATAWATCHER_OBJECT = m;
                break;
            }
        }
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getType().equals(boolean.class) && f.getName().equalsIgnoreCase("g")) {
                f.setAccessible(true);
                UPDATE_FIELD = f;
                continue;
            }
            if (f.getName().equalsIgnoreCase("e")) {
                f.setAccessible(true);
                MAP_FIELD = f;
                continue;
            }
        }
    }



    private IFakeEntity fakeEntity;

    private final Map<UUID, Long> lastTrackAttempt = new HashMap<>();
    private final Map<UUID,Integer> tracking = new HashMap<>();
    private double distance2View = 35D;
    private double viewdsqd = this.distance2View * this.distance2View;//TODO maybe move these to values set once render distance is set?
    private double minviewdsqd = viewdsqd - 9d;
    private double maxviewdsqd = viewdsqd + 9d;
    private World lastWorld;
    private Vector lastLocale;
    private float lastYaw, lastPitch;
    private final SynchedEntityData dataWatcher = new SynchedEntityData(null);
    private byte mask = 0;
    private boolean updateMetadata = false;
    private boolean forceTeleportPacket = false;
    private boolean updateMounts = false;
    private List<Player> toUpdate = new ArrayList<>();

    // Metadata
    private boolean onFire = false;
    private boolean crouch = false;
    private boolean sprinting = false;
    private boolean invisible = false;
    private boolean glowing = false;
    private boolean elytraFlying = false;
    private boolean swimming = false;
    private boolean riding = false;

    private int airTime = 0;
    private Optional<Component> customName = Optional.empty();
    private boolean customNameVisible = false;
    private boolean silent = false;
    private boolean gravity = false;

    private boolean onGround = true;

    protected Set<Integer> mounts = new HashSet<>();

    public EntityRenderer() {

        this.registerMetadata(MASK, mask);
        this.registerMetadata(AIR_TICKS, airTime);
        this.registerMetadata(IS_CUSTOM_NAME_VISIBLE, this.customNameVisible);
        this.registerMetadata(CUSTOM_NAME, customName);
        this.registerMetadata(IS_SILENT, this.silent);
        this.registerMetadata(NO_GRAVITY, this.gravity);
    }

    @Override
    public void serialize(DataOut out) {
        out.writeBoolean(onFire);
        out.writeBoolean(crouch);
        out.writeBoolean(sprinting);
        out.writeBoolean(invisible);
        out.writeBoolean(glowing);
        out.writeBoolean(elytraFlying);
        out.writeBoolean(swimming);
        out.writeBoolean(riding);

        out.writeInt(airTime);
        if (this.customName.isPresent()) {
            out.writeBoolean(true);
            out.writeBoolean(this.customNameVisible);
            out.writeString(Component.Serializer.toJson(this.customName.get()));
        } else {
            out.writeBoolean(false);
        }
        out.writeBoolean(this.silent);
        out.writeBoolean(this.gravity);
    }

    @Override
    public void deserialize(DataIn in) {
        this.setOnFire(in.readBoolean());
        this.setCrouched(in.readBoolean());
        this.setSprinting(in.readBoolean());
        this.setInvisible(in.readBoolean());
        this.setGlowing(in.readBoolean());
        this.setElytraFlying(in.readBoolean());
        this.setSwimming(in.readBoolean());
        this.setIsRiding(in.readBoolean());

        this.setAirTime(in.readInt());
        if (in.readBoolean()) {
            this.setCustomNameVisible(in.readBoolean());
            this.customName = Optional.ofNullable(Component.Serializer.fromJson(in.readString()));
            this.set(CUSTOM_NAME, this.customName);
        }
        this.setSilent(in.readBoolean());
        this.setGravity(in.readBoolean());
    }

    private AtomicBoolean attached = new AtomicBoolean(false);

    @Override
    public <T extends IFakeEntity> void attachTo(T entity) {
        this.fakeEntity = entity;
        Location lastLocation = entity.getLocation();
        this.lastWorld = lastLocation.getWorld();
        this.lastLocale = lastLocation.toVector();
        this.lastYaw = lastLocation.getYaw();
        this.lastPitch = lastLocation.getPitch();
        attached.set(true);
    }

    @Override
    public void tick(Player[] players) {
        if (fakeEntity == null || !attached.get())//WHAT?!
        {
            return;
        }
        long time = System.currentTimeMillis();
        this.toUpdate.clear();
        for (Player player : players) {
            UUID uuid = player.getUniqueId();
            Integer id = this.tracking.get(uuid);
            if (id == null || id != player.getEntityId()) {
                if (player.getWorld().equals(fakeEntity.getWorld()) && player.getLocation().toVector().distanceSquared(fakeEntity.getSpot()) <= this.minviewdsqd) {
                    boolean canSee = true;
                    /*
                    for (TrackSetting ts : trackSettings) {
                        if (!ts.canTrack(player)) {
                            canSee = false;
                            break;
                        }
                    }

                     */
                    if (!canSee) {
                        if (id != null)
                            this.tracking.remove(uuid);
                        continue;
                    }
                    spawnFor(player);
                    tracking.put(uuid, player.getEntityId());
                } else if (id != null)
                    this.tracking.remove(uuid);
            } else {
                if (!this.lastWorld.equals(fakeEntity.getWorld())) {//TODO Move this outside of all players
                    tracking.remove(uuid);
                    this.despawnFor(player);
                } else if (player.getLocation().toVector().distanceSquared(fakeEntity.getSpot()) >= this.maxviewdsqd) {
                    tracking.remove(uuid);
                    this.despawnFor(player);
                }
                /*
                else {
                    //TODO Updates
                    /*
                    boolean shouldRemove = !trackSettings.isEmpty();
                    for (TrackSetting ts : trackSettings) {
                        if (ts.canTrack(player)) {
                            shouldRemove = false;
                            break;
                        }
                    }


                    if (shouldRemove) {
                        tracking.remove(uuid);
                        despawnFor(player);
                        continue;
                    }

                    toUpdate.add(player);
                }

                 */
            }
        }
        double diff = fakeEntity.getSpot().distance(lastLocale);
        boolean relativeMove = fakeEntity.getSpot().distance(lastLocale) < 4;
        boolean rotationChange = fakeEntity.getYaw() != this.lastYaw || fakeEntity.getPitch() != this.lastPitch;
        List<Packet> packets = new ArrayList<>();
        if (diff > 0) {
            if (relativeMove && rotationChange) {
                if (!this.forceTeleportPacket) {
                    packets.add(Packets.getMoveAndLook(this.fakeEntity, this.lastLocale, this.fakeEntity.getSpot(), this.fakeEntity.getYaw(), this.fakeEntity.getPitch(), this.fakeEntity.isOnGround()));
                    //packets.add(Packets.getHeadLook(this.fakeEntity, this.fakeEntity.getYaw()));
                } else {
                    packets.add(Packets.getTeleport(this.fakeEntity));
                   // packets.add(Packets.getHeadLook(this.fakeEntity, this.fakeEntity.getYaw()));
                }
            } else if (relativeMove && !rotationChange) {
                if (!this.forceTeleportPacket) {
                    packets.add(Packets.getMove(this.fakeEntity, this.lastLocale, this.fakeEntity.getSpot(), this.fakeEntity.isOnGround()));
                } else {
                    packets.add(Packets.getTeleport(this.fakeEntity));
                }
            } else {
                packets.add(Packets.getTeleport(this.fakeEntity));
                //packets.add(Packets.getHeadLook(this.fakeEntity, this.fakeEntity.getYaw()));
            }
        } else {
            if (rotationChange) {
                packets.add(Packets.getLook(this.fakeEntity, this.fakeEntity.getYaw(), this.fakeEntity.getPitch(), this.fakeEntity.isOnGround()));
               // packets.add(Packets.getHeadLook(this.fakeEntity, this.fakeEntity.getYaw()));
            }
        }
        /*
        if (!this.animationsToPlay.isEmpty()) {
            this.animationsToPlay.forEach(anim -> packets.add(Packets.getAnimation(this.fakeEntity, anim)));
            this.animationsToPlay.clear();
        }


        if (updateMetadata) {
            updateMetadata = false;
            packets.add(Packets.getMetadata(fakeEntity, dataWatcher, true));
        }

        if (this.updateMounts) {
            this.updateMounts = false;
            int[] mounts = new int[this.mounts.size()];
            int ato = 0;
            for (int mounts2 : this.mounts) {
                if (ato < mounts.length) {
                    mounts[ato++] = mounts2;
                }
            }
            packets.add(Packets.getMount(fakeEntity, mounts));
        }
        */

        //TODO ANIMATIONS AND UPDATE OTHER
        otherUpdates(packets);

        ServerGamePacketListenerImpl[] connections = toUpdate.stream().map(player -> ((CraftPlayer) player).getHandle().connection).toArray(ServerGamePacketListenerImpl[]::new);
        for (Packet p : packets) {
            for (ServerGamePacketListenerImpl pc : connections) {
                pc.send(p);
            }
        }

        this.lastWorld = fakeEntity.getWorld();
        this.lastLocale = fakeEntity.getSpot();
        this.lastYaw = fakeEntity.getYaw();
        this.lastPitch = fakeEntity.getPitch();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setOnFire(boolean onFire) {
        this.onFire = onFire;
        byte b = mask;
        if (onFire) {
            b |= ON_FIRE_FLAG;
        } else {
            b &= ~ON_FIRE_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setCrouched(boolean crouch) {
        this.crouch = crouch;
        byte b = mask;
        if (crouch) {
            b |= CROUCH_FLAG;
        } else {
            b &= ~CROUCH_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setSprinting(boolean sprinting) {
        this.sprinting = sprinting;
        byte b = mask;
        if (sprinting) {
            b |= SPRINTING_FLAG;
        } else {
            b &= ~SPRINTING_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setInvisible(boolean invisible) {
        this.invisible = invisible;
        byte b = mask;
        if (invisible) {
            b |= INVISIBLE_FLAG;
        } else {
            b &= ~INVISIBLE_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setGlowing(boolean glowing) {
        this.glowing = glowing;
        byte b = mask;
        if (glowing) {
            b |= GLOWING_FLAG;
        } else {
            b &= ~GLOWING_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setElytraFlying(boolean elytraFlying) {
        this.elytraFlying = elytraFlying;
        byte b = mask;
        if (elytraFlying) {
            b |= ELYTRA_FLAG;
        } else {
            b &= ~ELYTRA_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setSwimming(boolean swimming) {
        this.swimming = swimming;
        byte b = mask;
        if (swimming) {
            b |= SWIMMING_FLAG;
        } else {
            b &= ~SWIMMING_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setIsRiding(boolean riding) {
        this.riding = riding;
        byte b = mask;
        if (riding) {
            b |= RIDING_FLAG;
        } else {
            b &= ~RIDING_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setAirTime(int airTime) {
        this.airTime = airTime;
        this.set(AIR_TICKS, this.airTime);
        return pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setCustomName(String customName) {
        if (customName == null || customName.isEmpty()) {
            this.customName = Optional.empty();
            this.set(CUSTOM_NAME, this.customName);
            return pushMetadataUpdate();
        }
        this.customName = Optional.of(Component.Serializer.fromJson(ComponentSerializer.toString(TextComponent.fromLegacyText(customName))));
        this.set(CUSTOM_NAME, this.customName);
        return pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setCustomNameVisible(boolean customNameVisible) {
        this.customNameVisible = customNameVisible;
        this.set(IS_CUSTOM_NAME_VISIBLE, this.customNameVisible);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setSilent(boolean isSilent) {
        this.silent = isSilent;
        this.set(IS_SILENT, this.silent);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setGravity(boolean hasGravity) {
        this.gravity = hasGravity;
        this.set(NO_GRAVITY, !this.gravity);
        return this.pushMetadataUpdate();
    }

    @Override
    public boolean isOnFire() {
        return this.onFire;
    }

    @Override
    public boolean isCrouched() {
        return this.crouch;
    }

    @Override
    public boolean isSprinting() {
        return this.sprinting;
    }

    @Override
    public boolean isInvisible() {
        return this.invisible;
    }

    @Override
    public boolean isGlowing() {
        return this.glowing;
    }

    @Override
    public boolean isElytraFlying() {
        return this.elytraFlying;
    }

    @Override
    public boolean isSwimming() {
        return this.swimming;
    }

    @Override
    public boolean isRiding() {
        return this.riding;
    }

    @Override
    public int getAirTime() {
        return this.airTime;
    }

    @Override
    public String getCustomName() {
        if (!this.customName.isPresent()) {
            return null;
        }
        return new TextComponent(ComponentSerializer.parse(Component.Serializer.toJson(this.customName.get()))).toLegacyText();
    }

    @Override
    public boolean isCustomNameVisible() {
        return this.customNameVisible;
    }

    @Override
    public boolean isSilent() {
        return this.silent;
    }

    @Override
    public boolean hasGravity() {
        return this.gravity;
    }

    @Override
    public <T extends IFakeEntity> T getAttached() {
        return (T) this.fakeEntity;
    }

    @Override
    public Set<UUID> getViewingUUID() {
        return Sets.newHashSet(this.tracking.keySet());
    }

    @Override
    public double getRenderDistance() {
        return this.distance2View;
    }

    @Override
    public com.crabcode.factory.entities.EntityRenderer setRenderDistance(double distance) {
        this.distance2View = distance;
        this.viewdsqd = this.distance2View * this.distance2View;
        this.minviewdsqd = this.viewdsqd - 9d;
        this.maxviewdsqd = this.viewdsqd + 9d;
        return this;
    }

    /*
    @Override
    public List<TrackSetting> getTrackSettings() {
        return this.trackSettings;
    }


     */

    public <T extends EntityRenderer> T pushMetadataUpdate() {
        this.updateMetadata = true;
        return (T) this;
    }

    public SynchedEntityData getWatcher() {
        return this.dataWatcher;
    }

    public <T> EntityRenderer registerMetadata(EntityDataAccessor<T> obj, T value) {
        try {
            SynchedEntityData.DataItem<T> item = new SynchedEntityData.DataItem<T>(obj, value);
            Int2ObjectOpenHashMap<SynchedEntityData.DataItem<?>> entries = (Int2ObjectOpenHashMap<SynchedEntityData.DataItem<?>>) MAP_FIELD.get(this.dataWatcher);
            entries.put(obj.getId(), item);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

    public <T> void set(EntityDataAccessor<T> obj, T t0) {
        try {
            SynchedEntityData.DataItem<T> datawatcher_watchableobject = (SynchedEntityData.DataItem<T>) GET_DATAWATCHER_OBJECT.invoke(dataWatcher, obj);
            if (ObjectUtils.notEqual(t0, datawatcher_watchableobject.getValue())) {
                datawatcher_watchableobject.setValue(t0);
                datawatcher_watchableobject.setDirty(true);
                UPDATE_FIELD.set(dataWatcher, true);
            }
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean addMount(org.bukkit.entity.Entity entity) {
        if (this.mounts.add(entity.getEntityId())) {
            this.updateMounts = true;
            return true;
        }
        return false;
    }

    public boolean hasMount(org.bukkit.entity.Entity entity) {
        return this.mounts.contains(entity.getEntityId());
    }

    public boolean removeMount(org.bukkit.entity.Entity entity) {
        if (this.mounts.remove(entity.getEntityId())) {
            this.updateMounts = true;
            return true;
        }
        return false;
    }

    public boolean addMount(int entityId) {
        if (this.mounts.add(entityId)) {
            this.updateMounts = true;
            return true;
        }
        return false;
    }

    public boolean hasMount(int entityId) {
        return this.mounts.contains(entityId);
    }

    public boolean removeMount(int entityId) {
        if (this.mounts.remove(entityId)) {
            this.updateMounts = true;
            return true;
        }
        return false;
    }

    public void otherUpdates(List<Packet> updates) {
    }

    @Override
    public boolean hasEntity() {
        return this.fakeEntity != null;
    }

    @Override
    public EntityRenderer setEntity(IFakeEntity entity) {
        if (this.fakeEntity == null) {
            this.fakeEntity = entity;
        }
        return this;
    }

    public void setForceTeleportPacket(boolean forceTeleportPacket) {
        this.forceTeleportPacket = forceTeleportPacket;
    }

    public boolean forceTeleportPacket() {
        return this.forceTeleportPacket;
    }

    @Override
    public void despawnFor(Player player) {
        this.tracking.remove(player.getUniqueId());
    }

    @Override
    public void despawnFor(List<Player> player) {
        player.forEach(p -> this.tracking.remove(p.getUniqueId()));
    }

    /*
    @Override
    public void playAnimation(Animation animation) {
        animationsToPlay.add(animation);
    }


     */
    @Override
    public void resetRenderedFor(Player player) {
        this.tracking.remove(player.getUniqueId());//TODO Make this better by actually despawning/not doing anything if still close enough?
    }
}

