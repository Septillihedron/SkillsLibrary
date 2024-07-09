package me.xemor.skillslibrary2.effects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class VelocityEffect extends ModifyEffect implements EntityEffect, ComplexTargetEffect {
    private String component;

    public VelocityEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        if (!configurationSection.contains("value")) super.valueExpr = configurationSection.getDouble("velocity", 1.0);
        // default is Y to maintain backwards compatibility unfortunately
        // would be better for it to be all
        component = configurationSection.getString("component", "Y");
        if ("ALL".equalsIgnoreCase(component)) {
            component = "XYZ";
        }
    }

    @Override
    public boolean useEffect(Entity livingEntity) {
        Vector velocity = livingEntity.getVelocity();
        if (component.contains("X") || component.contains("x")) {
            velocity = velocity.setX(changeValue(velocity.getX()));
        }
        if (component.contains("Y") || component.contains("y")) {
            velocity = velocity.setY(changeValue(velocity.getY()));
        }
        if (component.contains("Z") || component.contains("z")) {
            velocity = velocity.setZ(changeValue(velocity.getZ()));
        }
        livingEntity.setVelocity(velocity);
        return false;
    }

    @Override
    public boolean useEffect(Entity livingEntity, Entity target) {
        useEffect(target);
        return false;
    }

}
