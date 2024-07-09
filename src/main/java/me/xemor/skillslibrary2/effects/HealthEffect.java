package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class HealthEffect extends ModifyEffect implements EntityEffect, TargetEffect {

	public HealthEffect(int effect, ConfigurationSection configurationSection) {
		super(effect, configurationSection);
	}

	@Override
	public void useEffect(Execution execution, Entity entity) {
		if (entity instanceof LivingEntity livingEntity) {
			double newHealth = (int) changeValue(livingEntity.getHealth());
			if (newHealth < 0) newHealth = 0;
			double maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
			if (newHealth > maxHealth) newHealth = maxHealth;
			((LivingEntity) entity).setHealth(newHealth);
		}
	}

	@Override
	public void useEffectAgainst(Execution execution, Entity target) {
		useEffect(execution, target);
	}

}

