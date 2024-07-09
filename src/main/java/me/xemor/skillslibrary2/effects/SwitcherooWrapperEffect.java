package me.xemor.skillslibrary2.effects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

public class SwitcherooWrapperEffect extends WrapperEffect implements ComplexTargetEffect {

    public SwitcherooWrapperEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        return handleEffects(target, entity) || handleEffects(target);
    }

}
