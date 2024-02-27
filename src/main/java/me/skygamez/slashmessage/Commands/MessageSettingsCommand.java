package me.skygamez.slashmessage.Commands;

import com.google.gson.JsonArray;
import me.skygamez.slashmessage.utils.Formatting;
import me.skygamez.slashmessage.utils.JSON;
import me.skygamez.slashmessage.SlashMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MessageSettingsCommand extends Command implements TabExecutor {

    public Configuration config;
    public SlashMessage slashMessage;
    public int version;

    private BungeeAudiences adventure;

    MiniMessage mm;


    public MessageSettingsCommand(SlashMessage slashMessage, Configuration config, BungeeAudiences bungeeAudiences, String[] Aliases) {
        super("messagesettings", "slashmessage.settings", Aliases);
        this.config = config;
        this.slashMessage = slashMessage;
        this.version = slashMessage.version;
        adventure = bungeeAudiences;
        mm = slashMessage.mm;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("toggle");
            completions.add("block");
            completions.add("unblock");
            completions.add("blocked-users");
        }
        return completions;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Audience player = adventure.sender(sender);

        if (args.length == 0) {
            //player.sendMessage(Formatting.messageFormat("<red>Usage: /messagesettings <action> [player]"));
            player.sendMessage(Formatting.messageFormat("<green><bold><underlined>Message Settings"));
            player.sendMessage(Formatting.messageFormat("<reset>"));
            player.sendMessage(Formatting.messageFormat("<light_blue><underlined>Possible Commands:"));
            player.sendMessage(Formatting.messageFormat("<gray>» <light_purple>/messagesettings toggle"));
            player.sendMessage(Formatting.messageFormat("<gray>» <light_purple>/messagesettings block [player]"));
            player.sendMessage(Formatting.messageFormat("<gray>» <light_purple>/messagesettings unblock [player]"));
            player.sendMessage(Formatting.messageFormat("<gray>» <light_purple>/messagesettings blocked-users"));
            return;
        }

        UUID playerUUID = ProxyServer.getInstance().getPlayer(sender.getName()).getUniqueId();

        // Block logic
        if (args[0].equalsIgnoreCase("block")) {
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

            String targetUUIDString = JSON.getJsonFromKey(playerUUIDs, "name", "uuid", false, args[1]);


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

        if (args[0].equalsIgnoreCase("unblock")) {
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

            String targetUUIDString = JSON.getJsonFromKey(playerUUIDs, "name", "uuid", false, args[1]);

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

        if (args[0].equalsIgnoreCase("blocked-users")) {
            List<UUID> blockedUsersUUID;
            List<String> blockedUsers;

            JsonArray playerUUIDs;
            try {
                playerUUIDs = JSON.parseJsonFromFile(slashMessage.playerUUIDFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            blockedUsersUUID = new ArrayList<>(slashMessage.userBlockedByUser.get(playerUUID));

            blockedUsers = blockedUsersUUID.stream()
                    .map(element -> JSON.getJsonFromKey(playerUUIDs, "uuid", "name", true, String.valueOf(element)))
                    .collect(Collectors.toList());

            player.sendMessage(Formatting.messageFormat("<green>You have blocked <gray>" + blockedUsers.size() + "<green> user(s)."));
            player.sendMessage(Formatting.messageFormat("<green>Your blocked users: " + blockedUsers.stream().map(Object::toString).collect(Collectors.joining(", "))));
            return;
        }

        if (args[0].equalsIgnoreCase("toggle")) {

            if (slashMessage.messagingDisabledUsers.contains(playerUUID)) {
                slashMessage.messagingDisabledUsers.remove(playerUUID);
                player.sendMessage(Formatting.messageFormat("<green>Messages enabled"));
                return;
            }

            slashMessage.messagingDisabledUsers.add(playerUUID);
            player.sendMessage(Formatting.messageFormat("<green>Messages disabled"));

            JSON.saveJsonData(JSON.arrayListToJson((ArrayList<?>) slashMessage.messagingDisabledUsers), slashMessage.playerDisabledDataFile);
            return;
        }

        if (args[0].equalsIgnoreCase("debug")) {
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
