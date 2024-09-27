package me.xemor.skillslibrary2.conditions;

import me.xemor.skillslibrary2.Mode;
import me.xemor.skillslibrary2.OtherObject;
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
import java.util.function.Supplier;
import java.util.stream.Stream;

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

    public CompletableFuture<Boolean> ANDConditions(Execution execution, Entity entity, boolean exact, Object... objects) {
        OtherObject otherObject = OtherObject.create(objects.length == 0 ? null : objects[0]);
        CompletableFuture<Boolean> resultFuture = CompletableFuture.completedFuture(true);

        for (Condition condition : conditions) {
            if (!exact && otherObject.getMode() != Mode.SELF) {
                resultFuture = resultFuture.thenCompose(prev -> calculateResultAndElseBranch(execution, condition, prev, entity, new OtherObject.Empty()));
            }
            resultFuture = resultFuture.thenCompose(prev -> calculateResultAndElseBranch(execution, condition, prev, entity, otherObject));
        }
        return resultFuture;
    }

    public CompletableFuture<Boolean> calculateResultAndElseBranch(Execution execution, Condition condition, boolean currentValue, Entity entity, OtherObject otherObject) {
        if (currentValue == false) return CompletableFuture.completedFuture(currentValue);
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            return condition.isTrue(entity, otherObject)
                    .thenApply(b -> {
                                if (b == null) return currentValue;
                                if (!b) {
                                    condition.getOtherwise().handleEffects(execution, entity, otherObject);
                                }
                                return b;
                            }
                    );
        });
    }

    public CompletableFuture<Boolean> ORConditions(Execution execution, Entity entity, boolean exact, Object... objects) {
        OtherObject otherObject = OtherObject.create(objects.length == 0 ? null : objects[0]);
        CompletableFuture<Boolean> resultFuture = CompletableFuture.completedFuture(false);

        for (Condition condition : conditions) {
            if (!exact && otherObject.getMode() != Mode.SELF) {
                resultFuture = resultFuture.thenCompose(prev -> handleElseBranchForOr(execution, condition, prev, entity, new OtherObject.Empty()));
            }
            resultFuture = resultFuture.thenCompose(prev -> handleElseBranchForOr(execution, condition, prev, entity, otherObject));
        }
        return resultFuture;
    }

    public CompletableFuture<Boolean> handleElseBranchForOr(Execution execution, Condition condition, boolean currentValue, Entity entity, OtherObject otherObject) {
        if (currentValue == false) return CompletableFuture.completedFuture(currentValue);
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            return condition.isTrue(entity, otherObject)
                    .thenApply(b -> {
                                if (b == null) return currentValue;
                                if (!b) {
                                    condition.getOtherwise().handleEffects(execution, entity, otherObject);
                                }
                                return b;
                            }
                    );
        });
    }

    @NotNull
    @Override
    public Iterator<Condition> iterator() {
        return conditions.iterator();
    }
}
