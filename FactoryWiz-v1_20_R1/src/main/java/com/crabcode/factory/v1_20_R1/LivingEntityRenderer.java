package com.crabcode.factory.v1_20_R1;

import com.crabcode.factory.data.io.DataIn;
import com.crabcode.factory.data.io.DataOut;
import com.crabcode.factory.entities.Equipable;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class LivingEntityRenderer extends EntityRenderer implements com.crabcode.factory.entities.entity.LivingEntityRenderer {

    private static final EntityDataAccessor<Byte> MASK = NMSDataWatcherObject.get(LivingEntity.class, "u");
    private static final EntityDataAccessor<Float> HEALTH = NMSDataWatcherObject.get(LivingEntity.class, "bI");
    private static final EntityDataAccessor<Integer> POTION_COLOR = NMSDataWatcherObject.get(LivingEntity.class, "bJ");
    private static final EntityDataAccessor<Boolean> POTION_AMBIENT = NMSDataWatcherObject.get(LivingEntity.class, "bK");
    private static final EntityDataAccessor<Integer> ARROW_COUNT = NMSDataWatcherObject.get(LivingEntity.class, "bL");

    private static final byte HAND_ACTIVE_FLAG = 0x01;
    private static final byte MAIN_HAND_FLAG = 0x02;
    private static final byte IN_RIPTIDE_FLAG = 0x04;

    private boolean handActive = false;
    private ActiveHand activeHand = ActiveHand.MAIN;
    private boolean inRiptideAttack = false;

    private float health = 1f;
    private int potionEffectColor = 0;
    private boolean ambientPotionEffect = false;
    private int arrowCount = 0;

    private byte mask = 0;

    public LivingEntityRenderer() {
        super();

        this.registerMetadata(MASK, mask);
        this.registerMetadata(HEALTH, health);
        this.registerMetadata(POTION_COLOR, this.potionEffectColor);
        this.registerMetadata(POTION_AMBIENT, this.ambientPotionEffect);
        this.registerMetadata(ARROW_COUNT, arrowCount);
    }

    @Override
    public void serialize(DataOut out) {
        super.serialize(out);
        out.writeBoolean(handActive);
        out.writeString(this.activeHand.name());
        out.writeBoolean(inRiptideAttack);

        out.writeFloat(health);
        out.writeInt(this.potionEffectColor);
        out.writeBoolean(ambientPotionEffect);
        out.writeInt(this.arrowCount);
    }

    @Override
    public void deserialize(DataIn in) {
        super.deserialize(in);
        this.setHandActive(in.readBoolean());
        this.setActiveHand(ActiveHand.valueOf(in.readString()));
        this.setInRiptideSpinAttack(in.readBoolean());

        this.setHealth(in.readFloat());
        this.setPotionEffectColor(in.readInt());
        this.ambientPotionEffect = in.readBoolean();
        this.setArrowCount(in.readInt());

        this.set(POTION_AMBIENT, this.ambientPotionEffect);
    }

    @Override
    public com.crabcode.factory.entities.entity.LivingEntityRenderer setHandActive(boolean handActive) {
        this.handActive = handActive;
        byte b = mask;
        if (handActive) {
            b |= HAND_ACTIVE_FLAG;
        } else {
            b &= ~HAND_ACTIVE_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.LivingEntityRenderer setHealth(float health) {
        this.health = health;
        this.set(HEALTH, health);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.LivingEntityRenderer setPotionEffectColor(int color) {
        this.potionEffectColor = color;
        this.set(POTION_COLOR, color);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.LivingEntityRenderer setArrowCount(int arrows) {
        this.arrowCount = arrows;
        this.set(ARROW_COUNT, arrows);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.LivingEntityRenderer setActiveHand(ActiveHand hand) {
        this.activeHand = hand;
        byte b = mask;
        if (hand == ActiveHand.MAIN) {
            b |= MAIN_HAND_FLAG;
        } else {
            b &= ~MAIN_HAND_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return this.pushMetadataUpdate();
    }

    @Override
    public com.crabcode.factory.entities.entity.LivingEntityRenderer setInRiptideSpinAttack(boolean riptideSpinAttack) {
        this.inRiptideAttack = riptideSpinAttack;
        byte b = mask;
        if (riptideSpinAttack) {
            b |= IN_RIPTIDE_FLAG;
        } else {
            b &= ~IN_RIPTIDE_FLAG;
        }
        mask = b;
        this.set(MASK, this.mask);
        return this.pushMetadataUpdate();
    }

    @Override
    public boolean isHandActive() {
        return this.handActive;
    }

    @Override
    public float getHealth() {
        return this.health;
    }

    @Override
    public int getActivePotionEffectColor() {
        return this.potionEffectColor;
    }

    @Override
    public int getArrowCount() {
        return this.arrowCount;
    }

    @Override
    public ActiveHand getActiveHand() {
        return this.activeHand;
    }

    @Override
    public boolean isInRiptideSpinAttack() {
        return this.inRiptideAttack;
    }

    @Override
    public void spawnFor(Player player) {
        ((CraftPlayer) player).getHandle().connection.send(Packets.getObjectSpawn(this.getAttached(), this.getType()));
        if (this instanceof Equipable) {
            ItemStack item = null;
            ItemStack[] equipment = new ItemStack[7];

            if ((item = ((Equipable) this).getHelmet()) != null) {
                equipment[Equipable.EquipSlot.HELMET.ordinal()] = item;
            }
            if ((item = ((Equipable) this).getChestplate()) != null) {
                equipment[Equipable.EquipSlot.CHESTPLATE.ordinal()] = item;
            }
            if ((item = ((Equipable) this).getLeggings()) != null) {
                equipment[Equipable.EquipSlot.LEGGINGS.ordinal()] = item;
            }
            if ((item = ((Equipable) this).getBoots()) != null) {
                equipment[Equipable.EquipSlot.BOOTS.ordinal()] = item;
            }
            if ((item = ((Equipable) this).getMainHand()) != null) {
                equipment[Equipable.EquipSlot.MAINHAND.ordinal()] = item;
            }
            if ((item = ((Equipable) this).getOffHand()) != null) {
                equipment[Equipable.EquipSlot.OFFHAND.ordinal()] = item;
            }
            ((CraftPlayer) player).getHandle().connection.send(Packets.getEquipment(this.getAttached(), equipment));

        }
        /*
        if (!this.mounts.isEmpty()) {
            int[] mounts = new int[this.mounts.size()];
            int ato = 0;
            for (int mounts2 : this.mounts) {
                if (ato < mounts.length) {
                    mounts[ato++] = mounts2;
                }
            }
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(Packets.getMount(this.getAttached(), mounts));
        }

         */
    }

    @Override
    public void spawnFor(List<Player> player) {
        Packet packet = Packets.getObjectSpawn(this.getAttached(), this.getType());
        player.forEach(p -> {
            ((CraftPlayer) player).getHandle().connection.send(packet);
        });
    }

    @Override
    public void despawnFor(Player player) {
        super.despawnFor(player);
        ((CraftPlayer) player).getHandle().connection.send(new ClientboundRemoveEntitiesPacket(this.getAttached().getEntityID()));
    }

    @Override
    public void despawnFor(List<Player> player) {
        player.forEach(p -> despawnFor(p));
    }

    public abstract RenderType getType();

}
