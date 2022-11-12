package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.Mode;
import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.conditions.Conditions;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public abstract class Effect {

    private final int effect;
    private Mode mode = null;

    private final ConfigurationSection configurationSection;

    public Effect(int effect, ConfigurationSection configurationSection) {
        this.configurationSection = configurationSection;
        this.effect = effect;
        try {
            mode = Mode.valueOf(configurationSection.getString("mode", "ALL"));
        } catch (IllegalArgumentException e) {
            SkillsLibrary.getInstance().getLogger().severe("You have entered an invalid mode! " + configurationSection.getCurrentPath() + ".mode");
        }
        if (!supports(mode)) {
            SkillsLibrary.getInstance().getLogger().severe(Effects.getName(effect) + " does not support " + mode.name() + ". Please change the mode at " + configurationSection.getCurrentPath() + ".mode");
        }
    }

    public int getEffect() {
        return effect;
    }

    private boolean supports(Mode mode) {
        switch (mode) {
            case ALL: return true;
            case SELF: return this instanceof EntityEffect;
            case OTHER: return this instanceof TargetEffect;
            case LOCATION: return this instanceof LocationEffect;
        }
        return false;
    }

    @Nullable
    public static Effect create(int effect, ConfigurationSection configurationSection) {
        try {
            return Effects.getClass(effect).getConstructor(int.class, ConfigurationSection.class).newInstance(effect, configurationSection);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Throwable result = e;
            if (e instanceof InvocationTargetException c) {
                result = c.getCause();
            }
            Bukkit.getLogger().severe("Exception for " + Effects.getClass(effect).getName());
            result.printStackTrace();
        }
        return null;
    }

    public Mode getMode() {
        return mode;
    }
    
    public ConfigurationSection getConfigurationSection() {
        return configurationSection;
    }

}
