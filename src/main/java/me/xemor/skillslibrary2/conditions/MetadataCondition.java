package me.xemor.skillslibrary2.conditions;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MetadataCondition extends ComparisonCondition implements EntityCondition, TargetCondition {

    private NamespacedKey variable;

    public MetadataCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        String variableStr = configurationSection.getString("variable");
        if (variableStr == null) {
            SkillsLibrary.getInstance().getLogger().severe("You need to specify a variable name! " + configurationSection.getCurrentPath() + ".variable is null!");
        }
        else {
            variable = new NamespacedKey(SkillsLibrary.getInstance(), variableStr);
        }
    }

    @Override
    public boolean isTrue(Entity boss) {
        return isTrue(boss.getPersistentDataContainer());
    }

    @Override
    public boolean isTrue(Entity entity, Entity target) {
        return isTrue(target.getPersistentDataContainer());
    }

    public boolean isTrue(Execution execution, PersistentDataContainer container) {
        Double value = container.get(variable, PersistentDataType.DOUBLE);
        return checkComparison(execution, value == null ? 0 : value);
    }

}
