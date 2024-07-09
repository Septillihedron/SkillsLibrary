package me.xemor.skillslibrary2.effects;

import me.xemor.configurationdata.SoundData;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

public class SoundEffect extends Effect implements EntityEffect, ComplexTargetEffect, ComplexLocationEffect {
    private final SoundData soundData;

    public SoundEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        ConfigurationSection soundSection = configurationSection.getConfigurationSection("sound");
        soundData = new SoundData(soundSection);
    }

    @Override
    public boolean useEffect(Execution execution, Entity entity) {
        playSound(entity.getLocation());
        return false;
    }

    @Override
    public boolean useEffect(Entity livingEntity, Entity target) {
        playSound(target.getLocation());
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Location location) {
        playSound(location);
        return false;
    }

    private void playSound(Location location) {
        location.getWorld().playSound(location, soundData.getSound(), soundData.getVolume(), soundData.getPitch());
    }
}
