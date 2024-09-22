package me.xemor.skillslibrary2.conditions;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CooldownCondition extends Condition implements EntityCondition {

    private final long cooldown;
    private final HashMap<UUID, Long> cooldownEndsMap = new HashMap<>();

    public CooldownCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        cooldown = Math.round(configurationSection.getDouble("cooldown", 10) * 1000);
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity) {
        double cooldownEnds = cooldownEndsMap.getOrDefault(entity.getUniqueId(), -1L);
        if (cooldownEnds < System.currentTimeMillis()) {
            cooldownEndsMap.put(entity.getUniqueId(), cooldown + System.currentTimeMillis());
            return CompletableFuture.completedFuture(true);
        }
        return CompletableFuture.completedFuture(false);
    }

}
