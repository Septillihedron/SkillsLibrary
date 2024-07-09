package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Map;

public class DamageEffect extends Effect implements ComplexTargetEffect, EntityEffect {

    private final String damageExpression;
    private EntityDamageEvent.DamageCause damageCause;
    private final boolean shouldTriggerEvents;

    public DamageEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        damageExpression = configurationSection.getString("damage", "5");
        shouldTriggerEvents = configurationSection.getBoolean("shouldTriggerEvents", true);
        try {
            damageCause = EntityDamageEvent.DamageCause.valueOf(configurationSection.getString("cause", "CUSTOM"));
        } catch (IllegalArgumentException e) {
            SkillsLibrary.getInstance().getLogger().severe("You have entered an invalid damage cause at " + configurationSection.getCurrentPath() + ".cause");
        }
    }

    @Override
    public void useEffect(Execution exe, Entity entity, Entity target) {
        if (target instanceof LivingEntity livingTarget) {
            if (shouldTriggerEvents) livingTarget.damage(exe.expression(damageExpression, entity, target), entity);
            else livingTarget.damage(exe.expression(damageExpression, entity, target));
            livingTarget.setLastDamageCause(new EntityDamageEvent(entity, damageCause, exe.expression(damageExpression, entity, target)));
        }
    }

    @Override
    public void useEffect(Execution exe, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.damage(exe.expression(damageExpression, entity));
            livingEntity.setLastDamageCause(new EntityDamageEvent(livingEntity, damageCause, damageExpression));
        }
    }
}
