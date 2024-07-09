package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class GlidingEffect extends Effect implements EntityEffect, TargetEffect {

    private final boolean glide;

    public GlidingEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        glide = configurationSection.getBoolean("glide", true);
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setGliding(glide);
        }
    }

    @Override
    public void useEffectAgainst(Execution execution, Entity target) {
        if (target instanceof LivingEntity livingTarget) {
            livingTarget.setGliding(glide);
        }
    }
}
