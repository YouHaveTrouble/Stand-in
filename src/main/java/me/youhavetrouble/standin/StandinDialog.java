package me.youhavetrouble.standin;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

        String currentName = "";
        if (armorStand.customName() != null) {
            currentName = PlainTextComponentSerializer.plainText().serialize(armorStand.name());
        }

        inputs.add(
                DialogInput.text("name", Component.text("Display name"))
                        .initial(currentName)
                        .build()
        );
        inputs.add(
                DialogInput.bool("invisible", Component.text("Invisible"))
                        .initial(armorStand.isInvisible())
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
                    Entity entity = callbackPlayer.getWorld().getEntity(armorStandId);
                    if (!(entity instanceof ArmorStand stand)) return;
                    if (stand.isDead()) return;
                    String customName = view.getText("name");
                    if (customName == null || customName.isEmpty()) {
                        stand.customName(null);
                    } else {
                        stand.customName(Component.text(customName));
                    }
                    stand.setInvisible(Boolean.TRUE.equals(view.getBoolean("invisible")));
                    stand.setBasePlate(Boolean.TRUE.equals(view.getBoolean("basePlate")));
                    stand.setArms(Boolean.TRUE.equals(view.getBoolean("arms")));
                    stand.setSmall(Boolean.TRUE.equals(view.getBoolean("small")));
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
