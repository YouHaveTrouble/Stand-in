package me.youhavetrouble.standin.converter;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public interface EntityConverter<F extends Entity, T extends Entity> {

    NamespacedKey PLAYER_PROFILE_KEY = new NamespacedKey("stand-in", "player-profile");

    @NotNull EntityType entityFrom();

    @NotNull EntityType entityTo();

    /**
     * Spawn the new entity in the old entity's spot.
     * @param from Entity to base the new one from
     * @return The spawned entity or null if entity cannot be spawned
     */
    T spawn(@NotNull F from);

}
