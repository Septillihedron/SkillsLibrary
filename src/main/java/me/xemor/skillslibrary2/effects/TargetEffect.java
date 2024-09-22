package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.entity.Entity;

public interface TargetEffect {

    void useEffect(Execution execution, Entity entity, Entity target);

}
