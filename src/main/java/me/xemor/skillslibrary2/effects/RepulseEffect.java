package me.xemor.skillslibrary2.effects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class RepulseEffect extends Effect implements ComplexTargetEffect {

    private final double velocity;
    private final boolean add;

    public RepulseEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        velocity = configurationSection.getDouble("velocity", 1.0);
        add = configurationSection.getBoolean("add", false);
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        repulse(entity, target);
        return false;
    }

    public void repulse(Entity boss, Entity target) {
        Vector repulseVelocity = target.getLocation().subtract(boss.getLocation()).toVector().normalize().multiply(velocity);
        if (Double.isNaN(repulseVelocity.lengthSquared())) {
            return;
        }
        if (add) {
            target.setVelocity(target.getVelocity().add(repulseVelocity));
        }
        else {
            target.setVelocity(repulseVelocity);
        }
    }
}
