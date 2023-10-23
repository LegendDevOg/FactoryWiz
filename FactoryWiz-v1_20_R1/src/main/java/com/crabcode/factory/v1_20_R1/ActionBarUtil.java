package com.crabcode.factory.v1_20_R1;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * @since September 23, 2019
 * @author Andavin
 */
class ActionBarUtil extends com.crabcode.factory.util.ActionBarUtil {

    @Override
    protected void sendMessage(Player player, String message) {
        ((CraftPlayer) player).getHandle().connection.send(
                new ClientboundSetActionBarTextPacket(Component.literal(message)));
    }
}

