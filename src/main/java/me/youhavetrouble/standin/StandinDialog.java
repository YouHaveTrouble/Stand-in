package me.youhavetrouble.standin;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.util.TriState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class StandinDialog {

    public static void openArmorStandDialog(@NotNull Player player, @NotNull ArmorStand armorStand) {

        if (armorStand.isDead()) return;

        UUID armorStandId = armorStand.getUniqueId();

        List<DialogInput> inputs = new ArrayList<>();

        inputs.add(
                DialogInput.text("name", Component.text("Display name"))
                        .labelVisible(true)
                        .build()
        );
        inputs.add(
                DialogInput.bool("invisible", Component.text("Invisible"))
                        .initial(armorStand.isInvisible())
                        .build()
        );

        inputs.add(
                DialogInput.bool("onFire", Component.text("On fire"))
                        .initial(Boolean.TRUE.equals(armorStand.getVisualFire().toBoolean()))
                        .build()
        );

        ActionButton saveButton = ActionButton.builder(Component.text("Save")).action(
                DialogAction.customClick((view, audience) -> {
                    if (!(audience instanceof Player callbackPlayer)) return;
                    Entity entity = callbackPlayer.getWorld().getEntity(armorStandId);
                    if (!(entity instanceof ArmorStand stand)) return;
                    if (stand.isDead()) return;
                    stand.setInvisible(Boolean.TRUE.equals(view.getBoolean("invisible")));
                    stand.setVisualFire(TriState.byBoolean(Boolean.TRUE.equals(view.getBoolean("onFire"))));
                }, ClickCallback.Options.builder().lifetime(Duration.ofHours(1)).uses(1).build())
        ).build();

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(
                        DialogBase.builder(Component.text("Armor Stand Editor"))
                                .inputs(inputs)
                                .build())
                .type(DialogType.confirmation(saveButton, ActionButton.builder(Component.text("Cancel")).build()))
        );

        player.showDialog(dialog);
    }

}
