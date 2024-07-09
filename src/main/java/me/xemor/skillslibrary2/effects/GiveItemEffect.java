package me.xemor.skillslibrary2.effects;

import me.xemor.configurationdata.ItemStackData;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GiveItemEffect extends Effect implements EntityEffect, TargetEffect {

    private final ItemStack item;
    private final boolean dropIfFull;

    public GiveItemEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        ConfigurationSection item = configurationSection.getConfigurationSection("item");
        this.item = new ItemStackData(item).getItem();
        dropIfFull = configurationSection.getBoolean("dropIfFull", true);
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        giveItem(entity);
    }

    @Override
    public void useEffectAgainst(Execution execution, Entity target) {
        giveItem(target);
    }

    public void giveItem(Entity entity) {
        if (entity instanceof InventoryHolder inventoryHolder) {
            Inventory inventory = inventoryHolder.getInventory();
            HashMap<Integer, ItemStack> leftovers = inventory.addItem(item);
            if (dropIfFull) {
                World world = entity.getWorld();
                for (ItemStack item : leftovers.values()) {
                    world.dropItem(entity.getLocation(), item);
                }
            }
        }
    }
}
