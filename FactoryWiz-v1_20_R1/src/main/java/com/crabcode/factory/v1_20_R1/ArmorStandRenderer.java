package com.crabcode.factory.v1_20_R1;

import com.crabcode.factory.data.io.DataIn;
import com.crabcode.factory.data.io.DataOut;
import com.crabcode.factory.entities.Equipable;
import com.crabcode.factory.util.InventoryUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.List;

public class ArmorStandRenderer extends LivingEntityRenderer implements com.crabcode.factory.entities.entity.ArmorStandRenderer {

    private static final Vector3f DEFAULT_HEAD_POSE = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Vector3f DEFAULT_BODY_POSE = new Vector3f(0.0f, 0.0f, 0.0f);
    private static final Vector3f DEFAULT_LEFT_ARM_POSE = new Vector3f(-10.0f, 0.0f, -10.0f);
    private static final Vector3f DEFAULT_RIGHT_ARM_POSE = new Vector3f(-15.0f, 0.0f, 10.0f);
    private static final Vector3f DEFAULT_LEFT_LEG_POSE = new Vector3f(-1.0f, 0.0f, -1.0f);
    private static final Vector3f DEFAULT_RIGHT_LEG_POSE = new Vector3f(1.0f, 0.0f, 1.0f);

    private static final EntityDataAccessor<Byte> MASK = NMSDataWatcherObject.get(ArmorStand.class, "bC");
    private static final EntityDataAccessor<Vector3f> HEAD_POSE = NMSDataWatcherObject.get(ArmorStand.class, "bD");
    private static final EntityDataAccessor<Vector3f> BODY_POSE = NMSDataWatcherObject.get(ArmorStand.class, "bE");
    private static final EntityDataAccessor<Vector3f> LEFT_ARM_POSE = NMSDataWatcherObject.get(ArmorStand.class, "bF");
    private static final EntityDataAccessor<Vector3f> RIGHT_ARM_POSE = NMSDataWatcherObject.get(ArmorStand.class, "bG");
    private static final EntityDataAccessor<Vector3f> LEFT_LEG_POSE = NMSDataWatcherObject.get(ArmorStand.class, "bH");
    private static final EntityDataAccessor<Vector3f> RIGHT_LEG_POSE = NMSDataWatcherObject.get(ArmorStand.class, "bI");

    private static final byte SMALL_FLAG = 0x01;
    private static final byte ARMS_FLAG = 0x04;
    private static final byte NO_BASEPLATE_FLAG = 0x08;
    private static final byte MARKER_FLAG = 0x10;

    private byte mask = 0;

    private boolean isSmall = false;
    private boolean hasArms = false;
    private boolean showBaseplate = true;
    private boolean isMarker = false;

    private Vector3f headPose = DEFAULT_HEAD_POSE;
    private Vector3f bodyPose = DEFAULT_BODY_POSE;
    private Vector3f leftArmPose = DEFAULT_LEFT_ARM_POSE;
    private Vector3f rightArmPose = DEFAULT_RIGHT_ARM_POSE;
    private Vector3f leftLegPose = DEFAULT_LEFT_LEG_POSE;
    private Vector3f rightLegPose = DEFAULT_RIGHT_LEG_POSE;

    public ArmorStandRenderer() {
        super();
        this.registerMetadata(MASK, this.mask);
        this.registerMetadata(HEAD_POSE, this.headPose);
        this.registerMetadata(BODY_POSE, this.bodyPose);
        this.registerMetadata(LEFT_ARM_POSE, this.leftArmPose);
        this.registerMetadata(RIGHT_ARM_POSE, this.rightArmPose);
        this.registerMetadata(LEFT_LEG_POSE, this.leftLegPose);
        this.registerMetadata(RIGHT_LEG_POSE, this.rightLegPose);

    }

    // Equipment
    private boolean[] equipmentUpdates = new boolean[6];
    private ItemStack[] equipment = new ItemStack[7];

