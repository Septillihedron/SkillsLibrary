package me.xemor.skillslibrary2.evaluation;

import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * A class that evaluates a {@link ConfigurationSection}
 * based on an {@link Entity} self and optional objects {@link Entity} other, {@link Location} location, and {@link ItemStack} item
 */
public class Evaluator {

    /**
     * The suffix on a {@link ConfigurationSection} key that tells the {@link Evaluator} to evaluate the entry
     */
    public static final String EVAL_SUFFIX = "$eval";

    private final Entity self;
    private final Entity other;
    private final Location location;
    private final ItemStack item;
    
    private final Logger logger;

    /**
     * Creates an {@link Evaluator} with self set and all optional objects null
     * @param self The entity that is using this condition or effect, the user
     */
    public Evaluator(Entity self) {
        this(self, null, null, null);
    }
    /**
     * Creates an {@link Evaluator} with self and other set and all other optional objects null
     * @param self The {@link Entity} that is using this condition or effect, the user
     * @param other The {@link Entity} that is used as the target entity of the condition or effect
     */
    public Evaluator(Entity self, Entity other) {
        this(self, other, null, null);
    }
    /**
     * Creates an {@link Evaluator} with self and location set and all other optional objects null
     * @param self The {@link Entity} that is using this condition or effect, the user
     * @param location The {@link Location} that is used as the target location of the condition or effect
     */
    public Evaluator(Entity self, Location location) {
        this(self, null, location, null);
    }
    /**
     * Creates an {@link Evaluator} with self and item set and all other optional objects null
     * @param self The {@link Entity} that is using this condition or effect, the user
     * @param item The {@link ItemStack} that is used as the target item of the condition or effect
     */
    public Evaluator(Entity self, ItemStack item) {
        this(self, null, null, item);
    }

    /**
     * Creates an {@link Evaluator} with self set and one of optional objects set: <br>
     * <ul>
     *    <li>other set as otherObject if otherObject is an {@link Entity},</li>
     *    <li>location set as otherObject if otherObject is a {@link Location}, or </li>
     *    <li>item set as otherObject if otherObject is an {@link ItemStack} </li>
     * </ul>
     * and all other optional objects null. <br>
     * In the case that otherObject is null or is anything other than an {@link Entity}, {@link Location}, or {@link ItemStack}, no optional objects will be set
     *
     * @param self The {@link Entity} that is using this condition or effect, the user
     * @param otherObject An optional objects that can be an {@link Entity}, {@link Location}, or {@link ItemStack}
     */
    public Evaluator(Entity self, @Nullable Object otherObject) {
        this(self,
            (otherObject instanceof Entity other)? other : null,
            (otherObject instanceof Location location)? location : null,
            (otherObject instanceof ItemStack item)? item : null);
    }

    /**
     * Creates an {link Evaluator} with self and all optional objects set
     * @param self The {@link Entity} that is using this condition or effect, the user
     * @param other The {@link Entity} that is used as the target entity of the condition or effect
     * @param location The {@link Location} that is used as the target location of the condition or effect
     * @param item The {@link ItemStack} that is used as the target item of the condition or effect
     */
    public Evaluator(Entity self, Entity other, Location location, ItemStack item) {
        this.self = self;
        this.other = other;
        this.location = location;
        this.item = item;
        this.logger = SkillsLibrary.getInstance().getLogger();
    }

    /**
     * Evaluates all of {@code section}'s entries with keys that have the suffix {@link #EVAL_SUFFIX} and removes the suffix on the key
     * @param section The {@link ConfigurationSection} to evaluate
     * @return An evaluated {@link ConfigurationSection}
     */
    public ConfigurationSection evaluate(ConfigurationSection section) {
        var evaluatedSection = new MemoryConfiguration();
        String pathPrefix = section.getCurrentPath();
        if (pathPrefix == null) pathPrefix = "";
        for (var entry : section.getValues(false).entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (!key.endsWith(EVAL_SUFFIX)) {
                evaluatedSection.set(pathPrefix+"."+key, val);
                continue;
            }
            Object evaluatedValue;
            if (val instanceof ConfigurationSection subSection) {
                evaluatedValue = evaluate(subSection);
            } else if (val instanceof String variableName) {
                evaluatedValue = evaluateVariable(variableName, section, section.getCurrentPath()+"."+key);
            } else {
                logger.severe("Evaluated properties must be of type string or section! At "+pathPrefix+"."+key);
                continue;
            }
            evaluatedSection.set(pathPrefix+"."+key.substring(0, key.length()-EVAL_SUFFIX.length()), evaluatedValue);
        }
        return evaluatedSection.getConfigurationSection(pathPrefix);
    }

