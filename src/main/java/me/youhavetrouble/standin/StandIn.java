package me.youhavetrouble.standin;

import me.youhavetrouble.standin.stand.StandinInteractionListener;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class StandIn extends JavaPlugin {

    public static final NamespacedKey KEY = new NamespacedKey("stand-in", "stand-in");

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new StandinInteractionListener(), this);
    }

}
