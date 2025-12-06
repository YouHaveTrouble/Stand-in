package me.youhavetrouble.standin.stand;

import me.youhavetrouble.standin.entity.EntityHandler;
import org.bukkit.Material;
import org.bukkit.entity.Mannequin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MiscHandlerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMannequinDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Mannequin mannequin)) return;
        if (!EntityHandler.isStandinEntity(mannequin)) return;
        // Clear any default drops and drop all items equipped on the mannequin
        event.getDrops().clear();
        event.getDrops().addAll(List.of(mannequin.getEquipment().getArmorContents()));
        event.getDrops().add(mannequin.getEquipment().getItemInMainHand());
        event.getDrops().add(mannequin.getEquipment().getItemInOffHand());
        event.getDrops().add(new ItemStack(Material.ARMOR_STAND));
    }

}
