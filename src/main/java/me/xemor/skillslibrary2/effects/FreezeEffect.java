package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class FreezeEffect extends ModifyEffect implements EntityEffect, TargetEffect {

    public FreezeEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.setFreezeTicks((int) changeValue(livingEntity.getFreezeTicks()));
        }
    }

    @Override
    public void useEffectAgainst(Execution execution, Entity target) {
        useEffect(execution, target);
    }
}
