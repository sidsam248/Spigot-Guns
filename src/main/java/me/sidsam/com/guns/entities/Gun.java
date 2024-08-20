package me.sidsam.com.guns.entities;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.sidsam.com.guns.utils.ActionBarUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Gun {

    private final int metadata;
    private final JavaPlugin plugin;

    private static final Map<UUID, Map<Integer, Long>> lastShotTimes = new HashMap<>();
    public static final List<Integer> pistolList = new ArrayList<>(Arrays.asList(6, 11));
    public static final List<Integer> rifleList = new ArrayList<>(Arrays.asList(1, 3, 5));
    public static final List<Integer> sniperList = new ArrayList<>(Arrays.asList(4, 8));


    public Gun(int metadata, JavaPlugin plugin) {
        this.metadata = metadata;
        this.plugin = plugin;
    }

    public static List<Integer> getAllGuns() {
        return Stream.of(pistolList, rifleList, sniperList)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static boolean isPistol(int metadata) {
        return pistolList.contains(metadata);
    }

    public static boolean isRifle(int metadata) {
        return rifleList.contains(metadata);
    }

    public static boolean isSniper(int metadata) {
        return sniperList.contains(metadata);
    }

    public static ItemStack setCustomDurability(ItemStack item, int durability) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add("Durability: " + durability);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static int getCustomDurability(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore != null && !lore.isEmpty()) {
                String durabilityString = lore.getFirst().replace("Durability: ", "");
                return Integer.parseInt(durabilityString);
            }
        }
        return -1;
    }

    public static void reduceDurability(Player player, ItemStack item, int amount) {
        int currentDurability = getCustomDurability(item);
        currentDurability -= amount;

        // Ensure durability doesn't drop below 0
        if (currentDurability <= 0) {
            player.getInventory().remove(item); // Remove the item if durability is 0 or less
            ActionBarUtil.sendActionBar(player, "§e Your gun has broken");
        } else {
            ItemStack newItem = setCustomDurability(item, currentDurability);
            int slot = player.getInventory().first(item);
            if (slot != -1) {
                player.getInventory().setItem(slot, newItem);
            }
        }
    }

    public boolean isPistol() {
        return pistolList.contains(this.metadata);
    }

    public boolean isRifle() {
        return rifleList.contains(this.metadata);
    }

    public boolean isSniper() {
        return sniperList.contains(this.metadata);
    }

    public float getShotVolume() {
        if (isPistol())
            return 2.0F;
        else if (isRifle())
            return 4.0F;
        else if (isSniper())
            return 6.0F;
        else
            return 0F;
    }

    public double getBulletSpeed() {
        if (isPistol())
            return 1.5D;
        else if (isRifle())
            return 2.0D;
        else if (isSniper())
            return 4.0D;
        else
            return 0D;
    }

    public Bullet getBullet() {
        if (this.isPistol()) {
            return new Bullet(Material.IRON_NUGGET, 1, 6);
        } else if (this.isRifle()) {
            return new Bullet(Material.IRON_NUGGET, 2, 8);
        } else if (this.isSniper()) {
            return new Bullet(Material.GOLD_NUGGET, 3, 40);
        }
        return null;
    }

    public void shootBullet(Player player) {
        // Calculate the required delay based on the gun type in ms
        int delay = 0;
        Material requiredMaterial = Material.IRON_NUGGET;
        if (this.isPistol()) {
            delay = 500; // 500 milliseconds
        } else if (this.isRifle()) {
            delay = 100;
        } else if (this.isSniper()) {
            delay = 1000 * 2; // 2 seconds delay in milliseconds
            requiredMaterial = Material.GOLD_NUGGET;
        }

        // Get the current time
        long currentTime = System.currentTimeMillis();

        // Get the player's UUID and retrieve the player's last shot times
        UUID playerUUID = player.getUniqueId();
        Map<Integer, Long> playerLastShotTimes = lastShotTimes.getOrDefault(playerUUID, new HashMap<>());

        // Check if the player is allowed to shoot based on the delay
        if (playerLastShotTimes.containsKey(this.metadata)) {
            long lastShotTime = playerLastShotTimes.get(this.metadata);
            if (currentTime - lastShotTime < delay) {
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 1.0f);
                return;
            }
        }

        // Check if the player has the required material
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();

        int bulletCount = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == requiredMaterial) {
                bulletCount += item.getAmount();
            }
        }

        if (bulletCount < 1 && player.getGameMode() != GameMode.CREATIVE) {
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1.0f, 1.0f);
            return;
        }

        // Subtract one from the required material
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == requiredMaterial) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    inventory.remove(item);
                }
                break;
            }
        }

        ActionBarUtil.sendActionBar(player, "§eBullets left: " + Math.max(0, bulletCount - 1));

        // Update the last shot time for this gun
        playerLastShotTimes.put(this.metadata, currentTime);
        lastShotTimes.put(playerUUID, playerLastShotTimes);

        // Spawn the bullet item in front of the player
        Location spawnLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
        Bullet bullet = this.getBullet();
        if (bullet == null) return;
        Item bulletItem = player.getWorld().dropItem(spawnLocation, bullet.getBulletItem());
        bulletItem.setVelocity(player.getLocation().getDirection().multiply(getBulletSpeed()));

        // Play the sound of the bullet getting shot
        if (isPistol()) {
            playSoundToNearbyEntities(player,20, Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON);
        } else if (isRifle()) {
            playSoundToNearbyEntities(player, 40, Sound.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON);
        } else if (isSniper()) {
            playSoundToNearbyEntities(player, 80, Sound.ENTITY_GENERIC_EXPLODE);
        }

        reduceDurability(player, player.getItemInHand(), 1);

        // Track the bullet's movement to detect collisions
        new Bullet.BulletRunnable(bulletItem, player, bullet.getDamage()).runTaskTimer(plugin, 0L, 1L); // Run every tick (20 times per second
    }

    private void playSoundToNearbyEntities(Player shooter, double radius, Sound sound) {
        Location location = shooter.getLocation();
        if (location.getWorld() == null) return;

        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (entity instanceof Player nearbyPlayer) {
                double distance = location.distance(nearbyPlayer.getLocation());

                // Calculate volume based on distance
                double maxVolume = getShotVolume();
                double volume = Math.max(0.0, maxVolume * (1 - (distance / radius)));

                nearbyPlayer.playSound(location, sound, (float) volume, 1.0f);
            }
        }
    }
}