    @Override
    public void serialize(DataOut out) {
        super.serialize(out);

        out.writeBoolean(false);

        ItemStack item = null;

        if (InventoryUtil.isValid(item = this.getHelmet())) {
            out.writeBoolean(true);
            byte[] dataBlob = ItemBridge.serialize(item);
            out.writeInt(dataBlob.length);
            out.writeBytes(dataBlob);
        } else {
            out.writeBoolean(false);
        }

        if (InventoryUtil.isValid(item = this.getChestplate())) {
            out.writeBoolean(true);
            byte[] dataBlob = ItemBridge.serialize(item);
            out.writeInt(dataBlob.length);
            out.writeBytes(dataBlob);
        } else {
            out.writeBoolean(false);
        }

        if (InventoryUtil.isValid(item = this.getLeggings())) {
            out.writeBoolean(true);
            byte[] dataBlob = ItemBridge.serialize(item);
            out.writeInt(dataBlob.length);
            out.writeBytes(dataBlob);
        } else {
            out.writeBoolean(false);
        }

        if (InventoryUtil.isValid(item = this.getBoots())) {
            out.writeBoolean(true);
            byte[] dataBlob = ItemBridge.serialize(item);
            out.writeInt(dataBlob.length);
            out.writeBytes(dataBlob);
        } else {
            out.writeBoolean(false);
        }

        if (InventoryUtil.isValid(item = this.getMainHand())) {
            out.writeBoolean(true);
            byte[] dataBlob = ItemBridge.serialize(item);
            out.writeInt(dataBlob.length);
            out.writeBytes(dataBlob);
        } else {
            out.writeBoolean(false);
        }

        if (InventoryUtil.isValid(item = this.getOffHand())) {
            out.writeBoolean(true);
            byte[] dataBlob = ItemBridge.serialize(item);
            out.writeInt(dataBlob.length);
            out.writeBytes(dataBlob);
        } else {
            out.writeBoolean(false);
        }
    }

    @Override
    public void deserialize(DataIn in) {
        super.deserialize(in);

        if (in.readBoolean()) {
            //TODO READ ARMOR STAND STUFF
        }

        if (in.readBoolean()) {
            byte[] data = in.readBytes(in.readInt());
            this.setHelmet(ItemBridge.deserialize(data));
        }
        if (in.readBoolean()) {
            byte[] data = in.readBytes(in.readInt());
            this.setChestplate(ItemBridge.deserialize(data));
        }
        if (in.readBoolean()) {
            byte[] data = in.readBytes(in.readInt());
            this.setLeggings(ItemBridge.deserialize(data));
        }
        if (in.readBoolean()) {
            byte[] data = in.readBytes(in.readInt());
            this.setBoots(ItemBridge.deserialize(data));
        }
        if (in.readBoolean()) {
            byte[] data = in.readBytes(in.readInt());
            this.setMainHand(ItemBridge.deserialize(data));
        }
        if (in.readBoolean()) {
            byte[] data = in.readBytes(in.readInt());
            this.setOffHand(ItemBridge.deserialize(data));
        }
    }


