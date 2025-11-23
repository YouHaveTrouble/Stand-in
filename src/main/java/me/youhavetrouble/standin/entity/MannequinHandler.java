package me.youhavetrouble.standin.entity;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.youhavetrouble.standin.converter.MannequinToArmorStandConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class MannequinHandler extends EntityHandler<Mannequin> {

    public MannequinHandler() {
        super(Mannequin.class);
        addConverter(new MannequinToArmorStandConverter());
    }

    public Dialog editDialog(@NotNull Player player, Mannequin mannequin) {
        if (mannequin == null || mannequin.isDead()) return null;
        if (!canUseAction(player, mannequin, EntityAction.EDIT)) return null;

        UUID entityId = mannequin.getUniqueId();
        UUID playerId = player.getUniqueId();

        List<DialogInput> inputs = new ArrayList<>();

        String name = "";
        Component customName = mannequin.customName();
        if (customName != null) {
            name = PlainTextComponentSerializer.plainText().serialize(customName);
        }

        inputs.add(
                DialogInput.text("name", Component.text("Name"))
                        .initial(name)
                        .build()
        );

        List<ActionButton> actions = new ArrayList<>();

        ActionButton saveButton = ActionButton.builder(Component.text("Save")).action(
                DialogAction.customClick((view, audience) -> {
                    if (!(audience instanceof Player callbackPlayer)) return;
                    if (playerId != callbackPlayer.getUniqueId()) return;
                    Entity entity = callbackPlayer.getWorld().getEntity(entityId);
                    if (!(entity instanceof Mannequin mann)) return;
                    if (!canUseAction(callbackPlayer, mann, EntityAction.EDIT)) return;
                    if (mann.isDead()) return;
                    String newName = view.getText("name");
                    Component displayName = null;
                    if (newName != null) {
                        displayName = MiniMessage.miniMessage().deserialize(newName);
                    }
                    mann.customName(displayName);
                }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).uses(1).build())
        ).build();
        actions.add(saveButton);

        if (player.hasPermission("standin.change_type.armor_stand")) {
            ActionButton changeTypeButton = ActionButton.builder(Component.text("Change type"))
                    .action(
                            DialogAction.customClick((view, audience) -> {
                                        if (!(audience instanceof Player callbackPlayer)) return;
                                        if (playerId != callbackPlayer.getUniqueId()) return;
                                        Entity entity = callbackPlayer.getWorld().getEntity(entityId);
                                        if (!(entity instanceof Mannequin mann)) return;
                                        Dialog dialog = conversionDialog(callbackPlayer, mann);
                                        if (dialog == null) return;
                                        callbackPlayer.showDialog(dialog);
                                    }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).uses(1).build()
                            )
                    ).build();
            actions.add(changeTypeButton);
        }

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
