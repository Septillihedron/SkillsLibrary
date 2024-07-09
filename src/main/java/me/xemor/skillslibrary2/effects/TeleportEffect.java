package me.xemor.skillslibrary2.effects;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

public class TeleportEffect extends Effect implements ComplexTargetEffect, ComplexLocationEffect {

    public TeleportEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
    }

    @Override
    public boolean useEffect(Entity entity, Location location) {
        entity.teleport(location);
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        entity.teleport(target);
        return false;
    }
}
