package me.youhavetrouble.standin;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.youhavetrouble.standin.converter.ArmorStandToMannequinConverter;
import me.youhavetrouble.standin.converter.EntityConverter;
import me.youhavetrouble.standin.converter.MannequinToArmorStandConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// TODO generify this mess

@SuppressWarnings("UnstableApiUsage")
public class StandinDialog {

    public static void openConversionDialog(@NotNull Player player, @NotNull ArmorStand armorStand) {
        if (armorStand.isDead()) return;

        UUID armorStandId = armorStand.getUniqueId();
        UUID playerId = player.getUniqueId();

        List<DialogInput> inputs = List.of(
                DialogInput.singleOption("entity_type", Component.text("New entity type"), List.of(
                        SingleOptionDialogInput.OptionEntry.create("armor_stand", Component.text("Armor Stand"), true),
                        SingleOptionDialogInput.OptionEntry.create("mannequin", Component.text("Mannequin"), false)
                )).build()
        );

        ActionButton saveButton = ActionButton.builder(Component.text("Change type")).action(
                DialogAction.customClick((view, audience) -> {
                    if (!(audience instanceof Player callbackPlayer)) return;
                    Entity entity = callbackPlayer.getWorld().getEntity(armorStandId);
                    if (playerId != callbackPlayer.getUniqueId()) return;
                    if (!(entity instanceof ArmorStand stand)) return;
                    if (stand.isDead()) return;
                    String newEntityType = view.getText("entity_type");
                    switch (newEntityType) {
                        case "mannequin" -> {
                            EntityConverter<ArmorStand, Mannequin> mannequinConverter = new ArmorStandToMannequinConverter();
                            Mannequin mannequin = mannequinConverter.spawn(stand);
                            if (mannequin == null) {
                                audience.sendMessage(Component.text("Error spawning new entity. Ensure new entity can spawn in this spot.").color(NamedTextColor.RED));
                                return;
                            }
                            stand.remove();
                        }
                        case null, default -> {
                        }
                    }
                }, ClickCallback.Options.builder().lifetime(Duration.ofHours(1)).uses(1).build())
        ).build();

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(
                        DialogBase.builder(Component.text("Armor Stand Conversion"))
                                .body(List.of(
                                        DialogBody.plainMessage(
                                                Component.text("Some settings might not persist between changing entity type").color(NamedTextColor.RED)
                                        )
                                )).inputs(inputs)
                                .build())
                .type(DialogType.confirmation(saveButton, ActionButton.builder(Component.text("Cancel")).build()))
        );

