package com.crabcode.factory.entities;

import org.bukkit.inventory.ItemStack;

public interface Equipable {

    public ItemStack getHelmet();

    public ItemStack getChestplate();

    public ItemStack getLeggings();

    public ItemStack getBoots();

    public ItemStack getOffHand();

    public ItemStack getMainHand();


    public Equipable setHelmet(ItemStack item);

    public Equipable setChestplate(ItemStack item);

    public Equipable setLeggings(ItemStack item);

    public Equipable setBoots(ItemStack item);

    public Equipable setMainHand(ItemStack item);

    public Equipable setOffHand(ItemStack item);


    public enum EquipSlot {
        MAINHAND, OFFHAND, BOOTS, LEGGINGS, CHESTPLATE, HELMET;
    }

}

