package me.xemor.skillslibrary2.conditions;

import me.xemor.configurationdata.comparison.SetData;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;

public class BiomeCondition extends Condition implements EntityCondition, LocationCondition {

    private final SetData<Biome> biomes;

    public BiomeCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        biomes = new SetData<>(Biome.class, "biomes", configurationSection);
    }

    private boolean calculate(Entity entity, Location location) {
        return biomes.inSet(getBiome(location));
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity, Location location) {
        return SkillsLibrary.getFoliaHacks().runASAP(location, () -> calculate(entity, location));
    }

    @Override
    public boolean isTrue(Entity entity) {
        return calculate(entity, entity.getLocation());
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity, Entity target) {
        return isTrue(entity, target.getLocation());
    }

    public Biome getBiome(Location location) {
        return location.getBlock().getBiome();
    }

}
