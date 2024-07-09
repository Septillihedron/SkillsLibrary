package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MetadataEffect extends ModifyEffect implements EntityEffect, TargetEffect {

    private NamespacedKey variable;

    public MetadataEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        String variableStr = configurationSection.getString("variable");
        if (variableStr == null) {
            SkillsLibrary.getInstance().getLogger().severe("You need to specify a variable name! " + configurationSection.getCurrentPath() + ".variable is null!");
        }
        else {
            variable = new NamespacedKey(SkillsLibrary.getInstance(), variableStr);
        }
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        setVariable(entity.getPersistentDataContainer());
    }

    @Override
    public void useEffectAgainst(Execution execution, Entity target) {
        setVariable(target.getPersistentDataContainer());
    }

    public void setVariable(PersistentDataContainer container) {
        Double value;
        value = container.get(variable, PersistentDataType.DOUBLE);
        if (value == null) {
            value = 0D;
        }
        container.set(variable, PersistentDataType.DOUBLE, changeValue(value));
    }
}