    private Object evaluateVariable(String variableName, ConfigurationSection section, String path) {
        String[] identifiers = variableName.split("\\.");
        if (identifiers.length == 0) {
            logger.severe("Value of evaluated properties cannot be empty! at "+section.getCurrentPath());
            return null;
        }
        var nextIdentifiers = removeHead(identifiers);
        if (identifiers[0].equals("#"       )) return evaluateConfigPath          (nextIdentifiers, section , path);
        if (identifiers[0].equals("self"    )) return evaluatePersistentDataHolder(nextIdentifiers, self    , path);
        if (identifiers[0].equals("other"   )) return evaluatePersistentDataHolder(nextIdentifiers, other   , path);
        if (identifiers[0].equals("location")) return evaluateObject              (nextIdentifiers, location, path);
        if (identifiers[0].equals("item"    )) return evaluateObject              (nextIdentifiers, item    , path);
        logger.severe("Variable "+identifiers[0]+" does not exist. Only \"#\", \"self\", \"other\", \"location\", and \"item\" are available");
        return null;
    }

    private String toFieldName(String getterName) {
        getterName = getterName.substring("get".length());
        getterName = getterName.substring(0, 1).toLowerCase() + getterName.substring(1, getterName.length());
        return getterName;
    }

    //TODO add field access and methods like toString
    //maybe this isn't a good idea?
    private Object evaluateObject(String[] identifiers, Object object, String path) {
        if (identifiers.length == 0) return object;
        var getter = Arrays.stream(object.getClass().getMethods())
            .filter(method -> method.getName().startsWith("get") && method.getParameterCount() == 0)
            .filter(method -> toFieldName(method.getName()).equals(identifiers[0]))
            .findAny();
        if (getter.isEmpty()) {
            logger.severe("Object "+object+" does not contain getter for field "+identifiers[0]+"! At "+path);
            return null;
        }
        try {
            Object result = getter.get().invoke(object);
            String[] nextIdentifiers = removeHead(identifiers);
            if (result instanceof PersistentDataHolder persistentDataHolder) return evaluatePersistentDataHolder(nextIdentifiers, persistentDataHolder, path);
            else return evaluateObject(nextIdentifiers, result, path);
        } catch (IllegalAccessException e) {
            logger.severe("Failed to get access to method "+getter.get().getName()+" of class "+object.getClass().getSimpleName()+". At "+path);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            logger.severe("Exception from method "+getter.get().getName()+" of class "+object.getClass().getSimpleName()+". At "+path);
            e.getCause().printStackTrace();
        }
        return null;
    }
    private Object evaluatePersistentDataHolder(String[] identifiers, PersistentDataHolder persistentDataHolder, String path) {
        if (identifiers.length == 0) return persistentDataHolder;
        var container = persistentDataHolder.getPersistentDataContainer();
        var variable = new NamespacedKey(SkillsLibrary.getInstance(), identifiers[0]);
        if (!container.has(variable, PersistentDataType.DOUBLE)) return evaluateObject(identifiers, persistentDataHolder, path);
        return container.get(variable, PersistentDataType.DOUBLE);
    }

    //TODO enable access to other files
    private Object evaluateConfigPath(@NotNull String[] identifiers, @NotNull ConfigurationSection section, String path) {
        ConfigurationSection targetedSection = section.getRoot();
        if (targetedSection == null) {
            logger.severe("Failed to get root of "+section.getCurrentPath());
            return null;
        }
        if (identifiers.length == 0) {
            return targetedSection;
        }
        for (int i=0; i<identifiers.length-1; i++) {
            String identifier = identifiers[i];
            var nextSection = targetedSection.getConfigurationSection(identifier);
            if (nextSection == null) {
                logger.severe("Section "+targetedSection.getCurrentPath()+" does not contain path "+identifier+"! At "+path);
                return null;
            }
            targetedSection = nextSection;
        }
        return targetedSection.get(identifiers[identifiers.length-1]);
    }

    private static String[] removeHead(String[] arr) {
        return Arrays.copyOfRange(arr, 1, arr.length);
    }

}
