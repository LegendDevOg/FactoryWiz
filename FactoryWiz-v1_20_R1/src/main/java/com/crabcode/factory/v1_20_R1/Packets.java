package com.crabcode.factory.v1_20_R1;

import com.crabcode.factory.entities.Equipable;
import com.crabcode.factory.entities.IFakeEntity;
import com.crabcode.factory.util.InventoryUtil;
import com.crabcode.factory.util.Logger;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class Packets {

    private static Entity a;
    public static ClientboundMoveEntityPacket.Pos getMove(IFakeEntity entity, Vector oldS, Vector newS, boolean onGround) {
        return new ClientboundMoveEntityPacket.Pos(entity.getEntityID(),
                (short) (((newS.getX() * 32) - (oldS.getX() * 32)) * 128),
                (short) (((newS.getY() * 32) - (oldS.getY() * 32)) * 128),
                (short) (((newS.getZ() * 32) - (oldS.getZ() * 32)) * 128),
                onGround);
    }

    public static ClientboundMoveEntityPacket.PosRot getMoveAndLook(IFakeEntity entity, Vector oldS, Vector newS, float yaw, float pitch, boolean onGround) {
        return new ClientboundMoveEntityPacket.PosRot(entity.getEntityID(),
                (short) (((newS.getX() * 32) - (oldS.getX() * 32)) * 128),
                (short) (((newS.getY() * 32) - (oldS.getY() * 32)) * 128),
                (short) (((newS.getZ() * 32) - (oldS.getZ() * 32)) * 128),
                (byte) (yaw * 256.0F / 360.0F),
                (byte) (pitch * 256.0F / 360.0F),
                onGround);
    }

    public static ClientboundMoveEntityPacket.Rot getLook(IFakeEntity entity, float yaw, float pitch, boolean onGround) {
        return new ClientboundMoveEntityPacket.Rot(entity.getEntityID(),
                (byte) (yaw * 256.0F / 360.0F),
                (byte) (pitch * 256.0F / 360.0F),
                onGround);

    }

    public static ClientboundRotateHeadPacket getHeadLook(IFakeEntity entity, float yaw) {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.setInt(0, entity.getEntityID());
        byteBuf.setByte(1, (byte) (yaw * 256.0F / 360.0F));
        return new ClientboundRotateHeadPacket(new FriendlyByteBuf(byteBuf));

    }

    public static ClientboundTeleportEntityPacket getTeleport(IFakeEntity entity) {

        ByteBuf byteBuf = Unpooled.buffer();


        FriendlyByteBuf f = new FriendlyByteBuf(byteBuf);

        f.writeVarInt(entity.getEntityID());
        f.writeDouble(entity.getX());
        f.writeDouble(entity.getY());
        f.writeDouble(entity.getZ());
        f.writeByte((byte) entity.getYaw());
        f.writeByte((byte) entity.getPitch());
        f.writeBoolean(entity.isOnGround());

        return new ClientboundTeleportEntityPacket(f);
    }

    public static ClientboundSetEquipmentPacket getEquipment(IFakeEntity entity, ItemStack[] equipment) {
        List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> pairSlot = Lists.newArrayList();

        for(Equipable.EquipSlot slot : Equipable.EquipSlot.values()) {
            if(InventoryUtil.isValid(equipment[slot.ordinal()])) {
                pairSlot.add(Pair.of(getSlot(slot), CraftItemStack.asNMSCopy(equipment[slot.ordinal()])));
            }
        }

        return new ClientboundSetEquipmentPacket(entity.getEntityID(), pairSlot);
    }

    public static EquipmentSlot getSlot(Equipable.EquipSlot slot) {
        EquipmentSlot slot2 = null;
        switch (slot) {
            case CHESTPLATE:
                slot2 = EquipmentSlot.CHEST;
                break;
            case BOOTS:
                slot2 = EquipmentSlot.FEET;
                break;
            case HELMET:
                slot2 = EquipmentSlot.HEAD;
                break;
            case LEGGINGS:
                slot2 = EquipmentSlot.LEGS;
                break;
            case MAINHAND:
                slot2 = EquipmentSlot.MAINHAND;
                break;
            case OFFHAND:
                slot2 = EquipmentSlot.OFFHAND;
                break;
        }
        return slot2;
    }

    /*
    public static PacketPlayOutEntityMetadata getMetadata(IFakeEntity entity, SynchedEntityData watcher, boolean allData) {
        return new PacketPlayOutEntityMetadata(entity.getEntityID(), watcher, allData);
    }

     */

    public static ClientboundAddEntityPacket getObjectSpawn(IFakeEntity entity, RenderType type) {
        Vector vel = entity.getVelocity();
        return new ClientboundAddEntityPacket(entity.getEntityID(), entity.getUUID(), entity.getX(), entity.getY(),
                entity.getZ(), entity.getYaw(), entity.getPitch(), type.type, 0, new Vec3(vel.getX(), vel.getY(), vel.getZ()), entity.getYHeadRot());
    }

    public static ClientboundRemoveEntitiesPacket getDestroy(IFakeEntity... entities) {
        int[] ents = new int[entities.length];
        for (int i = 0; i > ents.length; i++) {
            ents[i] = entities[i].getEntityID();
        }
        return new ClientboundRemoveEntitiesPacket(ents);
    }

    /*
    public static ClientboundAnimatePacket getAnimation(IFakeEntity entity, Animation animation) {
        ClientboundAnimatePacket anim = new ClientboundAnimatePacket();
        EntityAnimation.set(anim, entity.getEntityID(), animation);
        return anim;
    }



    public static PacketPlayOutSpawnEntityLiving getLivingSpawn(IFakeEntity entity, RenderType type, DataWatcher dataWatcher) {
        return LivingSpawn.create(entity, type, dataWatcher);
    }


    public static PacketPlayOutNamedEntitySpawn getNamedSpawn(IFakeEntity entity, DataWatcher dataWatcher) {
        return NamedSpawn.create(entity, dataWatcher);
    }


    public static PacketPlayOutMount getMount(IFakeEntity entity, int... mounts) {
        return EntityMount.create(entity, mounts);
    }



    private static class EntityMount {
        private static Field ENTITY_ID;
        private static Field MOUNT_IDS;

        static {
            for (Field f : PacketPlayOutMount.class.getDeclaredFields()) {
                if (f.getType().equals(int.class) && f.getName().equalsIgnoreCase("a")) {
                    f.setAccessible(true);
                    ENTITY_ID = f;
                    continue;
                }
                if (f.getType().equals(int[].class) && f.getName().equalsIgnoreCase("b")) {
                    f.setAccessible(true);
                    MOUNT_IDS = f;
                    continue;
                }
            }
        }

        public static PacketPlayOutMount create(IFakeEntity entity, int... mounts) {
            try {
                PacketPlayOutMount pack = new PacketPlayOutMount();
                ENTITY_ID.set(pack, entity.getEntityID());
                MOUNT_IDS.set(pack, mounts);
                return pack;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private static class EntityAnimation {
        private static Field ENTITY_ID;
        private static Field ANIMATION;

        static {
            for (Field f : PacketPlayOutAnimation.class.getDeclaredFields()) {
                if (f.getType().equals(int.class) && f.getName().equalsIgnoreCase("a")) {
                    f.setAccessible(true);
                    ENTITY_ID = f;
                    continue;
                }
                if (f.getType().equals(int.class) && f.getName().equalsIgnoreCase("b")) {
                    f.setAccessible(true);
                    ANIMATION = f;
                    continue;
                }
            }
        }

        public static void set(PacketPlayOutAnimation pack, int entityID, Animation animation) {
            try {
                ENTITY_ID.set(pack, entityID);
                ANIMATION.set(pack, animation.ordinal());
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }
    */

    private static class Teleport {
        private static Field ENTITY_ID;
        private static Field X;
        private static Field Y;
        private static Field Z;
        private static Field YAW;
        private static Field PITCH;
        private static Field ON_GROUND;

        static {
            for (Field f : ClientboundTeleportEntityPacket.class.getDeclaredFields()) {
                if (f.getType().equals(int.class) && f.getName().equalsIgnoreCase("a")) {
                    f.setAccessible(true);
                    ENTITY_ID = f;
                    continue;
                }
                if (f.getType().equals(double.class) && f.getName().equalsIgnoreCase("b")) {
                    f.setAccessible(true);
                    X = f;
                    continue;
                }
                if (f.getType().equals(double.class) && f.getName().equalsIgnoreCase("c")) {
                    f.setAccessible(true);
                    Y = f;
                    continue;
                }
                if (f.getType().equals(double.class) && f.getName().equalsIgnoreCase("d")) {
                    f.setAccessible(true);
                    Z = f;
                    continue;
                }
                if (f.getType().equals(byte.class) && f.getName().equalsIgnoreCase("e")) {
                    f.setAccessible(true);
                    YAW = f;
                    continue;
                }
                if (f.getType().equals(byte.class) && f.getName().equalsIgnoreCase("f")) {
                    f.setAccessible(true);
                    PITCH = f;
                    continue;
                }
                if (f.getType().equals(boolean.class) && f.getName().equalsIgnoreCase("g")) {
                    f.setAccessible(true);
                    ON_GROUND = f;
                    continue;
                }
            }
        }

        public static void set(ClientboundTeleportEntityPacket pack, int entityID, double x, double y, double z, byte yaw, byte pitch, boolean onGround) {
            try {
                ENTITY_ID.set(pack, entityID);
                X.set(pack, x);
                Y.set(pack, y);
                Z.set(pack, z);
                YAW.set(pack, yaw);
                PITCH.set(pack, pitch);
                ON_GROUND.set(pack, onGround);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }
    /*


    private static class HeadLook {
        private static Field ENTITY_ID;
        private static Field YAW;

        static {
            for (Field f : PacketPlayOutEntityHeadRotation.class.getDeclaredFields()) {
                if (f.getType().equals(int.class) && f.getName().equalsIgnoreCase("a")) {
                    f.setAccessible(true);
                    ENTITY_ID = f;
                    continue;
                }
                if (f.getType().equals(byte.class) && f.getName().equalsIgnoreCase("b")) {
                    f.setAccessible(true);
                    YAW = f;
                    continue;
                }
            }
        }

        public static ClientboundRotateHeadPacket create(Entity entity, byte yaw) {
            ClientboundRotateHeadPacket pack = new ClientboundRotateHeadPacket(entity, yaw);

            return pack;
        }

    }

    private static class LivingSpawn {
        private static Field ENTITY_ID;
        private static Field UUID;
        private static Field ENTITY_TYPE;
        private static Field X;
        private static Field Y;
        private static Field Z;
        private static Field YAW;
        private static Field HEAD_YAW;
        private static Field PITCH;
        private static Field DATAWATCHER;
        private static Field VEL_X;
        private static Field VEL_Y;
        private static Field VEL_Z;

        static {
            for (Field f : ClientboundAddEntityPacket.class.getDeclaredFields()) {
                String fieldName = f.getName().toLowerCase();
                f.setAccessible(true);
                switch (fieldName) {
                    case "a":
                        ENTITY_ID = f;
                        continue;
                    case "b":
                        UUID = f;
                        continue;
                    case "c":
                        ENTITY_TYPE = f;
                        continue;
                    case "d":
                        X = f;
                        continue;
                    case "e":
                        Y = f;
                        continue;
                    case "f":
                        Z = f;
                        continue;
                    case "g":
                        VEL_X = f;
                        continue;
                    case "h":
                        VEL_Y = f;
                        continue;
                    case "i":
                        VEL_Z = f;
                        continue;
                    case "j":
                        YAW = f;
                        continue;
                    case "k":
                        PITCH = f;
                        continue;
                    case "l":
                        HEAD_YAW = f;
                        continue;
                    case "m":
                        DATAWATCHER = f;
                        continue;
                }
            }
        }

        public static ClientboundAddEntityPacket create(IFakeEntity entity, RenderType type, SynchedEntityData dataWatcher) {
            ClientboundAddEntityPacket packet = new ClientboundAddEntityPacket(entity.getEntityID());
            try {
                ENTITY_ID.set(packet, entity.getEntityID());
                UUID.set(packet, entity.getUUID());
                ENTITY_TYPE.set(packet, type.getID());
                X.set(packet, entity.getX());
                Y.set(packet, entity.getY());
                Z.set(packet, entity.getZ());
                YAW.set(packet, (byte) (entity.getYaw() * 256f / 360f));
                PITCH.set(packet, (byte) (entity.getPitch() * 256f / 360f));
                HEAD_YAW.set(packet, (byte) (entity.getYaw() * 256f / 360f));
                Vector velocity = entity.getVelocity();
                VEL_X.set(packet, (int) (Mth.clamp(velocity.getX(), -3.9, 3.9) * 8000));
                VEL_Y.set(packet, (int) (Mth.clamp(velocity.getY(), -3.9, 3.9) * 8000));
                VEL_Z.set(packet, (int) (Mth.clamp(velocity.getZ(), -3.9, 3.9) * 8000));
                DATAWATCHER.set(packet, dataWatcher);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return packet;
        }

    }

    private static class NamedSpawn {
        private static Field ENTITY_ID;
        private static Field UUID;
        private static Field X;
        private static Field Y;
        private static Field Z;
        private static Field YAW;
        private static Field PITCH;
        private static Field DATAWATCHER;

        static {
            for (Field f : PacketPlayOutNamedEntitySpawn.class.getDeclaredFields()) {
                String fieldName = f.getName().toLowerCase();
                f.setAccessible(true);
                switch (fieldName) {
                    case "a":
                        ENTITY_ID = f;
                        continue;
                    case "b":
                        UUID = f;
                        continue;
                    case "c":
                        X = f;
                        continue;
                    case "d":
                        Y = f;
                        continue;
                    case "e":
                        Z = f;
                        continue;
                    case "f":
                        YAW = f;
                        continue;
                    case "g":
                        PITCH = f;
                        continue;
                    case "h":
                        DATAWATCHER = f;
                        continue;
                }
            }
        }

        public static PacketPlayOutNamedEntitySpawn create(IFakeEntity entity, DataWatcher dataWatcher) {
            PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
            try {
                ENTITY_ID.set(packet, entity.getEntityID());
                UUID.set(packet, entity.getUUID());
                X.set(packet, entity.getX());
                Y.set(packet, entity.getY());
                Z.set(packet, entity.getZ());
                YAW.set(packet, (byte) (entity.getYaw() * 256f / 360f));
                PITCH.set(packet, (byte) (entity.getPitch() * 256f / 360f));
                DATAWATCHER.set(packet, dataWatcher);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return packet;
        }

    }

    public static class PlayerInfo {

        private static Field ACTION;
        private static Field INFO_LIST;

        static {
            for (Field f : PacketPlayOutPlayerInfo.class.getDeclaredFields()) {
                String fieldName = f.getName().toLowerCase();
                f.setAccessible(true);
                switch (fieldName) {
                    case "a":
                        ACTION = f;
                        continue;
                    case "b":
                        INFO_LIST = f;
                        continue;
                }
            }
        }

        public static final PacketPlayOutPlayerInfo getAdd(GameProfile profile, GameMode gamemode, int ping, TextComponent displayName) {
            SavagePlayerInfoOut packet = new SavagePlayerInfoOut(EnumPlayerInfoAction.ADD_PLAYER);
            packet.setPlayerInfoData(Collections.singletonList(new SavagePlayerInfoOut.SavagePlayerInfoData(profile, ping,
                    EnumGamemode.getById(gamemode.getValue()),
                    IChatBaseComponent.ChatSerializer.jsonToComponent(ComponentSerializer.toString(displayName)))));
            return packet.build();
//            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
//            try {
//                PacketPlayOutPlayerInfo.PlayerInfoData pid = packet.new PlayerInfoData(profile, ping, EnumGamemode.getById(gamemode.getValue()), IChatBaseComponent.ChatSerializer.jsonToComponent(ComponentSerializer.toString(displayName)));
//                ACTION.set(packet, EnumPlayerInfoAction.ADD_PLAYER);
//                List<PacketPlayOutPlayerInfo.PlayerInfoData> list = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) INFO_LIST.get(packet);
//                list.add(pid);
//            } catch (IllegalArgumentException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            return packet;
        }

        public static final PacketPlayOutPlayerInfo getRename(GameProfile profile, TextComponent displayName) {
            SavagePlayerInfoOut packet = new SavagePlayerInfoOut(EnumPlayerInfoAction.UPDATE_DISPLAY_NAME);
            packet.setPlayerInfoData(Collections.singletonList(new SavagePlayerInfoOut.SavagePlayerInfoData(profile, 0,
                    null,
                    IChatBaseComponent.ChatSerializer.jsonToComponent(ComponentSerializer.toString(displayName)))));
            return packet.build();
//            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
//            try {
//                PacketPlayOutPlayerInfo.PlayerInfoData pid = packet.new PlayerInfoData(profile, ping, EnumGamemode.getById(gamemode.getValue()), IChatBaseComponent.ChatSerializer.jsonToComponent(ComponentSerializer.toString(displayName)));
//                ACTION.set(packet, EnumPlayerInfoAction.ADD_PLAYER);
//                List<PacketPlayOutPlayerInfo.PlayerInfoData> list = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) INFO_LIST.get(packet);
//                list.add(pid);
//            } catch (IllegalArgumentException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            return packet;
        }


        public static final PacketPlayOutPlayerInfo getRemove(GameProfile profile) {
            SavagePlayerInfoOut packet = new SavagePlayerInfoOut(EnumPlayerInfoAction.REMOVE_PLAYER);
            packet.setPlayerInfoData(Collections.singletonList(new SavagePlayerInfoOut.SavagePlayerInfoData(profile, 0, null, null)));
            return packet.build();
//            try {
//                PacketPlayOutPlayerInfo.PlayerInfoData pid = packet.new PlayerInfoData(profile, 0, null, null);
//                ACTION.set(packet, EnumPlayerInfoAction.REMOVE_PLAYER);
//                List<PacketPlayOutPlayerInfo.PlayerInfoData> list = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) INFO_LIST.get(packet);
//                list.add(pid);
//            } catch (IllegalArgumentException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            return packet;
        }
    }
*/
}
