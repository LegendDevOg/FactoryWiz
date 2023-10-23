package com.crabcode.factory.v1_20_R1;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;

import static com.crabcode.factory.reflect.Reflection.findField;
import static com.crabcode.factory.reflect.Reflection.getFieldValue;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ItemBridge {

    private static final Field HANDLE = findField(CraftItemStack.class, "handle");

    protected static  byte[] serialize(ItemStack item) {

        checkArgument(item != null && item.getType() != Material.AIR);
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            NbtIo.writeCompressed(saveToNBT(item), stream);
            return stream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected static CompoundTag saveToNBT(ItemStack item) {
        net.minecraft.world.item.ItemStack nmsItem = getNmsItemStack(item);
        return nmsItem.save(new CompoundTag());
    }

    protected static <T> T getNmsItemStack(ItemStack item) {
        return CraftItemStack.class == item.getClass() ? getFieldValue(HANDLE, item) :
                (T) CraftItemStack.asNMSCopy(item);
    }

    protected static ItemStack deserialize(byte[] data) {

        checkNotNull(data);
        checkArgument(data.length != 0);
        try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            return createStack(NbtIo.readCompressed(stream));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static ItemStack createStack(Object tagObj) {
        return CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.of((CompoundTag) tagObj));
    }
}
