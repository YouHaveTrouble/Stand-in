package me.youhavetrouble.standin.stand;

import io.papermc.paper.event.player.PlayerPickEntityEvent;
import me.youhavetrouble.standin.StandinDialog;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.RayTraceResult;
import org.jetbrains.annotations.NotNull;

public class StandinInteractionListener implements Listener {

    private boolean handleInteraction(@NotNull Player player, Entity entity) {
        if (entity == null) return false;
        if (!player.isOnline()) return false;

        if (entity instanceof ArmorStand armorStand && player.hasPermission("standin.edit.armor_stand")) {
            StandinDialog.openArmorStandDialog(player, armorStand);
            return true;
        }

        if (entity instanceof Mannequin mannequin && player.hasPermission("standin.edit.mannequin")) {
            StandinDialog.openMannequinDialog(player, mannequin);
            return true;
        }

        return false;
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInteractWithDisplays(PlayerInteractEvent event) {
        if (!event.getPlayer().isSneaking()) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        Player player = event.getPlayer();

        AttributeInstance instance = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE);
        if (instance == null) return;

        double interactionRange = instance.getValue();

        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                interactionRange,
                0.5,
                (entity -> {
                    switch (entity.getType()) {
                        case TEXT_DISPLAY,
                             BLOCK_DISPLAY,
                             ITEM_DISPLAY -> {
                            if (entity.getVehicle() == null) return true;
                            return !player.getUniqueId().equals(entity.getVehicle().getUniqueId());
                        }
                        default -> {
                            return false;
                        }
                    }
                })
        );
        if (result == null) return;

        Entity entity = result.getHitEntity();
        if (!handleInteraction(player, entity)) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractWithStands(PlayerInteractAtEntityEvent event) {
        if (!event.getPlayer().isSneaking()) return;
        if (!handleInteraction(event.getPlayer(), event.getRightClicked())) return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractWithStands(PlayerPickEntityEvent event) {
        if (!event.getPlayer().isSneaking()) return;
        if (event.getEntity() instanceof ArmorStand armorStand) {
            StandinDialog.openConversionDialog(event.getPlayer(), armorStand);
            event.setCancelled(true);
            return;
        }

        // This currently does not work since pick entity does not fire for mannequins
        // https://github.com/PaperMC/Paper/issues/13340
        if (event.getEntity() instanceof Mannequin mannequin) {
            StandinDialog.openConversionDialog(event.getPlayer(), mannequin);
            event.setCancelled(true);
            return;
        }
    }

}
