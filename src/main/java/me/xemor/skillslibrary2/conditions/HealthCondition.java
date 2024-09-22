package me.xemor.skillslibrary2.conditions;

import me.xemor.configurationdata.comparison.RangeData;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.concurrent.CompletableFuture;

public class HealthCondition extends Condition implements EntityCondition, TargetCondition {

    private final RangeData healthRange;

    public HealthCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        if (configurationSection.contains("healthPercentage")) {
            healthRange = new RangeData(configurationSection.getString("healthPercentage", "0 - 100"));
        }
        else {
            healthRange = new RangeData(configurationSection.getDouble("minimumHealthPercentage", 0) + " - " + configurationSection.getDouble("maximumHealthPercentage", 100));
        }
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            if (entity instanceof LivingEntity livingEntity) {
                double healthPercentage = (livingEntity.getHealth() / livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) * 100;
                return healthRange.isInRange(healthPercentage);
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity, Entity target) {
        return isTrue(target);
    }


}
