package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.entity.Entity;

/**
 * A simple way of specifying that an effect can be used against another entity. Guarantees ownership of the entity on Folia servers
 */
public interface TargetEffect {

    void useEffectAgainst(Execution execution, Entity target);

}