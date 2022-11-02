package us.mcmagic.dreamwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import us.mcmagic.dreamwars.DreamWars;
import us.mcmagic.dreamwars.handlers.GameState;
import us.mcmagic.mcmagiccore.MCMagicCore;

import java.util.UUID;

public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        switch (GameState.getState()) {
            case PRE_GAME:
                break;
            case IN_GAME: {
                if (event.getTo().getBlockY() <= 0) {
                    if (DreamWars.gameUtil.isAlive(player)) {
                        DreamWars.gameUtil.die(player);
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
                                " fell off their cloud!");
                    } else {
                        player.teleport(DreamWars.configUtil.getRespawn());
                    }
                }
                break;
            }
            case POSTGAME:
                if (event.getTo().getBlockY() <= 0) {
                    player.teleport(DreamWars.configUtil.getSpawns()
                            .get(DreamWars.getPlayerData(player.getUniqueId()).getCloud()));
                }
                break;
        }
    }
}
