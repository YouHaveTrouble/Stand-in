package me.youhavetrouble.standin.converter;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface EntityConverter<F extends Entity, T extends Entity> {

    Class<F> entityFrom();

    Class<T> entityTo();

    /**
     * Spawn the new entity in the old entity's spot.
     * @param from Entity to base the new one from
     * @return The spawned entity or null if entity cannot be spawned
     */
    T spawn(@NotNull F from);

}
