package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.Mode;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EffectList implements Iterable<Effect> {

    private List<Effect> effects = new ArrayList<>(1);
    private static EffectList effectList = new EffectList();

    public EffectList(ConfigurationSection effectsSection) {
        loadEffects(effectsSection);
    }

    public EffectList() {}

    private void loadEffect(ConfigurationSection effectSection) {
        String effectTypeStr = effectSection.getString("type", "FLING");
        int effectType = Effects.getEffect(effectTypeStr);
        if (effectType == -1) {
            Bukkit.getLogger().warning("Invalid Effect Specified: " + effectTypeStr + " at " + effectSection.getCurrentPath() + ".type");
            return;
        }
        effects.add(Effect.create(effectType, effectSection));
    }

    public void loadEffects(ConfigurationSection effectsSection) {
        Collection<Object> values = effectsSection.getValues(false).values();
        effects = new ArrayList<>(values.size());
        for (Object item : values) {
            if (item instanceof ConfigurationSection effectSection) {
                loadEffect(effectSection);
            }
        }
    }

    public boolean handleEffects(Execution execution, Entity entity, Object... objects) {
        boolean result = false;
        Object otherObject = objects.length == 0 ? null : objects[0];
        for (Effect effect : effects) {
            if (effect instanceof EntityEffect entityEffect && effect.getMode().runs(Mode.SELF)) {
                entityEffect.useEffect(execution, entity);
            }
            if (effect instanceof ComplexTargetEffect complexTargetEffect && effect.getMode().runs(Mode.OTHER) && otherObject instanceof Entity) {
                complexTargetEffect.useEffect(execution, entity, (Entity) otherObject);
            }
            else if (effect instanceof ComplexLocationEffect complexLocationEffect && effect.getMode().runs(Mode.LOCATION) && otherObject instanceof Location) {
                complexLocationEffect.useEffect(execution, entity, (Location) otherObject);
            }
            else if (effect instanceof ItemStackEffect itemStack && effect.getMode().runs(Mode.ITEM) && otherObject instanceof ItemStack) {
                itemStack.useEffect(execution, entity, (ItemStack) otherObject);
            }
        }
        return result;
    }

    public boolean handleExactEffects(Execution execution, Entity entity, Object... objects) {
        boolean result = false;
        Object otherObject = objects.length == 0 ? null : objects[0];
        for (Effect effect : effects) {
            if (effect instanceof EntityEffect entityEffect && effect.getMode().runs(Mode.SELF) && otherObject == null) {
                entityEffect.useEffect(execution, entity);
            }
            else if (effect instanceof ComplexTargetEffect complexTargetEffect && effect.getMode().runs(Mode.OTHER) && otherObject instanceof Entity) {
                complexTargetEffect.useEffect(execution, entity, (Entity) otherObject);
            }
            else if (effect instanceof ComplexLocationEffect complexLocationEffect && effect.getMode().runs(Mode.LOCATION) && otherObject instanceof Location) {
                complexLocationEffect.useEffect(execution, entity, (Location) otherObject);
            }
            else if (effect instanceof ItemStackEffect itemStackEffect && effect.getMode().runs(Mode.ITEM) && otherObject instanceof ItemStack) {
                itemStackEffect.useEffect(execution, entity, (ItemStack) otherObject);
            }
        }
        return result;
    }

    public static EffectList effectList() {
        return effectList;
    }

    @NotNull
    @Override
    public Iterator<Effect> iterator() {
        return effects.iterator();
    }
}
