package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.Mode;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

/**
 * An effect that extends the functionality of other effects
 */
public abstract class ExtensionEffect extends Effect {

    protected Effect insideEffect;
    protected String insideEffectName;
    protected String effectName;

    public ExtensionEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        createInsideEffect();
    }

    public ExtensionEffect(int effect, ConfigurationSection configurationSection, boolean createInsideEffect) {
        super(effect, configurationSection);
        if (createInsideEffect) createInsideEffect();
    }

    protected Effect createInsideEffect() {
        return createInsideEffect(getConfigurationSection());
    }

    protected Effect createInsideEffect(ConfigurationSection configurationSection) {
        effectName = configurationSection.getString("type", "FLING");
        insideEffectName = getRealEffectName(effectName);

        int effectType = Effects.getEffect(insideEffectName);
        if (effectType == -1) {
            Bukkit.getLogger().warning("Invalid Effect Specified: " + insideEffectName + " from extension effect " + effectName + " at " + configurationSection.getCurrentPath() + ".type");
        }
        insideEffect = Effect.create(effectType, configurationSection);

        if (!support(getMode())) {
            SkillsLibrary.getInstance().getLogger().severe(insideEffectName + " does not support " + getMode().name() + ". Please change the mode at " + configurationSection.getCurrentPath() + ".mode");
        }
        return insideEffect;
    }

    /**
     * Called by the constructor to check that this class supports the mode.
     * By default, it checks if this effect and the effect extended by it implements the interface associated with the mode, that is:
     * <ul>
     *     <li>{@link Mode#ALL} -> any effect, always returns true</li>
     *     <li>{@link Mode#SELF} -> {@link EntityEffect}</li>
     *     <li>{@link Mode#OTHER} -> {@link TargetEffect}</li>
     *     <li>{@link Mode#LOCATION} -> {@link LocationEffect}</li>
     *     <li>{@link Mode#ITEM} -> {@link ItemStackEffect}</li>
     * </ul>
     * @param mode The mode to check whether this ExtensionEffect supports it
     * @return Whether this class supports the mode
     */
    protected boolean support(Mode mode) {
        return switch (mode) {
            case ALL -> true;
            case SELF -> this instanceof EntityEffect && insideEffect instanceof EntityEffect;
            case OTHER -> this instanceof TargetEffect && insideEffect instanceof TargetEffect;
            case LOCATION -> this instanceof LocationEffect && insideEffect instanceof LocationEffect;
            case ITEM -> this instanceof ItemStackEffect && insideEffect instanceof ItemStackEffect;
        };
    }

    /**
     * Converts one of the name of this effect into the name of the effect this class extends
     * @param extendedName The one of the name of this effect
     * @return The effect name that this class extends
     */
    abstract protected String getRealEffectName(String extendedName);

}
