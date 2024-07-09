package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;

public class HungerEffect extends ModifyEffect implements EntityEffect, TargetEffect {

	public HungerEffect(int effect, ConfigurationSection configurationSection) {
		super(effect, configurationSection);
	}

	@Override
	public void useEffect(Execution execution, Entity entity) {
		if (entity instanceof HumanEntity) {
			((HumanEntity) entity).setFoodLevel((int) changeValue(((HumanEntity) entity).getFoodLevel()));
		}
	}

	@Override
	public void useEffectAgainst(Execution execution, Entity target) {
		useEffect(execution, target);
	}

}

