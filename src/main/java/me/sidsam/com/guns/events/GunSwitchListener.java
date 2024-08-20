package me.sidsam.com.guns.events;

import me.sidsam.com.guns.utils.ActionBarUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class GunSwitchListener implements Listener {

    @EventHandler
    public void onPlayerSwitchItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();

        // Check if the player has a slowness effect
        if (player.hasPotionEffect(PotionEffectType.SLOWNESS)) {
            // Get the item the player is switching to
            ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
            // If the new item is not a sniper gun, remove the slowness effect
            if (newItem != null && newItem.getType() != Material.IRON_HORSE_ARMOR) {
                player.removePotionEffect(PotionEffectType.SLOWNESS);
                ActionBarUtil.sendActionBar(player,"§eScoped out");
            }
        }
    }
}
