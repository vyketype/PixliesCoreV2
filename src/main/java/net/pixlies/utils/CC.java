package net.pixlies.utils;

import org.bukkit.ChatColor;

public final class CC {

    public static String format(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
