package me.youhavetrouble.standin.converter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EntityConverter<F extends Entity, T extends Entity> {

    NamespacedKey PLAYER_PROFILE_KEY = new NamespacedKey("stand-in", "player-profile");
    NamespacedKey CUSTOM_NAME_KEY = new NamespacedKey("stand-in", "raw-custom-name");

    @NotNull EntityType entityFrom();

    @NotNull EntityType entityTo();

    /**
     * Spawn the new entity in the old entity's spot.
     * @param from Entity to base the new one from
     * @return The spawned entity or null if entity cannot be spawned
     */
    T spawn(@NotNull F from);

    /**
     * MiniMessage serialized entity name
     * @param entity Entity to get name for
     * @return Raw entity name
     */
    static @Nullable String getRawEntityName(@NotNull Entity entity) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        String pdcCustomName = pdc.get(CUSTOM_NAME_KEY, PersistentDataType.STRING);
        if (pdcCustomName != null) {
            // Prioritize PDC stored name
            return pdcCustomName;
        }
        Component entityCustomName = entity.customName();

        // Fallback to custom name component
        // Cannot be serialized to minimessage because gradients would be extremely long and not fit dialog fields
        if (entityCustomName != null) {
            return PlainTextComponentSerializer.plainText().serialize(entityCustomName);
        }

        return null;
    }

    /**
     * Save raw minimessage string in entity's PDC
     * @param entity Entity to save name for
     * @param rawName minimessage string to save
     */
    static void saveRawEntityName(@NotNull Entity entity, @Nullable String rawName) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (rawName != null && !rawName.isEmpty()) {
            pdc.set(CUSTOM_NAME_KEY, PersistentDataType.STRING, rawName);
            return;
        }
        pdc.remove(CUSTOM_NAME_KEY);
    }

}
