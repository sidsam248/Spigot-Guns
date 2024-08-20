package me.sidsam.com.guns;

import me.sidsam.com.guns.events.GunCraftingListener;
import me.sidsam.com.guns.events.GunShootListener;
import me.sidsam.com.guns.events.GunSwitchListener;
import me.sidsam.com.guns.events.GunZoomListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new GunShootListener(this), this);
        getServer().getPluginManager().registerEvents(new GunZoomListener(), this);
        getServer().getPluginManager().registerEvents(new GunSwitchListener(), this);
        getServer().getPluginManager().registerEvents(new GunCraftingListener(this), this);

    }
}