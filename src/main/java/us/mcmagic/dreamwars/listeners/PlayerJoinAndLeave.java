package us.mcmagic.dreamwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.mcmagic.dreamwars.DreamWars;
import us.mcmagic.dreamwars.handlers.GameState;
import us.mcmagic.mcmagiccore.MCMagicCore;

import java.util.UUID;

public class PlayerJoinAndLeave implements Listener {

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (GameState.isState(GameState.SERVER_STARTING)) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "This server is still starting up!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (GameState.isState(GameState.PRE_GAME)) {
            if (Bukkit.getOnlinePlayers().size() >= DreamWars.configUtil.getSpawns().size()) {
                event.setKickMessage(ChatColor.RED + "This game is full!");
                event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            }
            return;
        }
        event.setKickMessage(ChatColor.RED + "There is currently a game in progress!");
        event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (GameState.isState(GameState.PRE_GAME)) {
            DreamWars.gameUtil.handleJoin(player);
            MCMagicCore.gameManager.setPlayerCount(MCMagicCore.getMCMagicConfig().serverName, Bukkit.getOnlinePlayers().size());
        } else {
            //TODO Join as Spectator
        }
        DreamWars.scoreboardUtil.join(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        MCMagicCore.gameManager.quit(player);
        if (GameState.isState(GameState.PRE_GAME)) {
            MCMagicCore.gameManager.setPlayerCount(MCMagicCore.getMCMagicConfig().serverName, Bukkit.getOnlinePlayers().size() - 1);
            return;
        }
        if (!DreamWars.gameUtil.isAlive(player)) {
            for (Player tp : Bukkit.getOnlinePlayers()) {
                tp.getScoreboard().getTeam("spec").removeEntry(player.getName());
            }
            return;
        }
        MCMagicCore.gameManager.setPlayerCount(MCMagicCore.getMCMagicConfig().serverName,
                Bukkit.getOnlinePlayers().size() - (DreamWars.gameUtil.getSpectators().size() + 1));
        for (Player tp : Bukkit.getOnlinePlayers()) {
            tp.getScoreboard().getTeam("players").removeEntry(player.getName());
        }
        DreamWars.getWorld().strikeLightning(player.getLocation());
        EntityDamageEvent cause = player.getLastDamageCause();
        if (cause != null && player.hasMetadata("lastAttacker")) {
            if (cause.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                UUID uuid = UUID.fromString(player.getMetadata("lastAttacker").get(0).asString());
                Player tp = Bukkit.getPlayer(uuid);
                if (tp != null) {
                    MCMagicCore.gameManager.broadcast(ChatColor.YELLOW + player.getName() +
                            ChatColor.GREEN + " was knocked off their cloud by " + ChatColor.YELLOW +
                            tp.getName() + "!");
                    DreamWars.getPlayerData(tp.getUniqueId()).addAttack();
                    MCMagicCore.economy.addBalance(tp.getUniqueId(), 1, "Attack in DreamWars");
                    return;
                }
            }
        }
        MCMagicCore.gameManager.broadcast(ChatColor.YELLOW + player.getName() + ChatColor.GREEN +
                " awoke from their dream!");
    }
}
