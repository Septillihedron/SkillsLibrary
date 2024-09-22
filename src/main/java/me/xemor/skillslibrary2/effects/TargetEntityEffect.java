package me.xemor.skillslibrary2.effects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

public class TargetEntityEffect extends Effect implements TargetEffect {

    public TargetEntityEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        if (entity instanceof Mob && target instanceof LivingEntity) {
            Mob mob = (Mob) entity;
            LivingEntity livingTarget = (LivingEntity) target;
            mob.setTarget(livingTarget);
        }
        return false;
    }

}
