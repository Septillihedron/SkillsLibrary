package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class WaitEffect extends WrapperEffect implements EntityEffect, TargetEffect, ComplexLocationEffect, ItemStackEffect {

    private final long ticksDelay;

    public WaitEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        ticksDelay = Math.round(configurationSection.getDouble("delay", 1) * 20);
    }

    @Override
    public boolean useEffect(Execution execution, Entity entity) {
        new BukkitRunnable() {
            @Override
            public void run() {
                handleEffects(entity);
            }
        }.runTaskLater(SkillsLibrary.getInstance(), ticksDelay);
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                handleEffects(entity, target);
            }
        }.runTaskLater(SkillsLibrary.getInstance(), ticksDelay);
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Location location) {
        new BukkitRunnable() {
            @Override
            public void run() {
                handleEffects(entity, location);
            }
        }.runTaskLater(SkillsLibrary.getInstance(), ticksDelay);
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, ItemStack item) {
        new BukkitRunnable() {
            @Override
            public void run() {
                handleEffects(entity, item);
            }
        }.runTaskLater(SkillsLibrary.getInstance(), ticksDelay);
        return false;
    }
}
