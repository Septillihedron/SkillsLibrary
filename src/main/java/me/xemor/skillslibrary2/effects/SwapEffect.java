package me.xemor.skillslibrary2.effects;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

public class SwapEffect extends Effect implements TargetEffect {

    public SwapEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        Location location1 = entity.getLocation();
        entity.teleport(target);
        target.teleport(location1);
        return false;
    }
}
