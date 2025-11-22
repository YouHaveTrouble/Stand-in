package me.youhavetrouble.standin;

import me.youhavetrouble.standin.stand.StandinInteractionListener;
import org.bukkit.NamespacedKey;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public final class StandIn extends JavaPlugin {

    public static final NamespacedKey KEY = new NamespacedKey("stand-in", "stand-in");

    @Override
    public void onEnable() {

        getServer().getPluginManager().addPermissions(
                List.of(
                        new Permission("standin.edit.armor_stand", PermissionDefault.OP),
                        new Permission("standin.edit.mannequin", PermissionDefault.OP)
                )
        );


        getServer().getPluginManager().registerEvents(new StandinInteractionListener(), this);
    }

}
