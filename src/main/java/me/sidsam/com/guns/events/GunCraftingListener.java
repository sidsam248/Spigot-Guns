package me.sidsam.com.guns.events;

import me.sidsam.com.guns.entities.Gun;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GunCraftingListener implements Listener {

    private final JavaPlugin plugin;

    private static final List<Integer> pistolList = Gun.pistolList;
    private static final List<Integer> rifleList = Gun.rifleList;
    private static final List<Integer> sniperList = Gun.sniperList;

    private final Random random = new Random();

    public GunCraftingListener(JavaPlugin plugin) {
        this.plugin = plugin;
        addCraftingRecipes();
    }

    private void addCraftingRecipes() {
        ItemStack item = new ItemStack(Material.IRON_HORSE_ARMOR);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;

        // Pistol recipe
        NamespacedKey pistolKey = new NamespacedKey(plugin, "random_pistol");
        meta.setItemName("Pistol");
        meta.setCustomModelData(pistolList.getFirst());
        item.setItemMeta(meta);
        ShapedRecipe pistolRecipe = new ShapedRecipe(pistolKey, setCustomDurability(item, 250));
        pistolRecipe.shape("AAA", "AB ", "A  ");
        pistolRecipe.setIngredient('A', Material.IRON_NUGGET);
        pistolRecipe.setIngredient('B', Material.STICK);
        plugin.getServer().addRecipe(pistolRecipe);

        // Rifle recipe
        NamespacedKey rifleKey = new NamespacedKey(plugin, "random_rifle");
        meta.setItemName("Rifle");
        meta.setCustomModelData(rifleList.getFirst());
        item.setItemMeta(meta);
        ShapedRecipe rifleRecipe = new ShapedRecipe(rifleKey, setCustomDurability(item, 750));
        rifleRecipe.shape("DDD", "DB ", "D  ");
        rifleRecipe.setIngredient('D', Material.DIAMOND);
        rifleRecipe.setIngredient('B', Material.STICK);
        plugin.getServer().addRecipe(rifleRecipe);

        // Sniper recipe
        NamespacedKey sniperKey = new NamespacedKey(plugin, "random_sniper");
        meta.setItemName("Sniper");
        meta.setCustomModelData(sniperList.getFirst());
        item.setItemMeta(meta);
        ShapedRecipe sniperRecipe = new ShapedRecipe(sniperKey, item);
        sniperRecipe.shape("EEE", "EB ", "E  ");
        sniperRecipe.setIngredient('E', Material.EMERALD);
        sniperRecipe.setIngredient('B', Material.STICK);
        plugin.getServer().addRecipe(sniperRecipe);
    }


    private ItemStack createRandomPistol() {
        return createRandomGun(pistolList, "Pistol", 250);
    }

    private ItemStack createRandomRifle() {
        return createRandomGun(rifleList, "Rifle", 750);
    }

    private ItemStack createRandomSniper() {
        return createRandomGun(sniperList, "Sniper", 150);
    }

    private ItemStack createRandomGun(List<Integer> gunList, String gunName, int durability) {
        // Create an item stack for the gun
        ItemStack gun = new ItemStack(Material.IRON_HORSE_ARMOR);

        // Set the metadata to a random value
        ItemMeta meta = gun.getItemMeta();
        assert meta != null;
        meta.setDisplayName(gunName);
        int randomMeta = gunList.get(random.nextInt(gunList.size()));
        meta.setCustomModelData(randomMeta);
        gun.setItemMeta(meta);

        Gun.setCustomDurability(gun, durability);

        return gun;
    }

    private boolean isPistolRecipe(ItemStack[] matrix) {
        return checkRecipe(matrix, Material.IRON_BLOCK, Material.STICK);
    }

    private boolean isRifleRecipe(ItemStack[] matrix) {
        return checkRecipe(matrix, Material.DIAMOND_BLOCK, Material.STICK);
    }

    private boolean isSniperRecipe(ItemStack[] matrix) {
        return checkRecipe(matrix, Material.EMERALD_BLOCK, Material.STICK);
    }

    private boolean checkRecipe(ItemStack[] matrix, Material mainMaterial, Material secondaryMaterial) {
        // Simplified check for the pattern in the matrix
        // You may need to adapt this to match your exact recipe shapes
        int mainCount = 0;
        int secondaryCount = 0;

        for (ItemStack item : matrix) {
            if (item != null && item.getType() == mainMaterial) {
                mainCount++;
            } else if (item != null && item.getType() == Material.STICK) {
                secondaryCount++;
            }
        }

        return mainCount >= 4 && secondaryCount >= 1; // Adjust based on your recipe requirements
    }

    private ItemStack setCustomDurability(ItemStack item, int durability) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add("Durability: " + durability);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        // Get the result item of the recipe
        ItemStack result = event.getRecipe().getResult();

        // Check if the result is iron horse armor, which is the base for guns
        if (result.getType() == Material.IRON_HORSE_ARMOR) {
            ItemStack randomGun = null;

            // Get the crafting inventory
            CraftingInventory inventory = event.getInventory();
            ItemStack[] matrix = inventory.getMatrix();

            // Check the ingredients to determine which gun to create
            if (isPistolRecipe(matrix)) {
                randomGun = createRandomPistol();
            } else if (isRifleRecipe(matrix)) {
                randomGun = createRandomRifle();
            } else if (isSniperRecipe(matrix)) {
                randomGun = createRandomSniper();
            }

            // If a random gun was determined, set it as the crafting result
            if (randomGun != null) {
                inventory.setResult(randomGun);
            }
        }
    }


}

