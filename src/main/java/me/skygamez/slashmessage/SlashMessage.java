package me.skygamez.slashmessage;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import me.skygamez.slashmessage.Commands.MessageAdminCommand;
import me.skygamez.slashmessage.Commands.MessageCommand;
import me.skygamez.slashmessage.Commands.MessageSettingsCommand;
import me.skygamez.slashmessage.Events.PlayerJoinListener;
import me.skygamez.slashmessage.api.API;
import me.skygamez.slashmessage.utils.JSON;
import me.skygamez.slashmessage.utils.UpdateChecker;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import me.skygamez.slashmessage.Metrics.Metrics;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public final class SlashMessage extends Plugin {

    public API api;


    public HashMap<UUID, Set<UUID>> userBlockedByUser = new HashMap<>();
    public List<UUID> messagingDisabledUsers = new ArrayList<>();

    public List<UUID> messagingBannedUsers = new ArrayList<>();


    public Configuration config;
    public int version;

    private BungeeAudiences adventure;
    public MiniMessage mm;

    public File playerUUIDFile;
    public File playerBlockedDataFile;
    public File playerDisabledDataFile;

    public File playerBannedDataFile;

    @NonNull
    public BungeeAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Cannot retrieve audience provider while plugin is not enabled");
        }
        return this.adventure;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        this.adventure = BungeeAudiences.create(this);
        mm = MiniMessage.miniMessage();

        //Config loading logic
        try {
            loadConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if config version is stable
        if (config.getFloat("Config-Version") != 1.0f) {
            getLogger().info("§4------------------------------------");
            getLogger().info("");
            getLogger().info("§7          * §eWARNING!§7*");
            getLogger().info("§7     * §cConfig Version Mismatch!§7*");
            getLogger().info("§7   * §cPlease regenerate your config!§7 *");
            getLogger().info("");
            getLogger().info("§4------------------------------------");

            return;
        }

        int pluginId = 19884;
        Metrics metrics = new Metrics(this, pluginId);

        // Command Instancing logic

        String[] adminAliases = config.getStringList("commands.messageadmin.aliases").toArray(new String[0]);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new MessageAdminCommand(this, config, adventure, adminAliases));

        String[] msgAliases = config.getStringList("commands.message.aliases").toArray(new String[0]);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new MessageCommand(this, config, adventure, msgAliases));

        String[] settingAliases = config.getStringList("commands.messagesettings.aliases").toArray(new String[0]);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new MessageSettingsCommand(this, config, adventure, settingAliases));


        // Event Instancing Logic
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerJoinListener(this));

        //uuid file
        playerUUIDFile = new File(getDataFolder(), "player_uuids.json");
        if (!playerUUIDFile.exists()) {
            try {
                playerUUIDFile.createNewFile();
                FileWriter writer = new FileWriter(playerBlockedDataFile);
                writer.write("[]");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //blocked file
        playerBlockedDataFile = new File(getDataFolder(), "player_blocked.json");
        if (!playerBlockedDataFile.exists()) {
            try {
                playerBlockedDataFile.createNewFile();
                FileWriter writer = new FileWriter(playerBlockedDataFile);
                writer.write("[]");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSON.blockJSONtoHashMap(playerBlockedDataFile, userBlockedByUser);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //disabled file
        playerDisabledDataFile = new File(getDataFolder(), "player_disabled.json");
        if (!playerDisabledDataFile.exists()) {
            try {
                playerDisabledDataFile.createNewFile();
                FileWriter writer = new FileWriter(playerDisabledDataFile);
                writer.write("[]");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSON.JSONArrayToArrayList(JSON.parseJsonFromFile(playerDisabledDataFile), (ArrayList<UUID>) messagingDisabledUsers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        playerBannedDataFile = new File(getDataFolder(), "player_banned.json");
        if (!playerBannedDataFile.exists()) {
            try {
                playerBannedDataFile.createNewFile();
                FileWriter writer = new FileWriter(playerBannedDataFile);
                writer.write("[]");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSON.JSONArrayToArrayList(JSON.parseJsonFromFile(playerBannedDataFile), (ArrayList<UUID>) messagingBannedUsers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        api = new API(this);

        try {
            UpdateChecker updateChecker = new UpdateChecker(this, 116353);
            if (updateChecker.isUpdateRequired()) {
                getLogger().info("§b--------------------------------");
                getLogger().info("");
                getLogger().info("§7  * §9SlashMessage by SkyGameZ §7*");
                getLogger().info("§7     * §9Update available! §7*");
                getLogger().info("");
                getLogger().info("§b--------------------------------");
            } else {
                getLogger().info("§b--------------------------------");
                getLogger().info("");
                getLogger().info("§7  * §9SlashMessage by SkyGameZ §7*");
                getLogger().info("§7      * §9Version " + getDescription().getVersion() + " §7*");
                getLogger().info("");
                getLogger().info("§b--------------------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Configuration logic
    public void loadConfiguration() throws IOException {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            getDataFolder().mkdir();
            Files.copy(this.getResourceAsStream("config.yml"),
                    configFile.toPath());
            getLogger().info(ChatColor.translateAlternateColorCodes('&', "&aConfig Created"));
        }
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
    }


    private JsonArray loadJsonData(File file) {
        try (Reader reader = new FileReader(file)) {
            return JsonParser.parseReader(reader).getAsJsonArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonArray();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        getLogger().info("§b--------------------------------");
        getLogger().info("");
        getLogger().info("§7  * §9Shutting down SlashMessage §7*");
        getLogger().info("");
        getLogger().info("§b--------------------------------");

        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
}
