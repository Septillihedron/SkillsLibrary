package me.xemor.skillslibrary2.effects;

import me.xemor.configurationdata.comparison.SetData;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.EnumSet;

public class VeinMinerEffect extends Effect implements ComplexLocationEffect {

    private final SetData<Material> materials;
    private final long delay;
    private final int limit; // The maximum number of iterations outwards from the centre NOT the number of blocks broken
    private final boolean allowMultiTypeVein;

    private final static EnumSet<BlockFace> faces = EnumSet.complementOf(
            EnumSet.of(BlockFace.EAST_NORTH_EAST, BlockFace.EAST_SOUTH_EAST,
            BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_NORTH_WEST,
            BlockFace.SOUTH_SOUTH_EAST, BlockFace.SOUTH_SOUTH_WEST,
            BlockFace.WEST_NORTH_WEST, BlockFace.WEST_SOUTH_WEST));

    public VeinMinerEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        materials = new SetData<>(Material.class, "types", configurationSection);
        if (materials.getSet().isEmpty()){
            throw new IllegalStateException("Materials property has not been specified.");
        }
        delay = Math.round(20 * configurationSection.getDouble("delay", 0.05D));
        allowMultiTypeVein = configurationSection.getBoolean("allowMultiTypeVein",false);
        limit = configurationSection.getInt("limit", 10);
    }

    @Override
    public boolean useEffect(Entity entity, Location location) {
        breakLog(location.getBlock(), 0);
        return false;
    }

    private void breakLog(Block block, int count) {
        Material currentType = block.getType();
        if (count >= limit) return;
        if (materials.inSet(currentType)) {
            SkillsLibrary.getScheduling().regionSpecificScheduler(block.getLocation()).runDelayed(
                    () -> {
                        for (BlockFace face : faces) {
                            Block blockToBreak = block.getRelative(face);
                            if (allowMultiTypeVein || blockToBreak.getType() == currentType) {
                                breakLog(blockToBreak, count + 1);
                            }
                        }
                        block.breakNaturally();
                    }, delay
            );
        }
    }
}
