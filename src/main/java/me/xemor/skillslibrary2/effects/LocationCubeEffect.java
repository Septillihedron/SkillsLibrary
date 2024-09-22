package me.xemor.skillslibrary2.effects;

import me.xemor.configurationdata.VectorData;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class LocationCubeEffect extends WrapperEffect implements EntityEffect, ComplexLocationEffect, TargetEffect {

    private final int verticalRadius;
    private final int horizontalRadius;
    private final Vector offset;

    public LocationCubeEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        verticalRadius = configurationSection.getInt("verticalRadius");
        horizontalRadius = configurationSection.getInt("horizontalRadius");
        ConfigurationSection offsetSection = configurationSection.getConfigurationSection("offset");
        if (offsetSection == null) {
            offset = new Vector(0, 0, 0);
        }
        else {
            offset = new VectorData(offsetSection).getVector();
        }
    }

    @Override
    public void useEffect(Execution execution, Entity entity, Location location) {
        Location cubeCentre = location.add(offset);
        for (int i = -horizontalRadius; i <= horizontalRadius; i++) {
            for (int j = -horizontalRadius; j <= horizontalRadius; j++) {
                for (int k = -verticalRadius; k <= verticalRadius; k++) {
                    handleEffects(execution, entity, cubeCentre.clone().add(i, k, j));
                }
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
