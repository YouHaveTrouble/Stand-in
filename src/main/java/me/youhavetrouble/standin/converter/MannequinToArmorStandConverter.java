package me.youhavetrouble.standin.converter;

import me.youhavetrouble.standin.StandIn;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Mannequin;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class MannequinToArmorStandConverter implements EntityConverter<Mannequin, ArmorStand> {

    @Override
    public Class<Mannequin> entityFrom() {
        return Mannequin.class;
    }

    @Override
    public Class<ArmorStand> entityTo() {
        return ArmorStand.class;
    }

    @Override
    public ArmorStand spawn(@NotNull Mannequin from) {
        try {
            return from.getWorld().spawn(from.getLocation(), entityTo(), (armorStand -> {
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    try {
                        armorStand.getEquipment().setItem(slot, from.getEquipment().getItem(slot));
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
