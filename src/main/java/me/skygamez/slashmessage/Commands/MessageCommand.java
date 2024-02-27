package me.skygamez.slashmessage.Commands;

import me.skygamez.slashmessage.utils.Formatting;
import me.skygamez.slashmessage.utils.Permissions;
import me.skygamez.slashmessage.SlashMessage;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MessageCommand extends Command {

    public Configuration config;
    public SlashMessage slashMessage;
    public int version;

    private BungeeAudiences adventure;
    MiniMessage mm;

    public MessageCommand(SlashMessage slashMessage, Configuration config, BungeeAudiences bungeeAudiences, String[] Aliases) {
        super("message", "slashmessage.message", Aliases);
        this.config = config;
        this.slashMessage = slashMessage;
        this.version = slashMessage.version;
        adventure = bungeeAudiences;
        mm = slashMessage.mm;
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

        if (args.length == 0 || args.length == 1){
            player.sendMessage(Formatting.messageFormat("<red>Usage: /message <player> <message>"));
            return;
        }

        ProxiedPlayer targetReceiver = ProxyServer.getInstance().getPlayer(args[0].toLowerCase());
        ProxiedPlayer proxiedSender = ProxyServer.getInstance().getPlayer(sender.getName());

        Audience receiver = adventure.player(targetReceiver);

        if (slashMessage.messagingBannedUsers.contains(proxiedSender.getUniqueId())) {
            player.sendMessage(Formatting.messageFormat(formatPlaceholders(
                    sender.getName(),
                    targetReceiver.getName(),
                    proxiedSender.getServer().getInfo().getName(),
                    targetReceiver.getServer().getInfo().getName(),
                    "",
                    config.getString("message-formats.message-ban"))));
            return;
        }

        if (!targetReceiver.isConnected()) {
            player.sendMessage(Formatting.messageFormat(config.getString("message-formats.receiver-offline")));
            return;
        }

        Set<UUID> blockedUsers = slashMessage.userBlockedByUser.get(targetReceiver.getUniqueId());

        if (blockedUsers != null && blockedUsers.contains(proxiedSender.getUniqueId())) {
            player.sendMessage(Formatting.messageFormat(formatPlaceholders(
                    sender.getName(),
                    targetReceiver.getName(),
                    proxiedSender.getServer().getInfo().getName(),
                    targetReceiver.getServer().getInfo().getName(),
                    "",
                    config.getString("message-formats.blocked-by-receiver"))));
            return;
        }

        if (slashMessage.messagingDisabledUsers.contains(targetReceiver.getUniqueId())) {
            player.sendMessage(Formatting.messageFormat(formatPlaceholders(
                    sender.getName(),
                    targetReceiver.getName(),
                    proxiedSender.getServer().getInfo().getName(),
                    targetReceiver.getServer().getInfo().getName(),
                    "",
                    config.getString("message-formats.messages-disabled-by-receiver"))));
            return;
        }

        String message = String.join(" ", Arrays.stream(args).skip(1).toArray(String[]::new));

        receiver.sendMessage(Formatting.messageFormat(formatPlaceholders(
                sender.getName(),
                targetReceiver.getName(),
                proxiedSender.getServer().getInfo().getName(),
                targetReceiver.getServer().getInfo().getName(),
                message,
                config.getString("message-formats.receive"))));

        player.sendMessage(Formatting.messageFormat(formatPlaceholders(
                sender.getName(),
                targetReceiver.getName(),
                proxiedSender.getServer().getInfo().getName(),
                targetReceiver.getServer().getInfo().getName(),
                message,
                config.getString("message-formats.send"))));
    }


    private String formatPlaceholders(String sender, String receiver, String senderServer, String receiverServer, String message, String textToFormat) {
        textToFormat = textToFormat.replace("$SENDER$", sender)
                .replace("$RECEIVER$", receiver)
                .replace("$SENDER-SERVER$", senderServer)
                .replace("$RECEIVER-SERVER$", receiverServer)
                .replace("$MESSAGE$", message)
                .replace("$TIMESTAMP$", Formatting.DateFormatter(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));

        return textToFormat;
    }
}