    @Override
    public com.crabcode.factory.entities.entity.ArmorStandRenderer setSmall(boolean small) {
        this.isSmall = small;
        byte b = mask;
        if (small) {
            b |= SMALL_FLAG;
        } else {
            b &= ~SMALL_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.ArmorStandRenderer setArms(boolean arms) {
        this.hasArms = arms;
        byte b = mask;
        if (arms) {
            b |= ARMS_FLAG;
        } else {
            b &= ~ARMS_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.ArmorStandRenderer setBaseplate(boolean baseplate) {
        this.showBaseplate = baseplate;
        byte b = mask;
        if (!baseplate) {
            b |= NO_BASEPLATE_FLAG;
        } else {
            b &= ~NO_BASEPLATE_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.ArmorStandRenderer setMarker(boolean marker) {
        this.isMarker = marker;
        byte b = mask;
        if (marker) {
            b |= MARKER_FLAG;
        } else {
            b &= ~MARKER_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.ArmorStandRenderer setHeadRotation(float yaw, float pitch, float roll) {
        this.headPose = new Vector3f(yaw, pitch, roll);
        this.set(HEAD_POSE, this.headPose);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.ArmorStandRenderer setBodyRotation(float yaw, float pitch, float roll) {
        this.bodyPose = new Vector3f(yaw, pitch, roll);
        this.set(BODY_POSE, this.bodyPose);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.ArmorStandRenderer setLeftArmRotation(float yaw, float pitch, float roll) {
        this.leftArmPose = new Vector3f(yaw, pitch, roll);
        this.set(LEFT_ARM_POSE, this.leftArmPose);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.ArmorStandRenderer setRightArmRotation(float yaw, float pitch, float roll) {
        this.rightArmPose = new Vector3f(yaw, pitch, roll);
        this.set(RIGHT_ARM_POSE, this.rightArmPose);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.ArmorStandRenderer setLeftLegRotation(float yaw, float pitch, float roll) {
        this.leftLegPose = new Vector3f(yaw, pitch, roll);
        this.set(LEFT_LEG_POSE, this.leftLegPose);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.ArmorStandRenderer setRightLegRotation(float yaw, float pitch, float roll) {
        this.rightLegPose = new Vector3f(yaw, pitch, roll);
        this.set(RIGHT_LEG_POSE, this.rightLegPose);
        return this.pushMetadataUpdate();
    }

    @Override
    public boolean isSmall() {
        return this.isSmall;
    }

    @Override
    public boolean hasArms() {
        return this.hasArms;
    }

    @Override
    public boolean hasBaseplate() {
        return this.showBaseplate;
    }

    @Override
    public boolean isMarker() {
        return this.isMarker;
    }

    @Override
    public Vector getHeadRotation() {
        return new Vector(this.headPose.x(), this.headPose.y(), this.headPose.z());
    }

    @Override
    public Vector getBodyRotation() {
        return new Vector(this.bodyPose.x(), this.bodyPose.y(), this.bodyPose.z());
    }


    @Override
    public Vector getLeftArmRotation() {
        return new Vector(this.leftArmPose.x(), this.leftArmPose.y(), this.leftArmPose.z());
    }

    @Override
    public Vector getRightArmRotation() {
        return new Vector(this.rightArmPose.x(), this.rightArmPose.y(), this.rightArmPose.z());
    }

    @Override
    public Vector getLeftLegRotation() {
        return new Vector(this.leftLegPose.x(), this.leftLegPose.y(), this.leftLegPose.z());
    }

    @Override
    public Vector getRightLegRotation() {
        return new Vector(this.rightLegPose.x(), this.rightLegPose.y(), this.rightLegPose.z());
    }

    @Override
    public RenderType getType() {
        return RenderType.ARMOR_STAND;
    }

    @Override
    public void spawnFor(Player player) {
        ((CraftPlayer) player).getHandle().connection.send(Packets.getObjectSpawn(this.getAttached(), RenderType.ARMOR_STAND));
        //((CraftPlayer) player).getHandle().playerConnection.sendPacket(Packets.getMetadata(this.getAttached(), this.getWatcher(), true));
        if (this instanceof Equipable) {

            ((CraftPlayer) player).getHandle().connection.send(Packets.getEquipment(this.getAttached(), this.equipment));

        }
        ((CraftPlayer) player).getHandle().connection.send(Packets.getTeleport(this.getAttached()));
    }

    @Override
    public void spawnFor(List<Player> player) {
        Packet p1 = Packets.getObjectSpawn(this.getAttached(), RenderType.ARMOR_STAND);
        //Packet p2 = Packets.getMetadata(this.getAttached(), this.getWatcher(), true);
        player.forEach(p -> {
            ((CraftPlayer) player).getHandle().connection.send(p1);
            //((CraftPlayer) player).getHandle().playerConnection.sendPacket(p2);
            ((CraftPlayer) player).getHandle().connection.send(Packets.getTeleport(this.getAttached()));
        });
    }

    @Override
    public void despawnFor(Player player) {
        ((CraftPlayer) player).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(this.getAttached().getEntityID()));
    }

    @Override
    public void despawnFor(List<Player> player) {
        player.forEach(p -> despawnFor(p));
    }

    @Override
    public ItemStack getHelmet() {
        return this.equipment[EquipSlot.HELMET.ordinal()];
    }

    @Override
    public ItemStack getChestplate() {
        return this.equipment[EquipSlot.CHESTPLATE.ordinal()];
    }

    @Override
    public ItemStack getLeggings() {
        return this.equipment[EquipSlot.LEGGINGS.ordinal()];
    }

    @Override
    public ItemStack getBoots() {
        return this.equipment[EquipSlot.BOOTS.ordinal()];
    }

    @Override
    public ItemStack getOffHand() {
        return this.equipment[EquipSlot.OFFHAND.ordinal()];
    }

    @Override
    public ItemStack getMainHand() {
        return this.equipment[EquipSlot.MAINHAND.ordinal()];
    }


    @Override
    public void setHelmet(ItemStack item) {
        this.equipment[EquipSlot.HELMET.ordinal()] = item;
        this.equipmentUpdates[EquipSlot.HELMET.ordinal()] = true;
    }

    @Override
    public Equipable setChestplate(ItemStack item) {
        this.equipment[EquipSlot.CHESTPLATE.ordinal()] = item;
        this.equipmentUpdates[EquipSlot.CHESTPLATE.ordinal()] = true;
        return this;
    }

    @Override
    public Equipable setLeggings(ItemStack item) {
        this.equipment[EquipSlot.LEGGINGS.ordinal()] = item;
        this.equipmentUpdates[EquipSlot.LEGGINGS.ordinal()] = true;
        return this;
    }

    @Override
    public Equipable setBoots(ItemStack item) {
        this.equipment[EquipSlot.BOOTS.ordinal()] = item;
        this.equipmentUpdates[EquipSlot.BOOTS.ordinal()] = true;
        return this;
    }

    @Override
    public Equipable setMainHand(ItemStack item) {
        this.equipment[EquipSlot.MAINHAND.ordinal()] = item;
        this.equipmentUpdates[EquipSlot.MAINHAND.ordinal()] = true;
        return this;
    }

    @Override
    public Equipable setOffHand(ItemStack item) {
        this.equipment[EquipSlot.OFFHAND.ordinal()] = item;
        this.equipmentUpdates[EquipSlot.OFFHAND.ordinal()] = true;
        return this;
    }

}
