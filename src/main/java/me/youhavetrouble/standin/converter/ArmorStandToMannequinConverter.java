package me.youhavetrouble.standin.converter;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import me.youhavetrouble.standin.StandIn;
import me.youhavetrouble.standin.entity.MannequinHandler;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mannequin;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class ArmorStandToMannequinConverter implements EntityConverter<ArmorStand, Mannequin> {

    @Override
    public @NotNull EntityType entityFrom() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public @NotNull EntityType entityTo() {
        return EntityType.MANNEQUIN;
    }

    @Override
    public Mannequin spawn(@NotNull ArmorStand from) {
        try {
            return from.getWorld().spawn(from.getLocation(), Mannequin.class, (mannequin -> {
                markAsTransformed(mannequin);
                EntityConverter.saveRawEntityName(mannequin, EntityConverter.getRawEntityName(from));
                MannequinHandler.saveRawDescription(mannequin, MannequinHandler.getRawDescription(from));
                mannequin.customName(from.customName());
                mannequin.setImmovable(!from.canMove());
                mannequin.setGravity(from.hasGravity());
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    try {
                        mannequin.getEquipment().setItem(slot, from.getItem(slot));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                PersistentDataContainer pdc = from.getPersistentDataContainer();
                String profileName = pdc.get(EntityConverter.PLAYER_PROFILE_KEY, PersistentDataType.STRING);
                if (profileName != null) {
                    try {
                        mannequin.setProfile(ResolvableProfile.resolvableProfile().name(profileName).build());
                    } catch (IllegalArgumentException e) {
                        StandIn.getPlugin(StandIn.class).getSLF4JLogger().warn("Failed to set profile for mannequin", e);
                    }
                }
            }));
        } catch (IllegalArgumentException e) {
            StandIn.getPlugin(StandIn.class).getSLF4JLogger().warn("Failed to spawn entity", e);
            return null;
        }
    }

}
