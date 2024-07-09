package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Location;

/**
 * A simple way of specifying that an effect can be used against another location. Guarantees ownership of the location on Folia servers
 */
public interface LocationEffect {

    void useEffectAgainst(Execution execution, Location location);


}
