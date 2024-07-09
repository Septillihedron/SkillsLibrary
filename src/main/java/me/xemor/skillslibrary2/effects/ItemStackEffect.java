package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public interface ItemStackEffect {

    void useEffect(Execution execution, Entity entity, ItemStack item);

}
