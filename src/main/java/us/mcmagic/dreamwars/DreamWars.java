package us.mcmagic.dreamwars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mcmagic.dreamwars.handlers.GameState;
import us.mcmagic.dreamwars.handlers.PlayerData;
import us.mcmagic.dreamwars.listeners.*;
import us.mcmagic.dreamwars.utils.ConfigUtil;
import us.mcmagic.dreamwars.utils.GameUtil;
import us.mcmagic.dreamwars.utils.ScoreboardUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.arcade.ServerState;

import java.util.HashMap;
import java.util.UUID;

public class DreamWars extends JavaPlugin {
    private static DreamWars instance;
    private static HashMap<UUID, PlayerData> playerData = new HashMap<>();
    private static World world;
    public static GameUtil gameUtil;
    public static ConfigUtil configUtil;
    public static ScoreboardUtil scoreboardUtil;

    @Override
    public void onEnable() {
        GameState.setState(GameState.SERVER_STARTING);
        instance = this;
        gameUtil = new GameUtil();
        configUtil = new ConfigUtil();
        scoreboardUtil = new ScoreboardUtil();
        world = Bukkit.getWorld("world");

        registerListeners();

        /*
          Game Data
         */
        int start = (int) (DreamWars.configUtil.getSpawns().size() * .75);
        String c = ChatColor.GOLD.toString();
        String s = "          ";
        MCMagicCore.gameManager.setGameData("DreamWars", "        DreamWars", new String[]{c + s +
                        "           Game Description", c + s + " ", c + s + " "}, start,
                DreamWars.configUtil.getSpawns().size(), 15);
        Bukkit.getScheduler().runTaskLater(this, () -> {
            GameState.setState(GameState.PRE_GAME);
            MCMagicCore.gameManager.setState(MCMagicCore.getMCMagicConfig().serverName, ServerState.ONLINE);
        }, 100L);
    }

    @Override
    public void onDisable() {
        MCMagicCore.gameManager.setState(MCMagicCore.getMCMagicConfig().serverName, ServerState.RESTARTING);
        MCMagicCore.gameManager.setPlayerCount(MCMagicCore.getMCMagicConfig().serverName, 0);
    }

    public static DreamWars getInstance() {
        return instance;
    }

    public static World getWorld() {
        return world;
    }

    public static void addPlayerData(PlayerData data) {
        playerData.put(data.getUniqueId(), data);
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockListener(), this);
        pm.registerEvents(new PlayerDamage(), this);
        pm.registerEvents(new PlayerInteract(), this);
        pm.registerEvents(new PlayerJoinAndLeave(), this);
        pm.registerEvents(new PlayerMove(), this);
        pm.registerEvents(new WeatherListener(), this);
    }
}
