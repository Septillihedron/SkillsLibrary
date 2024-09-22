package me.xemor.skillslibrary2.effects;

import me.xemor.configurationdata.entity.EntityData;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class SpawnEffect extends Effect implements EntityEffect, TargetEffect, ComplexLocationEffect {

    private final EntityData entityData;

    public SpawnEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        this.entityData = EntityData.create(configurationSection, "entity", EntityType.ZOMBIE);
    }

    @Override
    public boolean useEffect(Execution execution, Entity entity) {
        useEffect(entity, entity.getLocation());
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Location location) {
        entityData.spawnEntity(location);
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        useEffect(entity, target.getLocation());
        return false;
    }
}
