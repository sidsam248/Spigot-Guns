package me.sidsam.com.guns.events;

import me.sidsam.com.guns.entities.Gun;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

public class GunShootListener implements Listener {

    private final JavaPlugin plugin;

    public GunShootListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
        // Check if the player is holding the custom gun item
        if (item.getType() != Material.IRON_HORSE_ARMOR) return;
        ItemMeta meta = item.getItemMeta();
        // Check if item has custom model metadata
        if (meta == null || !meta.hasCustomModelData()) return;
        Gun gun = new Gun(meta.getCustomModelData(), plugin);
        gun.shootBullet(player);
        if (gun.isSniper()) {
            player.removePotionEffect(PotionEffectType.SLOWNESS);
        }
    }
}
