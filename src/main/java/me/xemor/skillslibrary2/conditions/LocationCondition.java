package me.xemor.skillslibrary2.conditions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;

public interface LocationCondition {

    CompletableFuture<Boolean> isTrue(Entity entity, Location location);

}
