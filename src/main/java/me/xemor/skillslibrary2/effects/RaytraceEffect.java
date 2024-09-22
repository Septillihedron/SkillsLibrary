package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class RaytraceEffect extends WrapperEffect implements EntityEffect, TargetEffect {

    private double maxDistance;
    private FluidCollisionMode collisionMode;
    private boolean ignorePassables;
    private boolean alwaysHit;
    private double raySize;

    public RaytraceEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        maxDistance = configurationSection.getDouble("maxDistance", 10);
        alwaysHit = configurationSection.getBoolean("alwaysHit", true);
        ignorePassables = configurationSection.getBoolean("ignorePassables", true);
        raySize = configurationSection.getDouble("raySize", 1.0);
        String collisionModeStr = configurationSection.getString("collisionMode", "NEVER");
        try {
            collisionMode = FluidCollisionMode.valueOf(collisionModeStr);
        } catch (IllegalArgumentException e) {
            SkillsLibrary.getInstance().getLogger().severe("You have entered an invalid collsion mode! " + configurationSection.getCurrentPath() + ".collisionMode");
        }
    }

    @Override
    public boolean useEffect(Execution execution, Entity entity) {
        World world = entity.getWorld();
        Location location;
        if (entity instanceof Player) {
            location = ((Player) entity).getEyeLocation();
        }
        else {
            location = entity.getLocation();
        }
        Vector direction = location.getDirection();
        RayTraceResult rayTraceResult = world.rayTrace(location,
                location.getDirection(),
                maxDistance,
                FluidCollisionMode.NEVER,
                ignorePassables,
                raySize,
                (other) -> other != entity);
        Object result;
        if (rayTraceResult == null && alwaysHit) {
            result = location.add(direction.multiply(maxDistance));
        } else if (rayTraceResult == null) {
            return false;
        } else if (rayTraceResult.getHitEntity() != null) {
            result = rayTraceResult.getHitEntity();
        } else {
            Vector position = rayTraceResult.getHitPosition();
            result = new Location(world, position.getX(), position.getY(), position.getZ(), location.getYaw(), location.getPitch());
        }
        handleEffects(entity, result);
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        useEffect(target);
        return false;
    }

}
