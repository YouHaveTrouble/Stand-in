package me.youhavetrouble.standin.stand;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.event.player.PlayerPickEntityEvent;
import me.youhavetrouble.standin.StandIn;
import me.youhavetrouble.standin.entity.EntityHandler;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.Nullable;

public class StandinInteractionListener implements Listener {

    private final StandIn plugin;

    public StandinInteractionListener(StandIn plugin) {
        this.plugin = plugin;
    }

    private <E extends Entity> @Nullable Dialog invokeEditDialog(EntityHandler<E> handler, Player player, Entity clicked) {
        return handler.editDialog(player, handler.clazz.cast(clicked));
    }

    private <E extends Entity> @Nullable Dialog invokeConvertDialog(EntityHandler<E> handler, Player player, Entity clicked) {
        return handler.conversionDialog(player, handler.clazz.cast(clicked));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractWithStands(PlayerInteractAtEntityEvent event) {
        if (!event.getPlayer().isSneaking()) return;
        Entity entity = event.getRightClicked();
        if (!(entity instanceof ArmorStand) && !EntityHandler.isStandinEntity(entity) && !event.getPlayer().hasPermission(plugin.editAnyEntityPermission)) return;
        EntityHandler<?> handler = plugin.getEntityHandler(entity.getType());
        if (handler == null) return;
        Dialog dialog = invokeEditDialog(handler, event.getPlayer(), entity);
        if (dialog == null) return;
        event.getPlayer().showDialog(dialog);
        event.setCancelled(true);
    }

    /**
     * <a href="https://github.com/PaperMC/Paper/issues/13340">This currently does not work for mannequins since pick entity does not fire for them</a>
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteractWithStands(PlayerPickEntityEvent event) {
        if (!event.getPlayer().isSneaking()) return;
        Entity entity = event.getEntity();
        if (!(entity instanceof ArmorStand) && !EntityHandler.isStandinEntity(entity) && !event.getPlayer().hasPermission(plugin.convertAnyEntityPermission)) return;
        EntityHandler<?> handler = plugin.getEntityHandler(entity.getType());
        if (handler == null) return;
        Dialog dialog = invokeConvertDialog(handler, event.getPlayer(), entity);
        if (dialog == null) return;
        event.getPlayer().showDialog(dialog);
        event.setCancelled(true);
    }

}
