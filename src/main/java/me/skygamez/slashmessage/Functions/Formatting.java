package me.skygamez.slashmessage.Functions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatting {
    private static final Pattern hexColorPattern = Pattern.compile("#[a-fA-F0-9]{6}");
    private static final Map<String, String> colorMap = new HashMap<>();

    private static final MiniMessage mm = MiniMessage.miniMessage();

    //Assign legacy color values to their matching Kyori color
    static {
        colorMap.put("&0", "<black>");
        colorMap.put("&1", "<dark_blue>");
        colorMap.put("&2", "<dark_green>");
        colorMap.put("&3", "<dark_aqua>");
        colorMap.put("&4", "<dark_red>");
        colorMap.put("&5", "<dark_purple>");
        colorMap.put("&6", "<gold>");
        colorMap.put("&7", "<gray>");
        colorMap.put("&8", "<dark_gray>");
        colorMap.put("&9", "<blue>");
        colorMap.put("&a", "<green>");
        colorMap.put("&b", "<aqua>");
        colorMap.put("&c", "<red>");
        colorMap.put("&d", "<light_purple>");
        colorMap.put("&e", "<yellow>");
        colorMap.put("&f", "<white>");
        colorMap.put("&k", "<obfuscated>");
        colorMap.put("&l", "<bold>");
        colorMap.put("&m", "<strikethrough>");
        colorMap.put("&n", "<underlined>");
        colorMap.put("&o", "<italic>");
        colorMap.put("&r", "<reset>");
    }


    /**
     * Format a message with HexCodes and miniMessage
     *
     * @param text     The text to format
     * @return Returns a MiniMessage Component
     */
    public static Component messageFormat(String text) {
        //Match text legacy colors and replace them with Kyori values
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        //Match text Hexcode usage and assign it accordingly
        Matcher matcher = hexColorPattern.matcher(text);
        while (matcher.find()) {
            String color = text.substring(matcher.start(), matcher.end());
            text = text.replace(color, "<" + color + ">");
        }

        return mm.deserialize(text);
    }

    public static String DateFormatter(long timeInMillis, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        return dateFormat.format(new Date(timeInMillis));
    }
}
