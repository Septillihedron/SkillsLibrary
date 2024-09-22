package me.xemor.skillslibrary2.conditions;

import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class FlyingCondition extends Condition implements EntityCondition, TargetCondition {
    public FlyingCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            if (entity instanceof Player player) {
                return player.isFlying();
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity, Entity target) {
        return isTrue(target);
    }
}
