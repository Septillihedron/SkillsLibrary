package me.xemor.skillslibrary2.conditions;

import me.xemor.skillslibrary2.Mode;
import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConditionList implements Iterable<Condition> {

    private static final Executor completer = Executors.newSingleThreadExecutor();
    private List<Condition> conditions = new ArrayList<>(1);

    public ConditionList(ConfigurationSection conditionsSection) {
        loadConditions(conditionsSection);
    }

    public ConditionList() {}


    private void loadConditions(ConfigurationSection conditionsSection) {
        if (conditionsSection == null) return;
        Map<String, Object> values = conditionsSection.getValues(false);
        conditions = new ArrayList<>(values.size());
        for (Object item : values.values()) {
            if (item instanceof ConfigurationSection conditionSection) {
                int condition = Conditions.getCondition(conditionSection.getString("type"));
                if (condition == -1) {
                    Bukkit.getLogger().warning("Invalid Condition Type at " + conditionSection.getCurrentPath() + ".type");
                    continue;
                }
                Condition conditionData = Condition.create(condition, conditionSection);
                if (conditionData != null) {
                    conditions.add(conditionData);
                }
            }
        }
    }

    @Deprecated
    public boolean areConditionsTrue(Entity entity, Object... objects) {
        return ANDConditions(entity, false, objects);
    }

    public CompletableFuture<Boolean> ANDConditions(Execution execution, Entity entity, boolean exact, Object... objects) {
        Object otherObject = objects.length == 0 ? null : objects[0];
        AtomicBoolean workingValue = new AtomicBoolean(true);
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (Condition condition : conditions) {
            if (workingValue.get() && condition instanceof EntityCondition entityCondition && condition.getMode().runs(Mode.SELF) && (!exact || otherObject == null)) {
                futures.add(SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
                    boolean b = entityCondition.isTrue(entity);
                    if (!b && workingValue.getAndSet(false)) {
                        condition.getOtherwise().handleEffects(execution, entity);
                    }
                }));
            }
            if (workingValue.get() && condition instanceof TargetCondition targetCondition && otherObject instanceof Entity other && condition.getMode().runs(Mode.OTHER)) {
                futures.add(SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
                    boolean b = targetCondition.isTrue(entity, other);
                    if (!b && workingValue.getAndSet(false)) {
                        condition.getOtherwise().handleEffects(execution, entity, other);
                    }
                }));
            }
            if (workingValue.get() && condition instanceof LocationCondition locationCondition && otherObject instanceof Location location && condition.getMode().runs(Mode.LOCATION)) {
                futures.add(SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
                    boolean b = locationCondition.isTrue(entity, location);
                    workingValue.set(false);
                    if (!b && workingValue.getAndSet(false)) {
                        condition.getOtherwise().handleEffects(execution, entity, location);
                    }
                }));
            }
            if (workingValue.get() && condition instanceof ItemStackCondition itemStackCondition && otherObject instanceof ItemStack item && condition.getMode().runs(Mode.ITEM)) {
                futures.add(SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
                    boolean b = itemStackCondition.isTrue(entity, item);
                    if (!b && workingValue.getAndSet(false)) {
                        condition.getOtherwise().handleEffects(execution, entity, item);
                    }
                }));
            }
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).thenApply((d) -> workingValue.get());
    }

    public CompletableFuture<Boolean> ORConditions(Execution execution, Entity entity, boolean exact, Object... objects) {
        CompletableFuture<Boolean> completableResult = new CompletableFuture<>();
        Object otherObject = objects.length == 0 ? null : objects[0];
        for (Condition condition : conditions) {
            if (condition instanceof EntityCondition entityCondition && condition.getMode().runs(Mode.SELF) && (!exact || otherObject == null)) {
                boolean result = entityCondition.isTrue(entity);
                if (result) return true;
            }
            if (condition instanceof TargetCondition targetCondition && otherObject instanceof Entity other && condition.getMode().runs(Mode.OTHER)) {
                boolean result = targetCondition.isTrue(entity, other);
                if (result) return true;
            }
            else if (condition instanceof LocationCondition locationCondition && otherObject instanceof Location location && condition.getMode().runs(Mode.LOCATION)) {
                boolean result = locationCondition.isTrue(entity, location);
                if (result) return true;
            }
            else if (condition instanceof ItemStackCondition itemStackCondition && otherObject instanceof ItemStack item && condition.getMode().runs(Mode.ITEM)) {
                boolean result = itemStackCondition.isTrue(entity, item);
                if (result) result;
            }
        }
        return completableResult;
    }

    public void addCondition(Condition condition) {
        conditions.add(0, condition);
    }

    @NotNull
    @Override
    public Iterator<Condition> iterator() {
        return conditions.iterator();
    }
}
