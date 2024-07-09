package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

public class LightningEffect extends Effect implements EntityEffect, TargetEffect {

    private final boolean isFake;

    public LightningEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        isFake = configurationSection.getBoolean("fake", false);
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        strikeLightning(entity);
    }

    @Override
    public void useEffectAgainst(Execution execution, Entity target) {
        strikeLightning(target);
    }

    public void strikeLightning(Entity entity) {
        World world = entity.getWorld();
        if (isFake) {
            world.strikeLightningEffect(entity.getLocation());
        }
        else {
            world.strikeLightning(entity.getLocation());
        }
    }
}
