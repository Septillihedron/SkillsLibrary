package me.xemor.skillslibrary2.effects;

import me.xemor.configurationdata.entity.EntityData;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ProjectileEffect extends Effect implements EntityEffect, ComplexTargetEffect, ComplexLocationEffect {

    private final EntityData projectile;
    private final double velocity;

    public ProjectileEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        projectile = EntityData.create(configurationSection, "entity", EntityType.SNOWBALL);
        velocity = configurationSection.getDouble("velocity", 1.0);
    }

    @Override
    public boolean useEffect(Execution execution, Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            Vector velocity = livingEntity.getEyeLocation().getDirection().multiply(this.velocity);
            Entity projectile = this.projectile.spawnEntity(livingEntity.getEyeLocation().add(velocity));
            projectile.setVelocity(velocity);
        }
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        useEffect(entity, target.getLocation());
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Location location) {
        Vector velocity = location.subtract(entity.getLocation()).toVector().normalize().multiply(this.velocity);
        Entity projectileEntity = projectile.spawnEntity(entity.getLocation().add(velocity));
        projectileEntity.setVelocity(velocity);
        return false;
    }
}
