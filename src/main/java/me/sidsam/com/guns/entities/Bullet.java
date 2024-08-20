package me.sidsam.com.guns.entities;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Bullet {

    private final Material material;
    private final int modelData;
    private final double damage;

    public Bullet(Material material, int modelData, double damage) {
        this.material = material;
        this.modelData = modelData;
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }

    public ItemStack getBulletItem() {
        ItemStack bulletItem = new ItemStack(material);
        ItemMeta bulletMeta = bulletItem.getItemMeta();
        if (bulletMeta != null) {
            bulletMeta.setCustomModelData(modelData);
            bulletItem.setItemMeta(bulletMeta);
        }
        return bulletItem;
    }

    public static class BulletRunnable extends BukkitRunnable {

        private final Item bullet;
        private final Player shooter;
        private final double damage;

        public BulletRunnable(Item bullet, Player shooter, double damage) {
            this.bullet = bullet;
            this.shooter = shooter;
            this.damage = damage;
        }

        @Override
        public void run() {
            // Check if the bullet is still valid
            if (bullet.isDead() || !bullet.isValid()) {
                this.cancel();
                return;
            }

            // Check for entity collisions
            List<Entity> nearbyEntities = bullet.getNearbyEntities(0.5, 0.5, 0.5);
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity mob) {
                    // Bullet hit an entity
                    mob.damage(damage, shooter);
                    bullet.remove();
                    return;
                }
            }

            // Check for block collisions
            if (bullet.isOnGround()) {
                // Bullet hit the ground or a block
                bullet.remove();
                this.cancel();
            }
        }
    }
}
