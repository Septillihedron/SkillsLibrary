package me.xemor.skillslibrary2.conditions;

import me.xemor.configurationdata.comparison.SetData;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.concurrent.CompletableFuture;


public class BlockCondition extends Condition implements LocationCondition {

    private SetData<Material> allowedMaterials;

    public BlockCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        allowedMaterials = new SetData<>(Material.class, "materials", configurationSection);
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Entity entity, Location location) {
        World world = location.getWorld();
        return SkillsLibrary.getFoliaHacks().runASAP(location, () -> allowedMaterials.inSet(world.getBlockAt(location).getType()));
    }

}
