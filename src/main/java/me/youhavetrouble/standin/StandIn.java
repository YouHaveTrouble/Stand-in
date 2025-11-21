package me.youhavetrouble.standin;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class StandIn extends JavaPlugin {

    public static final NamespacedKey KEY = new NamespacedKey("stand-in", "stand-in");

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
