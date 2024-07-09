package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class ItemAmountEffect extends ModifyEffect implements ItemStackEffect {

    public ItemAmountEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
    }

    @Override
    public void useEffect(Execution execution, Entity entity, ItemStack item) {
        SkillsLibrary.getFoliaHacks().runASAP(entity, () -> {
            item.setAmount((int) Math.round(changeValue(item.getAmount())));
        });
    }

}
