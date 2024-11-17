package me.skygamez.slashmessage.Commands;

import me.skygamez.slashmessage.SlashMessage;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;

public class MessageSpyCommand extends Command implements TabExecutor {

    public Configuration config;
    public SlashMessage slashMessage;
    public int version;

    private BungeeAudiences adventure;
    MiniMessage mm;

    public MessageSpyCommand(SlashMessage slashMessage, Configuration config, BungeeAudiences bungeeAudiences, String[] Aliases) {
        super("message", "slashmessage.spy", Aliases);
        this.config = config;
        this.slashMessage = slashMessage;
        this.version = slashMessage.version;
        adventure = bungeeAudiences;
        mm = slashMessage.mm;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
