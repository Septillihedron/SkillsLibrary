package me.xemor.skillslibrary2.effects;

import me.xemor.configurationdata.entity.EntityData;
import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ProjectileEffect extends Effect implements EntityEffect, TargetEffect, ComplexLocationEffect {

    private final EntityData projectile;
    private final double velocity;

    public ProjectileEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        projectile = EntityData.create(configurationSection, "entity", EntityType.SNOWBALL);
        velocity = configurationSection.getDouble("velocity", 1.0);
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            if (entity instanceof LivingEntity livingEntity) {
                Vector velocity = livingEntity.getEyeLocation().getDirection().multiply(this.velocity);
                Entity projectile = this.projectile.spawnEntity(livingEntity.getEyeLocation().add(velocity));
                projectile.setVelocity(velocity);
            }
        });
    }

    @Override
    public void useEffect(Execution execution, Entity entity, Entity target) {
        SkillsLibrary.getFoliaHacks().runASAP(target, () -> useEffect(execution, entity, target.getLocation()));
    }

    @Override
    public void useEffect(Execution execution, Entity entity, Location location) {
        SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            Vector velocity = location.subtract(entity.getLocation()).toVector().normalize().multiply(this.velocity);
            Entity projectileEntity = projectile.spawnEntity(entity.getLocation().add(velocity));
            projectileEntity.setVelocity(velocity);
        });
    }
}
