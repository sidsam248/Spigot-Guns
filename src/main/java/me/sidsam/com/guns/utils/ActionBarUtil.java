package me.sidsam.com.guns.utils;

import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarUtil {

    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) return;

        TextComponent textComponent = new TextComponent(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent);
    }
}

