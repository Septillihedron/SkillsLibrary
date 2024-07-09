package me.xemor.skillslibrary2.effects;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class RandomTeleportEffect extends Effect implements EntityEffect, ComplexTargetEffect {

    private final double maxDistance;
    private final double minDistance;

    public RandomTeleportEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        maxDistance = configurationSection.getDouble("maxDistance", 10);
        minDistance = configurationSection.getDouble("minDistance", 5);
    }

    @Override
    public boolean useEffect(Execution execution, Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            entity.teleport(findLocation(livingEntity.getEyeLocation()));
        }
        return false;
    }

    @Override
    public boolean useEffect(Entity livingEntity, Entity target) {
        target.teleport(findLocation(target.getLocation()));
        return false;
    }

    public Location findLocation(Location initialLocation) {
        double randomAngle = Math.random() * 2 *  Math.PI; //random angle between 0 and 2 pi radians
        double randomDistance = Math.random() * (maxDistance - minDistance) + minDistance; //random distance between minDistance and maxDistance
        Vector direction = new Vector(Math.sin(randomAngle), 0, Math.cos(randomAngle));
        World world = initialLocation.getWorld();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(initialLocation, direction, randomDistance);
        if (rayTraceResult == null) {
            return initialLocation.add(direction.multiply(randomDistance));
        }
        else {
            return rayTraceResult.getHitPosition().toLocation(world, initialLocation.getYaw(), initialLocation.getPitch()).subtract(direction);
        }
    }
}
