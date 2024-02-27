package me.skygamez.slashmessage.utils;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Permissions {

    /**
     * Check if a user has a permission
     *
     * @param sender     The command sender to check
     * @param permission       The permission to check
     * @return Returns a boolean
     */
    public static boolean CheckPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }


    /**
     * Check if a user has a permission
     *
     * @param proxiedPlayer     The ProxiedPlayer to check
     * @param permission       The permission to check
     * @return Returns a boolean
     */
    public static boolean CheckPermission(ProxiedPlayer proxiedPlayer, String permission) {
        return proxiedPlayer.hasPermission(permission);
    }
}
