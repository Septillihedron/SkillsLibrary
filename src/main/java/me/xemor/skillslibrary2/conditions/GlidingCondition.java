package me.xemor.skillslibrary2.conditions;

import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.concurrent.CompletableFuture;

public class GlidingCondition extends Condition implements EntityCondition, TargetCondition {

    private final boolean shouldGlide;

    public GlidingCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        shouldGlide = configurationSection.getBoolean("shouldGlide", true) && configurationSection.getBoolean("glide", true);
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            if (entity instanceof LivingEntity livingEntity) {
                return livingEntity.isGliding() == shouldGlide;
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity, Entity target) {
        return isTrue(target);
    }
}
