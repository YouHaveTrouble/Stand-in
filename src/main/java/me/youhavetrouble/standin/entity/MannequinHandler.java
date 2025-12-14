package me.youhavetrouble.standin.entity;

import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.youhavetrouble.standin.converter.EntityConverter;
import me.youhavetrouble.standin.converter.MannequinToArmorStandConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mannequin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class MannequinHandler extends EntityHandler<Mannequin> {

    public static final NamespacedKey DESCRIPTION_KEY = new NamespacedKey("standin", "mannequin_description");

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

        String name = EntityConverter.getRawEntityName(mannequin);
        if (name == null) {
            name = "";
        }
        inputs.add(
                DialogInput.text("name", Component.text("Name"))
                        .initial(name)
                        .maxLength(1024)
                        .build()
        );

        String description = getRawDescription(mannequin);
        if (description == null) {
            description = "";
        }
        inputs.add(
                DialogInput.text("description", Component.text("Description"))
                        .initial(description)
                        .maxLength(1024)
                        .build()
        );

        String profileName = "";
        if (mannequin.getProfile().name() != null) {
            profileName = mannequin.getProfile().name();
        }
        inputs.add(
                DialogInput.text("profile", Component.text("Skin Profile (Mojang Username)"))
                        .initial(profileName)
                        .build()
        );
        inputs.add(
                DialogInput.singleOption("pose", Component.text("Pose"),
                                List.of(
                                        SingleOptionDialogInput.OptionEntry.create("standing", Component.text("Standing"), mannequin.getPose() == Pose.STANDING),
                                        SingleOptionDialogInput.OptionEntry.create("fall_flying", Component.text("Fall flying"), mannequin.getPose() == Pose.FALL_FLYING),
                                        SingleOptionDialogInput.OptionEntry.create("sleeping", Component.text("Sleeping"), mannequin.getPose() == Pose.SLEEPING)
                                ))
                        .build()
        );
        inputs.add(
                DialogInput.bool("immovable", Component.text("Immovable"))
                        .initial(mannequin.isImmovable())
                        .build()
        );
        inputs.add(
                DialogInput.bool("invulnerable", Component.text("Invulnerable"))
                        .initial(mannequin.isInvulnerable())
                        .build()
        );
        inputs.add(
                DialogInput.bool("gravity", Component.text("Gravity"))
                        .initial(mannequin.hasGravity())
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
                    if (newName != null && !newName.isBlank()) {
                        Component displayName = MiniMessage.miniMessage().deserialize(newName);
                        mann.customName(displayName);
                    } else {
                        mann.customName(null);
                    }
                    EntityConverter.saveRawEntityName(mann, newName);
                    String newDescription = view.getText("description");
                    if (newDescription != null && !newDescription.isBlank()) {
                        mann.setDescription(MiniMessage.miniMessage().deserialize(newDescription));
                    } else {
                        mann.setDescription(null);
                    }
                    saveRawDescription(mann, newDescription);

                    mann.setImmovable(Boolean.TRUE.equals(view.getBoolean("immovable")));
                    mann.setVelocity(mann.getVelocity().zero());
                    mann.setGravity(Boolean.TRUE.equals(view.getBoolean("gravity")));
                    mann.setInvulnerable(Boolean.TRUE.equals(view.getBoolean("invulnerable")));
                    try {
                        String newProfileName = view.getText("profile");
                        if (newProfileName == null || newProfileName.isBlank()) {
                            newProfileName = null;
                        }
                        mann.setProfile(ResolvableProfile.resolvableProfile().name(newProfileName).build());
                    } catch (IllegalArgumentException e) {
                        callbackPlayer.sendRichMessage("<red>Profile name not updated: invalid username.");
                    }
                    String poseString = view.getText("pose");
                    if (poseString != null) {
                        try {
                            Pose newPose = Pose.valueOf(poseString.toUpperCase(Locale.ROOT));
                            mann.setPose(newPose);
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }, ClickCallback.Options.builder().lifetime(Duration.ofMinutes(5)).uses(1).build())
        ).build();
        actions.add(saveButton);

        if (player.hasPermission("standin.change_type.mannequin")) {
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

    public static String getRawDescription(@NotNull Entity entity) {
        return entity.getPersistentDataContainer().get(DESCRIPTION_KEY, PersistentDataType.STRING);
    }

    public static void saveRawDescription(@NotNull Mannequin mannequin, @Nullable String description) {
        if (description == null) {
            mannequin.getPersistentDataContainer().remove(DESCRIPTION_KEY);
            return;
        }
        mannequin.getPersistentDataContainer().set(DESCRIPTION_KEY, PersistentDataType.STRING, description);
    }

}
