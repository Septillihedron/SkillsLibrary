package me.xemor.skillslibrary2.conditions;

import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ItemWrapperCondition extends WrapperCondition implements EntityCondition, TargetCondition {

    private EquipmentSlot equipmentSlot;
    private int slot = -1;

    public ItemWrapperCondition(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        String equipmentSlotStr = configurationSection.getString("slot", "HAND").toUpperCase();
        try {
            equipmentSlot = EquipmentSlot.valueOf(equipmentSlotStr);
        } catch (IllegalArgumentException e) {
            try {
                slot = Integer.parseInt(equipmentSlotStr);
            } catch (NumberFormatException ignored) {
                SkillsLibrary.getInstance().getLogger().severe("You have entered an invalid equipment slot! " + configurationSection.getCurrentPath() + ".slot");
            }
        }
    }

    @Override
    public boolean isTrue(Entity entity) {
        if (equipmentSlot != null) {
            if (entity instanceof LivingEntity livingEntity) {
                EntityEquipment entityEquipment = livingEntity.getEquipment();
                if (entityEquipment == null) return false;
                ItemStack item = entityEquipment.getItem(equipmentSlot);
                return handleConditions(entity, item);
            }
        }
        else if (slot != -1) {
            if (entity instanceof Player player) {
                return handleConditions(entity, player.getInventory().getItem(slot));
            }
        }
        return false;
    }

    @Override
    public boolean isTrue(Entity entity, Entity target) {
        return isTrue(target);
    }
}
