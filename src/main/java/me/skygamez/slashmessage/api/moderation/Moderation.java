package me.skygamez.slashmessage.api.moderation;

import me.skygamez.slashmessage.SlashMessage;
import me.skygamez.slashmessage.utils.Formatting;
import me.skygamez.slashmessage.utils.JSON;

import java.util.ArrayList;
import java.util.UUID;

public class Moderation {

    SlashMessage plugin;
    public Moderation(SlashMessage plugin) {
        this.plugin = plugin;
    }

    public void ban(UUID playerUUID) {
        if (plugin.messagingBannedUsers.contains(playerUUID)) {
            throw new IllegalStateException("Player with UUID " + playerUUID + " is already banned.");
        }
        plugin.messagingBannedUsers.add(playerUUID);
        JSON.saveJsonArrayData(JSON.arrayListToJson((ArrayList<?>) plugin.messagingBannedUsers), plugin.playerBannedDataFile);
    }

    public void unban(UUID playerUUID) {
        if (!plugin.messagingBannedUsers.contains(playerUUID)) {
            throw new IllegalStateException("Player with UUID " + playerUUID + " is not banned.");
        }
        plugin.messagingBannedUsers.remove(playerUUID);
        JSON.saveJsonArrayData(JSON.arrayListToJson((ArrayList<?>) plugin.messagingBannedUsers), plugin.playerBannedDataFile);
    }

    public boolean checkIfBanned(UUID playerUUID) {
        return plugin.messagingBannedUsers.contains(playerUUID);
    }
}

