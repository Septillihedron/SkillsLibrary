package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.Mode;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public abstract class Effect {

    private final int effect;
    private Mode mode = null;

    public Effect(int effect, ConfigurationSection configurationSection) {
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
        return switch (mode) {
            case ALL -> true;
            case SELF -> this instanceof EntityEffect;
            case OTHER -> this instanceof TargetEffect || this instanceof TargetEffect;
            case LOCATION -> this instanceof ComplexLocationEffect || this instanceof LocationEffect;
            default -> false;
        };
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

}
