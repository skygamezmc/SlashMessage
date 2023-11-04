package me.skygamez.slashmessage.Commands;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import me.skygamez.slashmessage.Functions.Formatting;
import me.skygamez.slashmessage.Functions.JSON;
import me.skygamez.slashmessage.SlashMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

public class MessageSettingsCommand extends Command {

    public Configuration config;
    public SlashMessage slashMessage;
    public int version;

    private BungeeAudiences adventure;

    MiniMessage mm;


    public MessageSettingsCommand(SlashMessage slashMessage, Configuration config, BungeeAudiences bungeeAudiences, String[] Aliases) {
        super("settings", "slashmessage.settings", Aliases);
        this.config = config;
        this.slashMessage = slashMessage;
        this.version = slashMessage.version;
        adventure = bungeeAudiences;
        mm = slashMessage.mm;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Audience player = adventure.sender(sender);

        if (args.length == 0) {
            player.sendMessage(Formatting.messageFormat("<red>Usage: /messagesettings <action> [player]"));
            return;
        }

        UUID playerUUID = ProxyServer.getInstance().getPlayer(sender.getName()).getUniqueId();

        // Block logic
        if (args[0].equals("block")) {
            if (args.length == 1) {
                player.sendMessage(Formatting.messageFormat("<red>Usage: /messagesettings block <player>"));
                return;
            }

            JsonArray playerUUIDs;
            try {
                playerUUIDs = JSON.parseJsonFromFile(slashMessage.playerUUIDFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String targetUUIDString = JSON.getJsonFromKey(playerUUIDs, "name", "uuid", args[1]);

            System.out.println(targetUUIDString);
            System.out.println(args[1]);

            if (targetUUIDString == null) {
                player.sendMessage(Formatting.messageFormat("<red>This player has not played before!"));
                return;
            }

            UUID targetUUID = UUID.fromString(targetUUIDString);

            Set<UUID> blockedUsers = slashMessage.userBlockedByUser.computeIfAbsent(ProxyServer.getInstance().getPlayer(sender.getName()).getUniqueId(), k -> new HashSet<>());

            if (slashMessage.userBlockedByUser.get(playerUUID).contains(targetUUID)) {
                player.sendMessage(Formatting.messageFormat("<red>You already blocked this player!"));
                return;
            }

            blockedUsers.add(targetUUID);

            JSON.saveJsonData(JSON.hashMapToJson(slashMessage.userBlockedByUser), slashMessage.playerBlockedDataFile);

            player.sendMessage(Formatting.messageFormat("<green>Successfully blocked " + args[1]));
            return;
        }

        if (args[0].equals("unblock")) {
            if (args.length == 1) {
                player.sendMessage(Formatting.messageFormat("<red>Usage: /messagesettings unblock <player>"));
                return;
            }

            JsonArray playerUUIDs;
            try {
                playerUUIDs = JSON.parseJsonFromFile(slashMessage.playerUUIDFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String targetUUIDString = JSON.getJsonFromKey(playerUUIDs, "name", "uuid", args[1]);

            System.out.println(targetUUIDString);
            System.out.println(args[1]);

            if (targetUUIDString == null) {
                player.sendMessage(Formatting.messageFormat("<red>This player has not played before!"));
                return;
            }

            UUID targetUUID = UUID.fromString(targetUUIDString);

            Set<UUID> blockedUsers = slashMessage.userBlockedByUser.computeIfAbsent(ProxyServer.getInstance().getPlayer(sender.getName()).getUniqueId(), k -> new HashSet<>());

            if (!slashMessage.userBlockedByUser.get(playerUUID).contains(targetUUID)) {
                player.sendMessage(Formatting.messageFormat("<red>You haven't blocked this player!"));
                return;
            }

            blockedUsers.remove(targetUUID);

            JSON.saveJsonData(JSON.hashMapToJson(slashMessage.userBlockedByUser), slashMessage.playerBlockedDataFile);

            player.sendMessage(Formatting.messageFormat("<green>Successfully unblocked " + args[1]));
            return;
        }

        if (args[0].equals("toggle")) {

            if (slashMessage.messagingDisabledUsers.contains(playerUUID)) {
                slashMessage.messagingDisabledUsers.remove(playerUUID);
                player.sendMessage(Formatting.messageFormat("<green>Messages enabled"));
                return;
            }

            slashMessage.messagingDisabledUsers.add(playerUUID);
            player.sendMessage(Formatting.messageFormat("<green>Messages disabled"));
            return;

        }

        if (args[0].equals("debug")) {
            System.err.println("-----------DEBUG - SLASHMESSAGE-----------");
            System.err.println(" ");
            System.err.println("--JSON--");
            System.err.println(" ");
            try {
                System.err.println("UUID FILE: " + JSON.parseJsonFromFile(slashMessage.playerUUIDFile));
            } catch (IOException exception) {
                exception.printStackTrace();
            };
            System.err.println(" ");
            System.err.println("--LIST--");
            System.err.println(" ");
            System.err.println("USERS WHOM DISABLED MESSAGES: " + slashMessage.messagingDisabledUsers);
            System.err.println("BLOCKED USERS: ");
            for (UUID blockedUser : slashMessage.userBlockedByUser.keySet()) {
                System.err.println("User: " + blockedUser + " Blocked users: " + slashMessage.userBlockedByUser.get(blockedUser));
            }
            System.err.println(" ");
            System.err.println("------------------DEBUG END----------------");
        }

    }
}
