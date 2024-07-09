package me.xemor.skillslibrary2.effects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public class ScrambleInventoryEffect extends Effect implements EntityEffect, ComplexTargetEffect {
    public ScrambleInventoryEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
    }

    @Override
    public boolean useEffect(Execution execution, Entity entity) {
        scrambleInventory(entity);
        return false;
    }

    @Override
    public boolean useEffect(Entity livingEntity, Entity target) {
        scrambleInventory(target);
        return false;
    }

    public void scrambleInventory(Entity entity) {
        if (entity instanceof InventoryHolder inventoryHolder) {
            Inventory inventory = inventoryHolder.getInventory();
            ItemStack[] contents = inventory.getStorageContents();
            Collections.shuffle(Arrays.asList(contents));
            inventory.setStorageContents(contents);
        }
    }
}
