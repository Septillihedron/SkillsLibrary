package me.xemor.skillslibrary2.effects;

import me.xemor.configurationdata.BlockDataData;
import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.conditions.ConditionList;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaceBlockEffect extends Effect implements ComplexLocationEffect {

    private BlockData blockData;
    private final boolean updatePhysics;
    private final boolean isPacket;
    private final ConditionList revertConditions;
    private final String revertsAfterExpr;

    public PlaceBlockEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        String typeStr = configurationSection.getString("material");
        if (typeStr == null) {
            BlockDataData blockDataData = new BlockDataData(configurationSection.getConfigurationSection("block"));
            blockData = blockDataData.getBlockData();
        }
        else {
            try {
                blockData = Bukkit.createBlockData(Material.valueOf(typeStr));
            } catch (IllegalArgumentException e) {
                SkillsLibrary.getInstance().getLogger().severe("You have entered an invalid material! " + configurationSection.getCurrentPath() + ".material");
            }
        }
        updatePhysics = configurationSection.getBoolean("updatePhysics", true);
        isPacket = configurationSection.getBoolean("isPacket", false);
        revertConditions = new ConditionList(configurationSection.getConfigurationSection("revertConditions"));
        revertsAfterExpr = configurationSection.getString("revertsAfter", "-1");
    }

    private int getRevertsAfter(Execution execution, Entity entity) {
        return (int) Math.round(execution.expression(revertsAfterExpr, entity) * 20);
    }

    @Override
    public void useEffect(Execution execution, Entity entity, Location location) {
        SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            int revertsAfter = getRevertsAfter(execution, entity);
            SkillsLibrary.getFoliaHacks().runASAP(location, () -> {
                if (isPacket) {
                    handlePacketPlaceBlock(execution, entity, location, revertsAfter);
                }
                else {
                    handlePlaceBlock(execution, entity, location, revertsAfter);
                }
            });
        });
    }

    public void handlePacketPlaceBlock(Execution execution, Entity entity, Location location, int revertsAfter) {
        if (entity instanceof Player player) {
            BlockData currentData = location.getBlock().getBlockData();
            SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
                player.sendBlockChange(location, blockData);
            });
            if (revertsAfter > 0) {
                SkillsLibrary.getScheduling().regionSpecificScheduler(location).runAtFixedRate((task) -> {
                    boolean conditionsPassed = revertConditions.ANDConditions(execution, entity, false, location);
                    if (location.getBlock().getBlockData().matches(currentData) && conditionsPassed) {
                        SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
                            player.sendBlockChange(location, location.getBlock().getBlockData());
                        });
                    }
                    if (conditionsPassed) {
                        task.cancel();
                    }
                }, revertsAfter, revertsAfter);
            }
        }
    }

    public void handlePlaceBlock(Execution execution, Entity entity, Location location, int revertsAfter) {
        Block block = location.getBlock();
        BlockData oldData = block.getBlockData();
        block.setBlockData(blockData, updatePhysics);
        if (revertsAfter > 0) {
            SkillsLibrary.getScheduling().regionSpecificScheduler(location).runAtFixedRate((task) -> {
                boolean conditionsPassed = revertConditions.ANDConditions(execution, entity, false, location);
                if (block.getBlockData().matches(blockData) && conditionsPassed) {
                    block.setBlockData(oldData, updatePhysics);
                }
                if (conditionsPassed) {
                    task.cancel();
                }
            }, revertsAfter, revertsAfter);
        }
    }
}
