package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


/*
    This class was originally written by Creeves
 */
public class TimerEffect extends WrapperEffect implements EntityEffect, ComplexTargetEffect, ComplexLocationEffect, ItemStackEffect {

    private final long ticksDelay;
    private final long period;
    private final int repeats;

    public TimerEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        ticksDelay = Math.round(configurationSection.getDouble("delay", 1) * 20);
        period = Math.round(configurationSection.getDouble("period", 1) * 20);
        repeats = configurationSection.getInt("numberOfRepeats", 1);
    }

    @Override
    public boolean useEffect(Execution execution, Entity entity) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                handleEffects(entity);
                if (++count >= repeats) {
                    this.cancel();
                }
            }
        }.runTaskTimer(SkillsLibrary.getInstance(), ticksDelay, period);
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                handleEffects(entity, target);
                if (++count >= repeats) {
                    this.cancel();
                }
            }
        }.runTaskTimer(SkillsLibrary.getInstance(), ticksDelay, period);
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Location location) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                handleEffects(entity, location);
                if (++count >= repeats) {
                    this.cancel();
                }
            }
        }.runTaskTimer(SkillsLibrary.getInstance(), ticksDelay, period);
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, ItemStack item) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                handleEffects(entity, item);
                if (++count >= repeats) {
                    this.cancel();
                }
            }
        }.runTaskTimer(SkillsLibrary.getInstance(), ticksDelay, period);
        return false;
    }
}