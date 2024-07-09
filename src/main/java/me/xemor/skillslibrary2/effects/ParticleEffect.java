package me.xemor.skillslibrary2.effects;

import me.creeves.particleslibrary.ParticleData;
import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class ParticleEffect extends Effect implements EntityEffect, TargetEffect, LocationEffect {

    private ParticleData particleData;

    public ParticleEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        if (!configurationSection.contains("particle")) {
            SkillsLibrary.getInstance().getLogger().warning("You have not specified a particle section at " + configurationSection.getCurrentPath() + ".particle");
        }
        ConfigurationSection particle = configurationSection.getConfigurationSection("particle");
        if (particle != null) {
            particleData = new ParticleData(particle);
        }
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            particleData.spawnParticle(livingEntity);
        }
    }

    @Override
    public void useEffectAgainst(Execution execution, Entity target) {
        if (target instanceof LivingEntity livingEntity) {
            particleData.spawnParticle(livingEntity);
        }
    }


    @Override
    public void useEffectAgainst(Execution execution, Location location) {
        particleData.spawnParticle(location);
    }
}