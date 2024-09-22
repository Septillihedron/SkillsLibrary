package me.xemor.skillslibrary2.conditions;

import me.xemor.configurationdata.comparison.RangeData;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;

public class DistanceCondition extends Condition implements TargetCondition, LocationCondition {

    private final RangeData requiredDistance;

    public DistanceCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        this.requiredDistance = new RangeData(configurationSection.getString("distance"));
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity, Entity target) {
        return SkillsLibrary.getFoliaHacks().runASAP(target, () -> isTrue(entity, target.getLocation()));
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity, Location location) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            Location entityLocation = entity.getLocation();
            future.complete(requiredDistance.isInRange(entityLocation.distance(location)));
        });
        return future;
    }
}
