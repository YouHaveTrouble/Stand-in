package me.youhavetrouble.standin.entity;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.youhavetrouble.standin.converter.EntityConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public abstract class EntityHandler<E extends Entity> {

    public final Class<E> clazz;
    private final Set<EntityConverter<E, ?>> possibleConverters = new HashSet<>();

    public EntityHandler(Class<E> clazz) {
        this.clazz = clazz;
    }

    /**
     * Get the classes of entities this entity can convert into. Conversion can be lossy.
     *
     * @return Collection of classes of entities this entity can convert to
     */
    public final Set<Class<? extends Entity>> getPossibleConversions() {
        Set<Class<? extends Entity>> classes = new HashSet<>();
        for (EntityConverter<E, ?> converter : possibleConverters) {
            classes.add(converter.entityTo());
        }
        return Collections.unmodifiableSet(classes);
    }

    /**
     * Register new converter for entity type of E
     * @param converter converter to register
     */
    public final void addConverter(EntityConverter<E, ?> converter) {
        possibleConverters.add(converter);
    }

    /**
     * Gets the dialog with options for converting entity into another one.
     *
     * @param player player the dialog is meant for
     * @param entity entity that is supposed to be converted
     * @return dialog ready to display for the player or null if displaying it is impossible or no conversions are possible
     */
    public final @Nullable Dialog conversionDialog(@NotNull Player player, E entity) {
        if (entity.isDead()) return null;
        if (possibleConverters.isEmpty()) return null;
        if (!canUseAction(player, entity, EntityAction.CHANGE_TYPE)) return null;
        UUID entityId = entity.getUniqueId();
        UUID playerId = player.getUniqueId();
        Class<? extends Entity> entityClass = entity.getClass();

        List<SingleOptionDialogInput.OptionEntry> entityEntries = new ArrayList<>();

        entityEntries.add(
                SingleOptionDialogInput.OptionEntry.create(
                        entity.getType().toString(),
                        Component.translatable(entity.getType().translationKey(), entity.getType().toString()),
                        true
                )
        );
        for (EntityConverter<?, ?> converter : possibleConverters) {
            entityEntries.add(
                    SingleOptionDialogInput.OptionEntry.create(
                            converter.entityTo().getName(),
                            Component.translatable(converter.entityToType().translationKey()),
                            false
                    )
            );
        }

        List<DialogInput> inputs = List.of(
                DialogInput.singleOption(
                        "entity_type",
                        Component.text("New entity type"),
                        entityEntries
                ).build()
        );

        ActionButton saveButton = ActionButton.builder(Component.text("Change type")).action(
                DialogAction.customClick((view, audience) -> {
                    if (!(audience instanceof Player callbackPlayer)) return;
                    if (playerId != callbackPlayer.getUniqueId()) return;
                    Entity callbackEntity = callbackPlayer.getWorld().getEntity(entityId);
                    if (callbackEntity == null || callbackEntity.isDead()) return;
                    if (!callbackEntity.getClass().equals(entityClass)) return;
                    E existing = (E) callbackEntity;
                    if (!canUseAction(callbackPlayer, existing, EntityAction.CHANGE_TYPE)) return;
                    String newEntityType = view.getText("entity_type");
                    if (newEntityType == null) return;
                    if (newEntityType.equals(existing.getClass().getName())) return; // skip if the class is the same
                    EntityConverter<E, ?> foundConverter = null;
                    for (EntityConverter<E, ?> converter : possibleConverters) {
                        if (!newEntityType.equals(converter.entityTo().getName())) continue;
                        foundConverter = converter;
                        break;
                    }
                    if (foundConverter == null) return;
                    Entity converted = foundConverter.spawn(existing);
                    if (converted == null) return;
                    existing.remove();
                }, ClickCallback.Options.builder().lifetime(Duration.ofHours(1)).uses(1).build())
        ).build();

        return Dialog.create(builder -> builder.empty()
                .base(
                        DialogBase.builder(Component.text("Entity Conversion"))
                                .body(List.of(
                                        DialogBody.plainMessage(
                                                Component.text("Some settings might not persist between changing entity type").color(NamedTextColor.RED)
                                        )
                                )).inputs(inputs)
                                .build())
                .type(DialogType.confirmation(saveButton, ActionButton.builder(Component.text("Cancel")).build()))
        );
    }

    /**
     * Checks if player can execute given action for the entity. This is checked on opening dialogs and on executing
     * callbacks.
     * @param player player executing action
     * @param entity entity action is supposed to be used on
     * @param action action to b taken
     * @return Boolean representing the allowance state of the action
     */
    public boolean canUseAction(@NotNull Player player, E entity, EntityAction action) {
        String entityTypeName = entity.getType().toString().toLowerCase(Locale.ENGLISH);
        return switch (action) {
            case EDIT -> player.hasPermission("standin.edit." + entityTypeName);
            case CHANGE_TYPE -> player.hasPermission("standin.change_type." + entityTypeName);
            default -> false;
        };
    }

    /**
     * Open a dialog allowing to edit properties of the entity
     *
     * @param player player the dialog is meant for
     * @param entity entity that is supposed to be converted
     * @return dialog ready to display for the player or null if displaying it is impossible or no edits are possible
     */
    public Dialog editDialog(@NotNull Player player, E entity) {
        return null;
    }

    public enum EntityAction {
        EDIT,
        CHANGE_TYPE,
    }

}
