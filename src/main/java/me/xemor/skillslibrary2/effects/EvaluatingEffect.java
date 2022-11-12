package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.evaluation.Evaluator;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class EvaluatingEffect extends ExtensionEffect implements EntityEffect, TargetEffect, LocationEffect, ItemStackEffect {

    public static final String SUFFIX = "$EVAL";

    public EvaluatingEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection, false);
    }

    private Effect evaluateInsideEffect(Entity self, Object otherObject) {
        var section = getConfigurationSection();
        var evaluatedSection = new Evaluator(self, otherObject).evaluate(section);
        return createInsideEffect(evaluatedSection);
    }

    @Override
    public boolean useEffect(Entity self) {
        Effect evaluatedEffect = evaluateInsideEffect(self, null);
        if (evaluatedEffect instanceof EntityEffect entityEffect) return entityEffect.useEffect(self);
        else return false;
    }
    @Override
    public boolean useEffect(Entity self, Entity other) {
        Effect evaluatedEffect = evaluateInsideEffect(self, other);
        if (evaluatedEffect instanceof TargetEffect targetEffect) return targetEffect.useEffect(self, other);
        else return false;
    }
    @Override
    public boolean useEffect(Entity self, Location location) {
        Effect evaluatedEffect = evaluateInsideEffect(self, location);
        if (evaluatedEffect instanceof LocationEffect locationEffect) return locationEffect.useEffect(self, location);
        else return false;
    }
    @Override
    public boolean useEffect(Entity self, ItemStack item) {
        Effect evaluatedEffect = evaluateInsideEffect(self, item);
        if (evaluatedEffect instanceof ItemStackEffect itemStackEffect) return itemStackEffect.useEffect(self, item);
        else return false;
    }

    @Override
    protected String getRealEffectName(String extendedName) {
        return extendedName.substring(0, extendedName.length()-SUFFIX.length());
    }

}
