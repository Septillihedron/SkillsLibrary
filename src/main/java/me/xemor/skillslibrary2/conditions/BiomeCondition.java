package me.xemor.skillslibrary2.conditions;

import me.xemor.configurationdata.comparison.SetData;
import me.xemor.skillslibrary2.Skill;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BiomeCondition extends Condition implements EntityCondition, TargetCondition, LocationCondition {

    private final SetData<Biome> biomes;

    public BiomeCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        biomes = new SetData<>(Biome.class, "biomes", configurationSection);
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity, Location location) {
        return SkillsLibrary.getFoliaHacks().runASAP(location, () -> biomes.inSet(getBiome(location)));
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity) {
        return isTrue(entity, entity.getLocation());
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity, Entity target) {
        return isTrue(entity, target.getLocation());
    }

    public Biome getBiome(Location location) {
        return location.getBlock().getBiome();
    }

}
