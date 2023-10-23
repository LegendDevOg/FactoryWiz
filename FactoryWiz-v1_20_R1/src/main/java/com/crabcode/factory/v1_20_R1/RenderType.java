package com.crabcode.factory.v1_20_R1;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

public enum RenderType {
    AREA_EFFECT_CLOUD(EntityType.AREA_EFFECT_CLOUD),
    ARMOR_STAND(EntityType.ARMOR_STAND),
    ARROW(EntityType.ARROW),
    BAT(EntityType.BAT),
    BLAZE(EntityType.BLAZE),
    BOAT(EntityType.BOAT),
    CAT(EntityType.CAT),
    CAVE_SPIDER(EntityType.CAVE_SPIDER),
    CHICKEN(EntityType.CHICKEN),
    COD(EntityType.COD),
    COW(EntityType.COW),
    CREEPER(EntityType.CREEPER),
    DONKEY(EntityType.DONKEY),
    DOLPHIN(EntityType.DOLPHIN),
    DRAGON_FIREBALL(EntityType.DRAGON_FIREBALL),
    DROWNED(EntityType.DROWNED),
    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN),
    END_CRYSTAL(EntityType.END_CRYSTAL),
    ENDER_DRAGON(EntityType.ENDER_DRAGON),
    ENDERMAN(EntityType.ENDERMAN),
    ENDERMITE(EntityType.ENDERMITE),
    EVOKER_FANGS(EntityType.EVOKER_FANGS),
    EVOKER(EntityType.EVOKER),
    EXPERIENCE_ORB(EntityType.EXPERIENCE_ORB),
    EYE_OF_ENDER(EntityType.EYE_OF_ENDER),
    FALLING_BLOCK(EntityType.FALLING_BLOCK),
    FIREWORK_ROCKET(EntityType.FIREWORK_ROCKET),
    FOX(EntityType.FOX),
    GHAST(EntityType.GHAST),
    GIANT(EntityType.GIANT),
    GUARDIAN(EntityType.GUARDIAN),
    HORSE(EntityType.HORSE),
    HUSK(EntityType.HUSK),
    ILLUSIONER(EntityType.ILLUSIONER),
    ITEM(EntityType.ITEM),
    ITEM_FRAME(EntityType.ITEM_FRAME),
    FIREBALL(EntityType.FIREBALL),
    LEASH_KNOTT(EntityType.ARMOR_STAND),
    LLAMA(EntityType.LLAMA),
    LLAMA_SPIT(EntityType.LLAMA_SPIT),
    MAGMA_CUBE(EntityType.MAGMA_CUBE),
    MINECART(EntityType.MINECART),
    CHEST_MINECART(EntityType.CHEST_MINECART),
    COMMAND_BLOCK_MINECART(EntityType.COMMAND_BLOCK_MINECART),
    FURNACE_MINECART(EntityType.FURNACE_MINECART),
    HOPPER_MINECART(EntityType.HOPPER_MINECART),
    SPAWNER_MINECART(EntityType.SPAWNER_MINECART),
    TNT_MINECART(EntityType.TNT_MINECART),
    MULE(EntityType.MULE),
    MOOSHROOM(EntityType.MOOSHROOM),
    OCELOT(EntityType.OCELOT),
    PAINTING(EntityType.PAINTING),
    PANDA(EntityType.PANDA),
    PARROT(EntityType.PARROT),
    PIG(EntityType.PIG),
    PUFFERFISH(EntityType.PUFFERFISH),
    ZOMBIE_PIGMAN(EntityType.ZOMBIFIED_PIGLIN),
    POLAR_BEAR(EntityType.POLAR_BEAR),
    TNT(EntityType.TNT),
    RABBIT(EntityType.RABBIT),
    SALMON(EntityType.SALMON),
    SHEEP(EntityType.SHEEP),
    SHULKER(EntityType.SHULKER),
    SHULKER_BULLET(EntityType.SHULKER_BULLET),
    SILVERFISH(EntityType.SILVERFISH),
    SKELETON(EntityType.SKELETON),
    SKELETON_HORSE(EntityType.SKELETON_HORSE),
    SLIME(EntityType.SLIME),
    SMALL_FIREBALL(EntityType.SMALL_FIREBALL),
    SNOW_GOLEM(EntityType.SNOW_GOLEM),
    SNOWBALL(EntityType.SNOWBALL),
    SPECTRAL_ARROW(EntityType.SPECTRAL_ARROW),
    SPIDER(EntityType.SPIDER),
    SQUID(EntityType.SQUID),
    STRAY(EntityType.STRAY),
    TRADER_LLAMA(EntityType.TRADER_LLAMA),
    TROPICAL_FISH(EntityType.TROPICAL_FISH),
    TURTLE(EntityType.TURTLE),
    EGG(EntityType.EGG),
    ENDER_PEARL(EntityType.ENDER_PEARL),
    EXPERIENCE_BOTTLE(EntityType.EXPERIENCE_BOTTLE),
    POTION(EntityType.POTION),
    TRIDENT(EntityType.TRIDENT),
    VEX(EntityType.VEX),
    VILLAGER(EntityType.VILLAGER),
    IRON_GOLEM(EntityType.IRON_GOLEM),
    VINDICATOR(EntityType.VINDICATOR),
    PILLAGER(EntityType.PILLAGER),
    WANDERING_TRADER(EntityType.WANDERING_TRADER),
    WITCH(EntityType.WITCH),
    WITHER(EntityType.WITHER),
    WITHER_SKELETON(EntityType.WITHER_SKELETON),
    WITHER_SKULL(EntityType.WITHER_SKULL),
    WOLF(EntityType.WOLF),
    ZOMBIE(EntityType.ZOMBIE),
    ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER),
    PHANTOM(EntityType.PHANTOM),
    RAVAGER(EntityType.RAVAGER),
    LIGHTNING_BOLT(EntityType.LIGHTNING_BOLT),
    PLAYER(EntityType.PLAYER),
    FISHING_BOBBER(EntityType.FISHING_BOBBER),
    ;

    EntityType<?> type;

    RenderType(EntityType<?> type) {
        this.type = type;
    }

    public int getID() {
        return BuiltInRegistries.ENTITY_TYPE.getId(this.type);
    }

    public static RenderType getFromID(int id) {
        for (RenderType type : values()) {
            if (id == type.getID()) {
                return type;
            }
        }
        return null;
    }

}
