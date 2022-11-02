package us.mcmagic.dreamwars.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import us.mcmagic.dreamwars.DreamWars;
import us.mcmagic.dreamwars.handlers.GameState;
import us.mcmagic.mcmagiccore.MCMagicCore;

public class PlayerDamage implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING)) {
            event.setCancelled(true);
            return;
        }
        if (!GameState.isState(GameState.IN_GAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        if (!(damager instanceof Player)) {
            if (player.getHealth() - event.getFinalDamage() <= 0) {
                DreamWars.gameUtil.die(player);
                MCMagicCore.gameManager.broadcast(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " awoke from their dream!");
            }
            return;
        }
        Player attacker = (Player) damager;
        if (!(DreamWars.gameUtil.isAlive(player) || DreamWars.gameUtil.isAlive(attacker))) {
            event.setCancelled(true);
            return;
        }
        player.setMetadata("lastAttacker", new FixedMetadataValue(DreamWars.getInstance(), attacker.getUniqueId()));
        if (player.getHealth() - event.getFinalDamage() <= 0) {
            DreamWars.gameUtil.die(player);
            MCMagicCore.gameManager.broadcast(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " was awoken by "
                    + ChatColor.YELLOW + attacker.getName() + "!");
            DreamWars.getPlayerData(attacker.getUniqueId()).addAttack();
            MCMagicCore.economy.addBalance(attacker.getUniqueId(), 1, "Attack in DreamWars");
        }
    }
}
