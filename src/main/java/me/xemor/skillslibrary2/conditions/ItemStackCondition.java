package me.xemor.skillslibrary2.conditions;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.CompletableFuture;

public interface ItemStackCondition {

    CompletableFuture<Boolean> isTrue(Entity entity, ItemStack itemStack);

}
