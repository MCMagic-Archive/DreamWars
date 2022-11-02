package us.mcmagic.dreamwars.utils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import us.mcmagic.dreamwars.DreamWars;
import us.mcmagic.dreamwars.handlers.GameState;
import us.mcmagic.dreamwars.handlers.PlayerData;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.arcade.GameStartEvent;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GameUtil implements Listener {
    private final String map;
    private HashMap<UUID, Integer> clouds = new HashMap<>();
    private static List<UUID> spectators = new ArrayList<>();

    public GameUtil() {
        YamlConfiguration mapFile = YamlConfiguration.loadConfiguration(new File("gamemap.yml"));
        map = mapFile.getString("map");
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        GameState.setState(GameState.IN_GAME);
        int i = 1;
        HashMap<Integer, Location> spawns = DreamWars.configUtil.getSpawns();
        ItemStack sword = new ItemCreator(Material.WOOD_SWORD, "Dreamer's Sword");
        ItemStack helm = new ItemCreator(Material.LEATHER_HELMET);
        ItemStack chest = new ItemCreator(Material.LEATHER_CHESTPLATE);
        ItemStack leg = new ItemCreator(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemCreator(Material.LEATHER_BOOTS);
        for (Player tp : Bukkit.getOnlinePlayers()) {
            tp.teleport(spawns.get(i));
            PlayerInventory inv = tp.getInventory();
            inv.clear();
            inv.addItem(sword);
            inv.setHelmet(helm);
            inv.setChestplate(chest);
            inv.setLeggings(leg);
            inv.setBoots(boots);
        }
    }

    public String getMap() {
        return map;
    }

    public void handleJoin(Player player) {
        int cloud = 1;
        for (int i = 1; i <= DreamWars.configUtil.getSpawns().size(); i++) {
            if (!clouds.containsValue(i)) {
                cloud = i;
                break;
            }
        }
        PlayerData data = new PlayerData(player.getUniqueId(), cloud);
        clouds.put(player.getUniqueId(), cloud);
        player.teleport(DreamWars.configUtil.getSpawns().get(cloud));
        DreamWars.addPlayerData(data);
    }

    public void die(Player player) {
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0));
        DreamWars.getWorld().strikeLightning(player.getLocation());
        player.teleport(DreamWars.configUtil.getRespawn());
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (isAlive(tp)) {
                tp.hidePlayer(player);
            }
            if (!tp.getUniqueId().equals(player.getUniqueId()) && !isAlive(tp)) {
                player.showPlayer(tp);
            }
            Scoreboard sb = tp.getScoreboard();
            Team spec = sb.getEntryTeam("spec");
            if (spec == null) {
                continue;
            }
            spec.addEntry(player.getName());
        }
        spectators.add(player.getUniqueId());
        MCMagicCore.gameManager.setPlayerCount(MCMagicCore.getMCMagicConfig().serverName,
                Bukkit.getOnlinePlayers().size() - spectators.size());
    }

    public static boolean isAlive(Player player) {
        return !spectators.contains(player.getUniqueId());
    }

    public List<UUID> getSpectators() {
        return new ArrayList<>(spectators);
    }
}
