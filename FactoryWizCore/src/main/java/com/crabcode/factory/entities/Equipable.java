package com.crabcode.factory.entities;

import org.bukkit.inventory.ItemStack;

public interface Equipable {

    public ItemStack getHelmet();

    public ItemStack getChestplate();

    public ItemStack getLeggings();

    public ItemStack getBoots();

    public ItemStack getOffHand();

    public ItemStack getMainHand();

    boolean[] getEquipmentUpdates();

    public Equipable setHelmet(ItemStack item);

    public Equipable setChestplate(ItemStack item);

    public Equipable setLeggings(ItemStack item);

    public Equipable setBoots(ItemStack item);

    public Equipable setMainHand(ItemStack item);

    public Equipable setOffHand(ItemStack item);

    public default Equipable updateSlot(EquipSlot slot) {
        getEquipmentUpdates()[slot.ordinal()] = true;
        return this;
    }

    public enum EquipSlot {
        MAINHAND, OFFHAND, BOOTS, LEGGINGS, CHESTPLATE, HELMET;
    }

}

