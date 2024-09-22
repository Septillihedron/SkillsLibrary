package me.xemor.skillslibrary2.effects;

import io.papermc.lib.PaperLib;
import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

/**
 * An effect that picks up the target entity and places it on the user's head.
 */
public class Pickup extends Effect implements TargetEffect {

    public Pickup(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
    }

    @Override
    public void useEffect(Execution execution, Entity entity, Entity target) {
        SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            Location location = entity.getLocation();
            if (!entity.getPassengers().contains(target)) {
                SkillsLibrary.getFoliaHacks().runASAP(target, () -> {
                    PaperLib.teleportAsync(target, location).thenAccept(
                            (success) -> {
                                if (success) {
                                    SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
                                        entity.addPassenger(target);
                                    });
                                }
                            }
                    );
                });
            }
        });
    }
}
