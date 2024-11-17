package me.skygamez.slashmessage.api;

import me.skygamez.slashmessage.SlashMessage;
import me.skygamez.slashmessage.api.moderation.Moderation;
import me.skygamez.slashmessage.api.party.Party;

public class API {
    SlashMessage plugin;
    public Moderation moderation;
    public Party party;
    public API (SlashMessage plugin) {
        this.plugin = plugin;

    }

     void initializeAPI() {
        moderation = new Moderation(plugin);
        party = new Party(plugin);
    }
}
