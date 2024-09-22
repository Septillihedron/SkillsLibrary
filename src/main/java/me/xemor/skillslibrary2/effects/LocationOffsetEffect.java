package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import me.xemor.configurationdata.VectorData;

public class LocationOffsetEffect extends WrapperEffect implements EntityEffect, ComplexLocationEffect, TargetEffect {

    private final Vector offset;

    public LocationOffsetEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        ConfigurationSection offsetSection = configurationSection.getConfigurationSection("offset");
        if (offsetSection == null) {
            offset = new Vector(0, 0, 0);
        } else {
            offset = new VectorData(offsetSection).getVector();
        }
    }

    @Override
    public void useEffect(Execution execution, Entity entity, Location location) {
        handleEffects(entity, location.add(offset));
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        SkillsLibrary.getFoliaHacks().runASAP(entity, () -> useEffect(execution, entity, entity.getLocation()));
    }

    @Override
    public void useEffect(Execution execution, Entity entity, Entity target) {
        SkillsLibrary.getFoliaHacks().runASAP(target, () -> useEffect(execution, entity, target.getLocation()));
    }

}
