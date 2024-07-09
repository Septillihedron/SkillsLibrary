package me.xemor.skillslibrary2.effects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

public class ShooterEffect extends WrapperEffect implements ComplexTargetEffect {

    private final boolean onlyProjectiles;

    public ShooterEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        onlyProjectiles = configurationSection.getBoolean("onlyProjectiles", true);
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        if (target instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();
            handleEffects(entity);
            handleEffects(entity, shooter);
        }
        else if (!onlyProjectiles) {
            handleEffects(entity);
            handleEffects(entity, target);
        }
        return false;
    }
}
