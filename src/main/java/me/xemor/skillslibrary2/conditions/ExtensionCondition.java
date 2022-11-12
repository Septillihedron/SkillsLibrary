package me.xemor.skillslibrary2.conditions;

import me.xemor.skillslibrary2.Mode;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A condition that extends the functionality of other conditions
 */
public abstract class ExtensionCondition extends Condition {

    protected Condition insideCondition;
    protected String insideConditionName;
    protected String conditionName;

    public ExtensionCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        createInsideCondition();
    }

    public ExtensionCondition(int condition, ConfigurationSection configurationSection, boolean createInsideCondition) {
        super(condition, configurationSection);
        if (createInsideCondition) createInsideCondition();
    }

    protected Condition createInsideCondition() {
        return createInsideCondition(getConfigurationSection());
    }

    protected Condition createInsideCondition(ConfigurationSection configurationSection) {
        conditionName = configurationSection.getString("type");
        insideConditionName = getRealConditionName(conditionName);

        int conditionType = Conditions.getCondition(insideConditionName);
        if (conditionType == -1) {
            Bukkit.getLogger().warning("Invalid Condition Specified: " + insideCondition +" from extension condition " + conditionName + " at " + configurationSection.getCurrentPath() + ".type");
        }
        insideCondition = Condition.create(conditionType, configurationSection);

        if (!support(getMode())) {
            SkillsLibrary.getInstance().getLogger().severe(insideConditionName + " does not support " + getMode().name() + ". Please change the mode at " + configurationSection.getCurrentPath() + ".mode");
        }
        return insideCondition;
    }

    /**
     * Called by the constructor to check that this class supports the mode.
     * By default, it checks if this condition and the condition extended by it implements the interface associated with the mode, that is:
     * <ul>
     *     <li>{@link Mode#ALL} -> any condition, always returns true</li>
     *     <li>{@link Mode#SELF} -> {@link EntityCondition}</li>
     *     <li>{@link Mode#OTHER} -> {@link TargetCondition}</li>
     *     <li>{@link Mode#LOCATION} -> {@link LocationCondition}</li>
     *     <li>{@link Mode#ITEM} -> {@link ItemStackCondition}</li>
     * </ul>
     * @param mode The mode to check whether this ExtensionCondition supports it
     * @return Whether this class supports the mode
     */
    protected boolean support(Mode mode) {
        return switch (mode) {
            case ALL -> true;
            case SELF -> this instanceof EntityCondition && insideCondition instanceof EntityCondition;
            case OTHER -> this instanceof TargetCondition && insideCondition instanceof TargetCondition;
            case LOCATION -> this instanceof LocationCondition && insideCondition instanceof LocationCondition;
            case ITEM -> this instanceof ItemStackCondition && insideCondition instanceof ItemStackCondition;
        };
    }

    /**
     * Converts one of the name of this condition into the name of the condition this class extends
     * @param extendedName The one of the name of this condition
     * @return The condition name that this class extends
     */
    abstract protected String getRealConditionName(String extendedName);

}
