package me.xemor.skillslibrary2.conditions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class ChanceCondition extends Condition implements EntityCondition {

    private final double chance;

    public ChanceCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        chance = configurationSection.getDouble("chance", 1.0);
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity boss) {
        return CompletableFuture.completedFuture(ThreadLocalRandom.current().nextFloat() <= chance);
    }
}
