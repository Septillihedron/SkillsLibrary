package me.xemor.skillslibrary2.conditions;

import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EntityWhitelistCondition extends Condition implements EntityCondition, TargetCondition {

    private final boolean whitelist;
    private final Set<EntityType> entities;

    public EntityWhitelistCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        whitelist = configurationSection.getBoolean("whitelist", true);
        entities = configurationSection.getStringList("entities").stream().map(String::toUpperCase).map(EntityType::valueOf).collect(Collectors.toSet());
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            boolean contained = entities.contains(entity.getType());
            return whitelist == contained;
        });
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity, Entity target) {
        return isTrue(target);
    }
}
