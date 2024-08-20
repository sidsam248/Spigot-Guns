package me.sidsam.com.guns.events;

import me.sidsam.com.guns.entities.Gun;
import me.sidsam.com.guns.utils.ActionBarUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;

public class GunZoomListener implements Listener {

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Only proceed if the player is left-clicking in the air
        if (event.getAction() != Action.LEFT_CLICK_AIR) return;

        // Check if the player is holding the custom gun item
        if (item.getType() != Material.IRON_HORSE_ARMOR) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) return;
        if (!Gun.isSniper(meta.getCustomModelData())) return; // Only apply zoom for sniper guns

        PotionEffect slowness = player.getPotionEffect(PotionEffectType.SLOWNESS);

        int zoomLevel = slowness == null ? -1 : slowness.getAmplifier() % 4;

        // Apply the corresponding slowness effect
        switch (zoomLevel) {
            case -1:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 4, false, false, false));
                ActionBarUtil.sendActionBar(player,"§eScoped in once");
                break;
            case 0:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 9, false, false, false));
                ActionBarUtil.sendActionBar(player,"§eScoped in twice");
                break;
            default:
                player.removePotionEffect(PotionEffectType.SLOWNESS);
                ActionBarUtil.sendActionBar(player,"§eScoped out");
                break;
        }
    }
}

