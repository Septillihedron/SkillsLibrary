package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockEntityEffect extends Effect implements TargetEffect {

    private final int duration;
    private Material blockToPlace;

    public BlockEntityEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        this.duration = (int) Math.round(configurationSection.getDouble("duration", -1) * 20);
        String blockToPlaceStr = configurationSection.getString("block", "COBWEB");
        try {
            blockToPlace = Material.valueOf(blockToPlaceStr);
        } catch (IllegalArgumentException e) {
            SkillsLibrary.getInstance().getLogger().severe("You have entered an invalid block type at " + configurationSection.getCurrentPath());
        }
    }

    @Override
    public void useEffectAgainst(Execution execution, Entity target) {
        Location location = target.getLocation();
        World world = location.getWorld();
        Block block = world.getBlockAt(location);
        Material originalType = block.getType();
        block.setType(blockToPlace, false);
        if (duration >= 0) {
            SkillsLibrary.getScheduling().entitySpecificScheduler(target).runDelayed(() -> {
                    block.setType(originalType, false);
            }, () -> {}, this.duration);
        }
    }
}
