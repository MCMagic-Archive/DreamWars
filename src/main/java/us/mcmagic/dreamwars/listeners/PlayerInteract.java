package us.mcmagic.dreamwars.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import us.mcmagic.dreamwars.DreamWars;
import us.mcmagic.dreamwars.handlers.ChestCategory;
import us.mcmagic.dreamwars.handlers.ChestItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Marc on 7/27/16
 */
public class PlayerInteract implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!DreamWars.gameUtil.isAlive(player)) {
            event.setCancelled(true);
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Block b = event.getClickedBlock();
        if (!b.getType().equals(Material.CHEST)) {
            return;
        }
        Chest chest = (Chest) b.getState();
        if (chest.hasMetadata("opened")) {
            return;
        }
        chest.setMetadata("opened", new FixedMetadataValue(DreamWars.getInstance(), true));
        Inventory ci = chest.getInventory();
        randomizeChest(ci);
    }

    @SuppressWarnings("deprecation")
    private void randomizeChest(Inventory inv) {
        for (ChestCategory c : ChestCategory.values()) {
            int slot = randomSlot(inv);
            List<ChestItem> items = c.getItems();
            List<Integer> ids = new ArrayList<>();
            for (ChestItem it : items) {
                for (int i = 0; i < (it.getRarity() * 100); i++) {
                    ids.add(it.getId());
                }
            }
            int id = ids.get(new Random().nextInt(100));
            Material material = Material.getMaterial(id);
            ItemStack item;
            if (material.equals(Material.WHEAT)) {
                item = new ItemStack(material, 3);
            } else if (material.equals(Material.EXP_BOTTLE)) {
                item = new ItemStack(material, 8);
            } else {
                item = new ItemStack(material);
            }
            inv.setItem(slot, item);
            if (material.equals(Material.BOW)) {
                slot = randomSlot(inv);
                ItemStack arrows = new ItemStack(Material.ARROW, 20);
                inv.setItem(slot, arrows);
            }
        }
    }

    private int randomSlot(Inventory inv) {
        int slot = new Random().nextInt(27) + 1;
        int count = 1;
        while (inv.getItem(slot) != null) {
            slot = new Random().nextInt(27) + 1;
            if (count >= 30) {
                break;
            }
            count++;
        }
        return slot;
    }
}
