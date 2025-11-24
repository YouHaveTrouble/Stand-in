package me.youhavetrouble.standin.converter;

import me.youhavetrouble.standin.StandIn;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mannequin;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class MannequinToArmorStandConverter implements EntityConverter<Mannequin, ArmorStand> {

    @Override
    public @NotNull EntityType entityFrom() {
        return EntityType.MANNEQUIN;
    }

    @Override
    public @NotNull EntityType entityTo() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public ArmorStand spawn(@NotNull Mannequin from) {
        try {
            return from.getWorld().spawn(from.getLocation(), ArmorStand.class, (armorStand -> {
                armorStand.customName(from.customName());
                EntityConverter.saveRawEntityName(armorStand, EntityConverter.getRawEntityName(from));
                armorStand.setGravity(from.hasGravity());
                armorStand.setCanMove(!from.isImmovable());
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    try {
                        armorStand.setItem(slot, from.getEquipment().getItem(slot));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                PersistentDataContainer pdc = armorStand.getPersistentDataContainer();
                String profileName = from.getProfile().name();
                if (profileName != null && !profileName.isEmpty()) {
                    pdc.set(EntityConverter.PLAYER_PROFILE_KEY, PersistentDataType.STRING, profileName);
                }
            }));
        } catch (IllegalArgumentException e) {
            StandIn.getPlugin(StandIn.class).getSLF4JLogger().warn("Failed to spawn entity", e);
            return null;
        }
    }

}