        player.showDialog(dialog);
    }

    public static void openConversionDialog(@NotNull Player player, @NotNull Mannequin mannequin) {
        if (mannequin.isDead()) return;

        UUID MannequinId = mannequin.getUniqueId();
        UUID playerId = player.getUniqueId();

        List<DialogInput> inputs = List.of(
                DialogInput.singleOption("entity_type", Component.text("New entity type"), List.of(
                        SingleOptionDialogInput.OptionEntry.create("mannequin", Component.text("Mannequin"), true),
                        SingleOptionDialogInput.OptionEntry.create("armor_stand", Component.text("Armor Stand"), false)
                )).build()
        );

        ActionButton saveButton = ActionButton.builder(Component.text("Change type")).action(
                DialogAction.customClick((view, audience) -> {
                    if (!(audience instanceof Player callbackPlayer)) return;
                    if (playerId != callbackPlayer.getUniqueId()) return;
                    Entity entity = callbackPlayer.getWorld().getEntity(MannequinId);
                    if (!(entity instanceof Mannequin cMannequin)) return;
                    if (cMannequin.isDead()) return;
                    String newEntityType = view.getText("entity_type");
                    switch (newEntityType) {
                        case "armor_stand" -> {
                            EntityConverter<Mannequin, ArmorStand> armorStandConverter = new MannequinToArmorStandConverter();
                            ArmorStand armorStand = armorStandConverter.spawn(cMannequin);
                            if (armorStand == null) {
                                audience.sendMessage(Component.text("Error spawning new entity. Ensure new entity can spawn in this spot.").color(NamedTextColor.RED));
                                return;
                            }
                            cMannequin.remove();
                        }
                        case null, default -> {
                        }
                    }
                }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).uses(1).build())
        ).build();

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(
                        DialogBase.builder(Component.text("Mannequin Conversion"))
                                .body(List.of(
                                        DialogBody.plainMessage(
                                                Component.text("Some settings might not persist between changing entity type").color(NamedTextColor.RED)
                                        )
                                )).inputs(inputs)
                                .build())
                .type(DialogType.confirmation(saveButton, ActionButton.builder(Component.text("Cancel")).build()))
        );

        player.showDialog(dialog);
    }

    public static void openArmorStandDialog(@NotNull Player player, @NotNull ArmorStand armorStand) {
        if (armorStand.isDead()) return;

        UUID armorStandId = armorStand.getUniqueId();
        UUID playerId = player.getUniqueId();

        List<DialogInput> inputs = new ArrayList<>();

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
                    if (playerId != callbackPlayer.getUniqueId()) return;
                    Entity entity = callbackPlayer.getWorld().getEntity(armorStandId);
                    if (!(entity instanceof ArmorStand stand)) return;
                    if (stand.isDead()) return;
                    stand.setInvisible(Boolean.TRUE.equals(view.getBoolean("invisible")));
                    stand.setBasePlate(Boolean.TRUE.equals(view.getBoolean("basePlate")));
                    stand.setArms(Boolean.TRUE.equals(view.getBoolean("arms")));
                    stand.setSmall(Boolean.TRUE.equals(view.getBoolean("small")));
                }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).uses(1).build())
        ).build();

        ActionButton changeTypeButton = ActionButton.builder(Component.text("Change type"))
                .action(
                        DialogAction.customClick((view, audience) -> {
                            if (!(audience instanceof Player callbackPlayer)) return;
                            if (playerId != callbackPlayer.getUniqueId()) return;
                            Entity entity = callbackPlayer.getWorld().getEntity(armorStandId);
                            if (!(entity instanceof ArmorStand stand)) return;
                            if (stand.isDead()) return;
                            StandinDialog.openConversionDialog(callbackPlayer, armorStand);
                                }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).uses(1).build()
                        )
                ).build();

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(
                        DialogBase.builder(Component.text("Armor Stand Editor"))
                                .inputs(inputs)
                                .build()
                ).type(
                        DialogType.multiAction(
                                List.of(saveButton, changeTypeButton),
                                ActionButton.builder(Component.text("Cancel")).build(),
                                1)
                )
        );

        player.showDialog(dialog);
    }

    public static void openMannequinDialog(@NotNull Player player, @NotNull Mannequin mannequin) {
        if (mannequin.isDead()) return;

        UUID mannequinId = mannequin.getUniqueId();
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

        ActionButton saveButton = ActionButton.builder(Component.text("Save")).action(
                DialogAction.customClick((view, audience) -> {
                    if (!(audience instanceof Player callbackPlayer)) return;
                    if (playerId != callbackPlayer.getUniqueId()) return;
                    Entity entity = callbackPlayer.getWorld().getEntity(mannequinId);
                    if (!(entity instanceof Mannequin cMannequin)) return;
                    if (cMannequin.isDead()) return;
                    String newName = view.getText("name");
                    Component displayName = null;
                    if (newName != null) {
                        displayName = MiniMessage.miniMessage().deserialize(newName);
                    }
                    cMannequin.customName(displayName);

                }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).uses(1).build())
        ).build();

        ActionButton changeTypeButton = ActionButton.builder(Component.text("Change type"))
                .action(
                        DialogAction.customClick((view, audience) -> {
                                    if (!(audience instanceof Player callbackPlayer)) return;
                                    if (playerId != callbackPlayer.getUniqueId()) return;
                                    Entity entity = callbackPlayer.getWorld().getEntity(mannequinId);
                                    if (!(entity instanceof Mannequin cMannequin)) return;
                                    if (cMannequin.isDead()) return;
                                    StandinDialog.openConversionDialog(callbackPlayer, cMannequin);
                                }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).uses(1).build()
                        )
                ).build();

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(
                        DialogBase.builder(Component.text("Armor Stand Editor"))
                                .inputs(inputs)
                                .build()
                ).type(
                        DialogType.multiAction(
                                List.of(saveButton, changeTypeButton),
                                ActionButton.builder(Component.text("Cancel")).build(),
                                1)
                )
        );

        player.showDialog(dialog);
    }

}
