package me.xemor.skillslibrary2.conditions;

import me.xemor.skillslibrary2.evaluation.Evaluator;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class EvaluatingCondition extends ExtensionCondition implements EntityCondition, TargetCondition, LocationCondition, ItemStackCondition {

    public static final String SUFFIX = "$EVAL";

    public EvaluatingCondition(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection, false);
    }

    private Condition evaluateInsideCondition(Entity self, Object otherObject) {
        var section = getConfigurationSection();
        var evaluatedSection = new Evaluator(self, otherObject).evaluate(section);
        return createInsideCondition(evaluatedSection);  
    }

    @Override
    public boolean isTrue(Entity self) {
        Condition evaluatedCondition = evaluateInsideCondition(self, null);
        if (evaluatedCondition instanceof EntityCondition entityCondition) return entityCondition.isTrue(self);
        else return true;
    }
    @Override
    public boolean isTrue(Entity self, Entity other) {
        Condition evaluatedCondition = evaluateInsideCondition(self, other);
        if (evaluatedCondition instanceof TargetCondition targetCondition) return targetCondition.isTrue(self, other);
        else return true;
    }
    @Override
    public boolean isTrue(Entity self, Location location) {
        Condition evaluatedCondition = evaluateInsideCondition(self, location);
        if (evaluatedCondition instanceof LocationCondition locationCondition) return locationCondition.isTrue(self, location);
        else return true;
    }
    @Override
    public boolean isTrue(Entity self, ItemStack item) {
        Condition evaluatedCondition = evaluateInsideCondition(self, item);
        if (evaluatedCondition instanceof ItemStackCondition itemStackCondition) return itemStackCondition.isTrue(self, item);
        else return true;
    }

    @Override
    protected String getRealConditionName(String extendedName) {
        return extendedName.substring(0, extendedName.length()-SUFFIX.length());
    }

}
