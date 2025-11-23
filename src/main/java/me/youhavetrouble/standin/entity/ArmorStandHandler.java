package me.youhavetrouble.standin.entity;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.youhavetrouble.standin.converter.ArmorStandToMannequinConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class ArmorStandHandler extends EntityHandler<ArmorStand> {

    public ArmorStandHandler() {
        super(ArmorStand.class);
        addConverter(new ArmorStandToMannequinConverter());
    }

    public Dialog editDialog(@NotNull Player player, ArmorStand armorStand) {
        if (armorStand == null || armorStand.isDead()) return null;
        if (!canUseAction(player, armorStand, EntityAction.EDIT)) return null;

        UUID entityId = armorStand.getUniqueId();
        UUID playerId = player.getUniqueId();

        List<DialogInput> inputs = new ArrayList<>();

        inputs.add(
                DialogInput.bool("invisible", Component.text("Invisible"))
                        .initial(armorStand.isInvisible())
                        .build()
        );
        inputs.add(
                DialogInput.bool("canMove", Component.text("Can move"))
                        .initial(armorStand.canMove())
                        .build()
        );
        inputs.add(
                DialogInput.bool("gravity", Component.text("Gravity"))
                        .initial(armorStand.hasGravity())
                        .build()
        );
        inputs.add(
                DialogInput.bool("basePlate", Component.text("Base plate"))
                        .initial(armorStand.hasBasePlate())
                        .build()
        );
        inputs.add(
                DialogInput.bool("arms", Component.text("Arms"))
                        .initial(armorStand.hasArms())
                        .build()
        );
        inputs.add(
                DialogInput.bool("small", Component.text("Small"))
                        .initial(armorStand.isSmall())
                        .build()
        );

        ActionButton saveButton = ActionButton.builder(Component.text("Save")).action(
                DialogAction.customClick((view, audience) -> {
                    if (!(audience instanceof Player callbackPlayer)) return;
                    if (playerId != callbackPlayer.getUniqueId()) return;
                    Entity entity = callbackPlayer.getWorld().getEntity(entityId);
                    if (!(entity instanceof ArmorStand stand)) return;
                    if (!canUseAction(callbackPlayer, stand, EntityAction.EDIT)) return;
                    if (stand.isDead()) return;
                    stand.setInvisible(Boolean.TRUE.equals(view.getBoolean("invisible")));
                    stand.setBasePlate(Boolean.TRUE.equals(view.getBoolean("basePlate")));
                    stand.setArms(Boolean.TRUE.equals(view.getBoolean("arms")));
                    stand.setSmall(Boolean.TRUE.equals(view.getBoolean("small")));
                    stand.setCanMove(Boolean.TRUE.equals(view.getBoolean("canMove")));
                    stand.setGravity(Boolean.TRUE.equals(view.getBoolean("gravity")));
                }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).uses(1).build())
        ).build();

        List<ActionButton> actions = new ArrayList<>();
        if (player.hasPermission("standin.change_type.armor_stand")) {
            ActionButton changeTypeButton = ActionButton.builder(Component.text("Change type"))
                    .action(
                            DialogAction.customClick((view, audience) -> {
                                        if (!(audience instanceof Player callbackPlayer)) return;
                                        if (playerId != callbackPlayer.getUniqueId()) return;
                                        Entity entity = callbackPlayer.getWorld().getEntity(entityId);
                                        if (!(entity instanceof ArmorStand stand)) return;
                                        Dialog dialog = conversionDialog(callbackPlayer, stand);
                                        if (dialog == null) return;
                                        callbackPlayer.showDialog(dialog);
                                    }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).uses(1).build()
                            )
                    ).build();
            actions.add(changeTypeButton);
        }

        actions.add(saveButton);

        return Dialog.create(builder -> builder.empty()
                .base(
                        DialogBase.builder(Component.text("Armor Stand Editor"))
                                .inputs(inputs)
                                .build()
                ).type(
                        DialogType.multiAction(
                                actions,
                                ActionButton.builder(Component.text("Cancel")).build(),
                                1)
                )
        );
    }

}
