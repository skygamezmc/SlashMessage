package me.skygamez.slashmessage.utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class PlayerUtils {

    public static UUID getUUID(String playerName) {
        ProxiedPlayer onlinePlayer = ProxyServer.getInstance().getPlayer(playerName);

        if (onlinePlayer != null) {
            return onlinePlayer.getUniqueId();
        }
        return null;
    }

    public static Boolean hasPlayerJoinedBefore(String playerName) {
        return false;
    }
}
