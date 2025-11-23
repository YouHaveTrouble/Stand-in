package me.youhavetrouble.standin;

import me.youhavetrouble.standin.entity.ArmorStandHandler;
import me.youhavetrouble.standin.entity.EntityHandler;
import me.youhavetrouble.standin.entity.MannequinHandler;
import me.youhavetrouble.standin.stand.StandinInteractionListener;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class StandIn extends JavaPlugin {

    public static final NamespacedKey KEY = new NamespacedKey("stand-in", "stand-in");

    private final Map<EntityType, EntityHandler<? extends Entity>> entityHandlers = new HashMap<>();

    @Override
    public void onEnable() {
        entityHandlers.put(EntityType.ARMOR_STAND, new ArmorStandHandler());
        entityHandlers.put(EntityType.MANNEQUIN, new MannequinHandler());

        getServer().getPluginManager().registerEvents(new StandinInteractionListener(), this);
    }

    /**
     * Gets entity handler for given entity class
     * @param entityType entity type to get handler for
     * @return Entity handler or null
     */
    public @Nullable EntityHandler<? extends Entity> getEntityHandler(EntityType entityType) {
        return entityHandlers.get(entityType);
    }

}
