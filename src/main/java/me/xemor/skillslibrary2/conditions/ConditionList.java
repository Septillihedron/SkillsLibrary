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

    public CompletableFuture<Boolean> ANDConditions(Execution execution, Entity entity, boolean exact, Object... objects) {
        Object otherObject = objects.length == 0 ? null : objects[0];
        CompletableFuture<Boolean> resultFuture = CompletableFuture.completedFuture(true);
        for (Condition condition : conditions) {
            resultFuture = resultFuture.thenCompose(previousResult -> {
                CompletableFuture<Boolean> future = CompletableFuture.completedFuture(previousResult);
                if (condition instanceof EntityCondition entityCondition && condition.getMode().runs(Mode.SELF) && (!exact || otherObject == null)) {
                    future.thenCompose((bool) -> calculateResultAndElseBranch(execution, entityCondition, bool, entity));
                }
                if (condition instanceof TargetCondition targetCondition && otherObject instanceof Entity other && condition.getMode().runs(Mode.OTHER)) {
                    future.thenCompose((bool) -> calculateResultAndElseBranch(execution, targetCondition, bool, entity, other));
                }
                if (condition instanceof LocationCondition locationCondition && otherObject instanceof Location location && condition.getMode().runs(Mode.LOCATION)) {
                    future.thenCompose((bool) -> calculateResultAndElseBranch(execution, locationCondition, bool, entity, location));
                }
                if (condition instanceof ItemStackCondition itemStackCondition && otherObject instanceof ItemStack item && condition.getMode().runs(Mode.ITEM)) {
                    future.thenCompose((bool) -> calculateResultAndElseBranch(execution, itemStackCondition, bool, entity, item));
                }
                return future;
            });
        }
        return resultFuture;
    }


    public CompletableFuture<Boolean> calculateResultAndElseBranch(Execution execution, EntityCondition entityCondition, boolean currentValue, Entity entity) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            CompletableFuture<Boolean> completableB = CompletableFuture.completedFuture(entityCondition.isTrue(entity));
            completableB = completableB
                    .thenApply((b) -> b && currentValue)
                    .thenApply(b -> {
                        if (!b) {
                            if (entityCondition instanceof Condition condition) {
                                condition.getOtherwise().handleEffects(execution, entity);
                            }
                        }
                        return b;
                    }
            );
            return completableB;
        });
    }

    public CompletableFuture<Boolean> calculateResultAndElseBranch(Execution execution, TargetCondition targetCondition, boolean currentValue, Entity entity, Entity other) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            CompletableFuture<Boolean> completableB = targetCondition.isTrue(entity, other);
            completableB
                    .thenApply((b) -> b && currentValue)
                    .thenAccept(b -> {
                if (!b) {
                    if (targetCondition instanceof Condition condition) {
                        condition.getOtherwise().handleEffects(execution, entity, other);
                    }
                }
            });
            return completableB;
        });
    }

    public CompletableFuture<Boolean> calculateResultAndElseBranch(Execution execution, ItemStackCondition itemCondition, boolean currentValue, Entity entity, ItemStack item) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            CompletableFuture<Boolean> completableB = itemCondition.isTrue(entity, item);
            completableB.thenApply((b) -> b && currentValue).thenAccept(b -> {
                if (!b) {
                    if (itemCondition instanceof Condition condition) {
                        condition.getOtherwise().handleEffects(execution, entity, item);
                    }
                }
            });
            return completableB;
        });
    }

    public CompletableFuture<Boolean> calculateResultAndElseBranch(Execution execution, LocationCondition locationCondition, boolean currentValue, Entity entity, Location location) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            CompletableFuture<Boolean> completableB = locationCondition.isTrue(entity, location);
            completableB.thenApply((b) -> b && currentValue).thenAccept(b -> {
                if (!b) {
                    if (locationCondition instanceof Condition condition) {
                        condition.getOtherwise().handleEffects(execution, entity, location);
                    }
                }
            });
            return completableB;
        });
    }

    public CompletableFuture<Boolean> ORConditions(Execution execution, Entity entity, boolean exact, Object... objects) {
        Object otherObject = objects.length == 0 ? null : objects[0];
        CompletableFuture<Boolean> resultFuture = CompletableFuture.completedFuture(true);
        for (Condition condition : conditions) {
            resultFuture = resultFuture.thenCompose(previousResult -> {
                CompletableFuture<Boolean> future = CompletableFuture.completedFuture(previousResult);
                if (condition instanceof EntityCondition entityCondition && condition.getMode().runs(Mode.SELF) && (!exact || otherObject == null)) {
                    future.thenCompose((bool) -> (handleElseBranchForOr(execution, entityCondition, bool, entity)));
                }
                if (condition instanceof TargetCondition targetCondition && otherObject instanceof Entity other && condition.getMode().runs(Mode.OTHER)) {
                    future.thenCompose((bool) -> (handleElseBranchForOr(execution, targetCondition, bool, entity, other)));
                }
                if (condition instanceof LocationCondition locationCondition && otherObject instanceof Location location && condition.getMode().runs(Mode.LOCATION)) {
                    future.thenCompose((bool) -> (handleElseBranchForOr(execution, locationCondition, bool, entity, location)));
                }
                if (condition instanceof ItemStackCondition itemStackCondition && otherObject instanceof ItemStack item && condition.getMode().runs(Mode.ITEM)) {
                    future.thenCompose((bool) -> (handleElseBranchForOr(execution, itemStackCondition, bool, entity, item)));
                }
                return future;
            });
        }
        return resultFuture;
    }

    public CompletableFuture<Boolean> handleElseBranchForOr(Execution execution, EntityCondition entityCondition, boolean currentValue, Entity entity) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            CompletableFuture<Boolean> completableB = CompletableFuture.completedFuture(entityCondition.isTrue(entity));
            completableB = completableB
                    .thenApply((b) -> b && currentValue)
                    .thenApply(b -> {
                        if (!b) {
                            if (entityCondition instanceof Condition condition) {
                                condition.getOtherwise().handleEffects(execution, entity);
                            }
                        }
                        return b;
                    });
            return completableB;
        });
    }

    public CompletableFuture<Boolean> handleElseBranchForOr(Execution execution, TargetCondition targetCondition, boolean currentValue, Entity entity, Entity other) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            CompletableFuture<Boolean> completableB = targetCondition.isTrue(entity, other);
            completableB.thenApply((b) -> b && currentValue)
                    .thenAccept(b -> {
                        if (!b) {
                            if (targetCondition instanceof Condition condition) {
                                condition.getOtherwise().handleEffects(execution, entity, other);
                            }
                        }
                    });
            return completableB;
        });
    }

    public CompletableFuture<Boolean> handleElseBranchForOr(Execution execution, ItemStackCondition itemCondition, boolean currentValue, Entity entity, ItemStack item) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            CompletableFuture<Boolean> completableB = itemCondition.isTrue(entity, item);
            completableB.thenApply((b) -> b && currentValue).thenAccept(b -> {
                if (!b) {
                    if (itemCondition instanceof Condition condition) {
                        condition.getOtherwise().handleEffects(execution, entity, item);
                    }
                }
            });
            return completableB;
        });
    }

    public CompletableFuture<Boolean> handleElseBranchForOr(Execution execution, LocationCondition locationCondition, boolean currentValue, Entity entity, Location location) {
        return SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            CompletableFuture<Boolean> completableB = locationCondition.isTrue(entity, location);
            completableB.thenApply((b) -> b && currentValue).thenAccept(b -> {
                if (!b) {
                    if (locationCondition instanceof Condition condition) {
                        condition.getOtherwise().handleEffects(execution, entity, location);
                    }
                }
            });
            return completableB;
        });
    }

    @NotNull
    @Override
    public Iterator<Condition> iterator() {
        return conditions.iterator();
    }
}
