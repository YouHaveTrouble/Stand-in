package me.youhavetrouble.standin.converter;

import me.youhavetrouble.standin.StandIn;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mannequin;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class ArmorStandToMannequinConverter implements EntityConverter<ArmorStand, Mannequin> {

    @Override
    public Class<ArmorStand> entityFrom() {
        return ArmorStand.class;
    }

    @Override
    public Class<Mannequin> entityTo() {
        return Mannequin.class;
    }

    @Override
    public @NotNull EntityType entityFromType() {
        return EntityType.ARMOR_STAND;
    }

    @Override
    public @NotNull EntityType entityToType() {
        return EntityType.MANNEQUIN;
    }

    @Override
    public Mannequin spawn(@NotNull ArmorStand from) {
        try {
            return from.getWorld().spawn(from.getLocation(), entityTo(), (mannequin -> {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    try {
                        mannequin.getEquipment().setItem(slot, from.getItem(slot));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }));
        } catch (IllegalArgumentException e) {
            StandIn.getPlugin(StandIn.class).getSLF4JLogger().warn("Failed to spawn entity", e);
            return null;
        }
    }
}
