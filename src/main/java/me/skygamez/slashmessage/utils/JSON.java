package me.skygamez.slashmessage.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class JSON {

    public static JsonArray parseJsonFromFile(File file) throws IOException, FileNotFoundException {
        Gson gson = new Gson();
        FileReader fileReader = new FileReader(file);

        // Parse the JSON file into a JsonArray
        JsonArray jsonArray = JsonParser.parseReader(fileReader).getAsJsonArray();
        fileReader.close();

        return jsonArray;
    }

    public static String getJsonFromKey(JsonArray jsonArray, String keyField, String targetField, boolean caseSensitive, String key) {
        if (jsonArray == null || keyField == null || targetField == null || key == null) {
            return null;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

            if (jsonObject.has(keyField) && isKeyMatch(jsonObject.get(keyField).getAsString(), key, caseSensitive)) {
                if (jsonObject.has(targetField)) {
                    return jsonObject.get(targetField).getAsString();
                }
            }
        }
        // Key not found or target field not found in the matching object
        return null;
    }

    private static boolean isKeyMatch(String fieldKey, String key, boolean caseSensitive) {
        if (caseSensitive) {
            return fieldKey.equals(key);
        } else {
            return fieldKey.equalsIgnoreCase(key);
        }
    }

    public static JsonObject hashMapToJson(HashMap<?, ?> hashMap) {
        Gson gson = new Gson();

        return JsonParser.parseString(gson.toJson(hashMap)).getAsJsonObject();
    }

    public static JsonObject arrayListToJson(ArrayList<?> arrayList) {
        Gson gson = new Gson();

        return JsonParser.parseString(gson.toJson(arrayList)).getAsJsonObject();
    }

    public static void saveJsonData(JsonObject jsonObject, File jsonFile) {
        Gson gson = new Gson();
        try (Writer writer = new FileWriter(jsonFile)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveJsonArrayData(JsonArray jsonArray, File jsonFile) {
        Gson gson = new Gson();
        try (Writer writer = new FileWriter(jsonFile)) {
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void JSONArrayToArrayList(JsonArray jsonArray, ArrayList<UUID> arrayList) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<UUID>>(){}.getType();
        arrayList = gson.fromJson(jsonArray, type);
    }

    public static void blockJSONtoHashMap(File jsonFile, HashMap<UUID, Set<UUID>> hashMap) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<UUID, Set<UUID>>>(){}.getType();

        try (Reader reader = new FileReader(jsonFile)) {
            // Parse the JSON directly from the reader
            System.err.println(reader.toString());
            HashMap<UUID, Set<UUID>> tempMap = gson.fromJson(reader, type);
            System.err.println(tempMap);

            // If the JSON was not null, clear the passed hashMap and add all from the tempMap
            if (tempMap != null) {
                hashMap.clear();
                hashMap.putAll(tempMap);
            }
        }
        // IOException must be caught or declared to be thrown
    }
}
