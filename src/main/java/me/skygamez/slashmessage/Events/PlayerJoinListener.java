package me.skygamez.slashmessage.Events;

import com.google.gson.*;
import me.skygamez.slashmessage.utils.JSON;
import me.skygamez.slashmessage.SlashMessage;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final Gson gson = new Gson();

    private SlashMessage slashMessage;

    public PlayerJoinListener(SlashMessage slashMessage) {
        this.slashMessage = slashMessage;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        String playerName = event.getPlayer().getName();
        UUID playerUUID = event.getPlayer().getUniqueId();

        JsonArray jsonArray = loadJsonData();
        JsonObject jsonObject = new JsonObject();

        for (JsonElement element : jsonArray) {
            JsonObject entry = element.getAsJsonObject();
            String uuidString = entry.get("uuid").getAsString();
            String name = entry.get("name").getAsString();
            jsonObject.addProperty(uuidString, name);
        }

        if (jsonObject.has(playerUUID.toString())) {
            String existingPlayerName = jsonObject.get(playerUUID.toString()).getAsString();

            // Check if the player name has changed
            if (!existingPlayerName.equals(playerName)) {
                // Update the player name in the JSON data
                jsonObject.addProperty(playerUUID.toString(), playerName);
                JSON.saveJsonData(jsonObject, slashMessage.playerUUIDFile);
            }
        } else {
            // UUID not found, add a new entry
            JsonObject newEntry = new JsonObject();
            newEntry.addProperty("uuid", playerUUID.toString());
            newEntry.addProperty("name", playerName);
            jsonArray.add(newEntry);
            JSON.saveJsonArrayData(jsonArray, slashMessage.playerUUIDFile);
        }
    }

    private JsonArray loadJsonData() {
        try (Reader reader = new FileReader(slashMessage.playerUUIDFile)) {
            return JsonParser.parseReader(reader).getAsJsonArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonArray();
        }
    }

}
