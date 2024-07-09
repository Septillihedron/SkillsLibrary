package me.xemor.skillslibrary2.conditions;

import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;

public interface TargetCondition {

    CompletableFuture<Boolean> isTrue(Entity entity, Entity target);

}
