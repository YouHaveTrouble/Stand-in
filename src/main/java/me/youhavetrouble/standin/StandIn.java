package me.youhavetrouble.standin;

import me.youhavetrouble.standin.entity.ArmorStandHandler;
import me.youhavetrouble.standin.entity.EntityHandler;
import me.youhavetrouble.standin.entity.MannequinHandler;
import me.youhavetrouble.standin.stand.MiscHandlerListener;
import me.youhavetrouble.standin.stand.StandinInteractionListener;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public final class StandIn extends JavaPlugin {

    public static final NamespacedKey KEY = new NamespacedKey("stand-in", "stand-in");

    public final Permission editAnyEntityPermission = new Permission("standin.edit-anything", PermissionDefault.FALSE);
    public final Permission convertAnyEntityPermission = new Permission("standin.convert-anything", PermissionDefault.FALSE);

    private final Map<EntityType, EntityHandler<? extends Entity>> entityHandlers = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().addPermissions(
                List.of(
                        editAnyEntityPermission,
                        convertAnyEntityPermission
                )
        );

        entityHandlers.put(EntityType.ARMOR_STAND, new ArmorStandHandler());
        entityHandlers.put(EntityType.MANNEQUIN, new MannequinHandler());

        entityHandlers.keySet().forEach(entityType -> {
            Permission editPermission = new Permission("standin.edit." + entityType.getKey().value(), PermissionDefault.OP);
            Permission convertPermission = new Permission("standin.change_type." + entityType.getKey().value(), PermissionDefault.OP);
            getServer().getPluginManager().addPermission(editPermission);
            getServer().getPluginManager().addPermission(convertPermission);
        });

        getServer().getPluginManager().registerEvents(new StandinInteractionListener(this), this);
        getServer().getPluginManager().registerEvents(new MiscHandlerListener(), this);
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
