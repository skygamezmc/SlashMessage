package me.skygamez.slashmessage.Commands;

import com.google.gson.JsonArray;
import me.skygamez.slashmessage.SlashMessage;
import me.skygamez.slashmessage.utils.Formatting;
import me.skygamez.slashmessage.utils.JSON;
import me.skygamez.slashmessage.utils.Permissions;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MessageAdminCommand extends Command {

    public Configuration config;
    public SlashMessage slashMessage;
    public int version;

    private BungeeAudiences adventure;


    public MessageAdminCommand(SlashMessage slashMessage, Configuration config, BungeeAudiences bungeeAudiences, String[] Aliases) {
        super("messageadmin", "slashmessage.admin", Aliases);
        this.config = config;
        this.slashMessage = slashMessage;
        this.version = slashMessage.version;
        adventure = bungeeAudiences;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Audience player = adventure.sender(sender);

        if(!Permissions.CheckPermission(sender, "slashmessage.message")) {
            if (config.getString("commands.message.permission-message").isEmpty()) {
                return;
            }
            player.sendMessage(Formatting.messageFormat(config.getString("commands.message.permission-message")));
            return;
        }
        if (args.length == 0) {
            player.sendMessage(Formatting.messageFormat("<green><bold><underlined>Message Admin"));
            player.sendMessage(Formatting.messageFormat("<reset>"));
            player.sendMessage(Formatting.messageFormat("<light_blue><underlined>Possible Commands:"));
            player.sendMessage(Formatting.messageFormat("<gray>» <light_purple>/messageadmin ban [player]"));
            player.sendMessage(Formatting.messageFormat("<gray>» <light_purple>/messageadmin unban [player]"));
            return;
        }

        if (args[0].equalsIgnoreCase("ban")) {
            if (args.length == 1) {
                player.sendMessage(Formatting.messageFormat("<red>Usage: /messageadmin ban <player>"));
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

            if (slashMessage.messagingBannedUsers.contains(targetUUID)) {
                player.sendMessage(Formatting.messageFormat("<red>Player is already message-banned!"));
                return;
            }
            slashMessage.messagingDisabledUsers.add(targetUUID);
            player.sendMessage(Formatting.messageFormat("<green>Successfully message-banned player!"));

            JSON.saveJsonData(JSON.arrayListToJson((ArrayList<?>) slashMessage.messagingBannedUsers), slashMessage.playerBannedDataFile);
        }
    }
}
