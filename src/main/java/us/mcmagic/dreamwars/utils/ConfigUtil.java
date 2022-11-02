package us.mcmagic.dreamwars.utils;

import org.bukkit.Location;
import org.json.JSONArray;
import org.json.JSONObject;
import us.mcmagic.dreamwars.DreamWars;
import us.mcmagic.dreamwars.handlers.ChestCategory;
import us.mcmagic.dreamwars.handlers.ChestItem;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

public class ConfigUtil {
    private Location respawn;
    private String delay = "https://spreadsheets.google.com/feeds/cells/1f3fPQKZ_UyyqPhQTU72OlFeI5IpZiXFF68TUObbb1FM/2/public/values?alt=json";
    private HashMap<Integer, Location> spawns = new HashMap<>();

    public ConfigUtil() {
        loadLocations();
        loadChestData();
    }

    public Location getRespawn() {
        return respawn;
    }

    public HashMap<Integer, Location> getSpawns() {
        return spawns;
    }

    private void loadLocations() {
        String locations = "https://spreadsheets.google.com/feeds/cells/1f3fPQKZ_UyyqPhQTU72OlFeI5IpZiXFF68TUObbb1FM/1/public/values?alt=json";
        JSONObject obj = readJsonFromUrl(locations);
        if (obj == null) {
            return;
        }
        JSONArray array = obj.getJSONObject("feed").getJSONArray("entry");
        boolean isMap = false;
        for (int i = 0; i < array.length(); i++) {
            JSONObject ob = array.getJSONObject(i);
            JSONObject d = ob.getJSONObject("content");
            JSONObject id = ob.getJSONObject("title");
            String column = id.getString("$t");
            Integer row = Integer.parseInt(column.substring(1, 2));
            if (column.substring(0, 1).equalsIgnoreCase("a")) {
                if (d.getString("$t").equalsIgnoreCase(DreamWars.gameUtil.getMap())) {
                    isMap = true;
                } else {
                    isMap = false;
                    continue;
                }
            }
            if (!isMap) {
                continue;
            }
            String data = d.getString("$t");
            String[] split = data.split(":");
            String type = split[0].toLowerCase();
            String info = split[1];
            String[] coords = info.split(",");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            double z = Double.parseDouble(coords[2]);
            float yaw = Float.parseFloat(coords[3]);
            float pitch = Float.parseFloat(coords[4]);
            Location loc = getLocation(x, y, z, yaw, pitch);
            if (type.equals("respawn")) {
                respawn = loc;
                continue;
            }
            int spawn = Integer.parseInt(type.replace("spawn", ""));
            if (spawns.containsKey(spawn)) {
                spawns.remove(spawn);
            }
            spawns.put(spawn, loc);
        }
    }

    private void loadChestData() {
        for (ChestCategory c : ChestCategory.values()) {
            String url = "https://spreadsheets.google.com/feeds/cells/16XaKU4B9132hqUkg_HZchrap5R1fdSULtwdyQi8Bsjw/" +
                    c.getId() + "/public/values?alt=json";
            JSONObject obj = readJsonFromUrl(url);
            if (obj == null) {
                return;
            }
            JSONArray array = obj.getJSONObject("feed").getJSONArray("entry");
            int currentID = 0;
            for (int i = 0; i < array.length(); i++) {
                JSONObject ob = array.getJSONObject(i);
                JSONObject d = ob.getJSONObject("content");
                JSONObject id = ob.getJSONObject("title");
                String column = id.getString("$t");
                Integer row = Integer.parseInt(column.substring(1, 2));
                switch (column.substring(0, 1).toLowerCase()) {
                    case "a": {
                        currentID = d.getInt("$t");
                        break;
                    }
                    case "c": {
                        c.addItem(new ChestItem(currentID, d.getDouble("$t"), c));
                    }
                }
            }
        }
    }

    private Location getLocation(double x, double y, double z, float yaw, float pitch) {
        return new Location(DreamWars.getWorld(), x, y, z, yaw, pitch);
    }

    private static JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}