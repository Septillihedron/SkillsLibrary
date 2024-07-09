package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.Map;

public class IgniteEffect extends Effect implements EntityEffect, TargetEffect {

    private final String fireTicksExpression;

    public IgniteEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        fireTicksExpression = configurationSection.getString("fireTicks", "40");
    }

    @Override
    public void useEffect(Execution exe, Entity entity) {
        entity.setFireTicks((int) Math.round(exe.expression(fireTicksExpression, entity)));
    }

    @Override
    public void useEffectAgainst(Execution exe, Entity target) {
        target.setFireTicks((int) Math.round(exe.expression(fireTicksExpression, Map.of("other", target.getPersistentDataContainer()))));
    }
}
