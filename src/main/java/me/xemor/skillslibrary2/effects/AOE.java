package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.Collection;

public class AOE extends WrapperEffect implements EntityEffect, ComplexTargetEffect, ComplexLocationEffect {

    double radius;

    public AOE(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        radius = configurationSection.getDouble("radius", 5);
        if (!configurationSection.isString("mode")) {
            SkillsLibrary.getInstance().getLogger().warning("AoE skill does not have mode specified! Please specify a mode, otherwise this could have unintended behaviour at " + configurationSection.getCurrentPath() + ".mode");
        }
    }
    @Override
    public void useEffect(Execution execution, Entity skillEntity, Location location) {
        Collection<Entity> entities = location.getWorld().getNearbyEntities(location, radius, radius, radius);
        for (Entity entity : entities) {
            if (entity != skillEntity) {
                handleEffects(skillEntity);
                handleEffects(skillEntity, entity);
            }
        }
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        useEffect(execution, entity, entity.getLocation());
    }


    @Override
    public void useEffect(Execution execution, Entity entity, Entity target) {
        useEffect(execution, entity, target.getLocation());
    }
}
