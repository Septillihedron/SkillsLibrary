package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class NearestEffect extends WrapperEffect implements EntityEffect, TargetEffect, ComplexLocationEffect {

    private final double radius;

    public NearestEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        radius = configurationSection.getDouble("radius", 5);
    }

    @Override
    public void useEffect(Execution execution, Entity entity, Location location) {
        getNearest(execution, entity, location).thenAccept((nearest) -> {
                    if (nearest == null) return;
                    handleEffects(entity, nearest);
        });
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        getNearest(execution, entity, entity.getLocation()).thenAccept((nearest) -> {
            if (nearest == null) return;
            handleEffects(entity, nearest);
        });
    }

    @Override
    public void useEffect(Execution execution, Entity livingEntity, Entity target) {
        getNearest(execution, livingEntity, target.getLocation()).thenAccept((nearest) -> {
            if (nearest == null) return;
            handleEffects(livingEntity, nearest);
        });
    }

    @NotNull
    public CompletableFuture<LivingEntity> getNearest(Execution execution, Entity livingEntity, Location location) {
        CompletableFuture<LivingEntity> completableFuture = new CompletableFuture<>();
        SkillsLibrary.getFoliaHacks().runASAP(location, () -> {
            World world = location.getWorld();
            Collection<Entity> entities = world.getNearbyEntities(location, radius, radius, radius);
            entities.removeIf((entity -> !(entity instanceof LivingEntity)));
            SkillsLibrary.getFoliaHacks().runASAP(livingEntity, () -> {
                entities.removeIf((entity -> !getConditions().ANDConditions(execution, livingEntity, false, entity)));
                if (entities.isEmpty()) {
                    return;
                }
                completableFuture.complete((LivingEntity) Collections.min(entities, Comparator.comparingDouble(entity -> entity.getLocation().distanceSquared(location))));
            });
        });
        return completableFuture;
    }

}
