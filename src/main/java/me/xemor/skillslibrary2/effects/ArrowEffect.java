package me.xemor.skillslibrary2.effects;

import me.xemor.configurationdata.entity.EntityData;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

public class ArrowEffect extends Effect implements TargetEffect {

    private final String velocityExpression;
    private final String damageExpression;
    private final EntityData entityData;
    private final String fireTicksExpression;
    private static final double log099 = Math.log(0.99);

    public ArrowEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        velocityExpression = configurationSection.getString("velocity", "1.0");
        damageExpression = configurationSection.getString("damage", "4");
        entityData = EntityData.create(configurationSection, "entity", EntityType.ARROW);
        fireTicksExpression = configurationSection.getString("fireTicks", "0");
    }

    @Override
    public void useEffect(Execution execution, Entity entity, Entity target) {
        Location location = target.getLocation();
        if (entity instanceof LivingEntity livingEntity) {
            Location startPoint = livingEntity.getEyeLocation();
            double yDifference = location.getY() - startPoint.getY();
            double xDifference = location.getX() - startPoint.getX();
            double zDifference = location.getZ() - startPoint.getZ();
            double length = Math.sqrt(yDifference * yDifference + xDifference * xDifference + zDifference * zDifference);
            if (length < 0.1) return;
            int time = (int) Math.round(length / execution.expression(velocityExpression));
            double initialYVelocity = solveForInitialVerticalVelocity(yDifference, time);
            double initialXVelocity = solveForInitialHorizontalVelocity(xDifference, time);
            double initialZVelocity = solveForInitialHorizontalVelocity(zDifference, time);
            Vector vector = new Vector(initialXVelocity, initialYVelocity, initialZVelocity);
            Entity spawnedEntity = entityData.spawnEntity(startPoint);
            if (spawnedEntity instanceof Arrow arrow) {
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                arrow.setDamage(execution.expression(damageExpression));
            }
            if (spawnedEntity instanceof Projectile projectile) {
                projectile.setShooter(livingEntity);
            }
            spawnedEntity.setFireTicks(execution.expression(fireTicksExpression));
            startPoint = startPoint.add(vector.clone().normalize());
            spawnedEntity.teleport(startPoint);
            spawnedEntity.setVelocity(vector);
        }
    }

    private double solveForInitialVerticalVelocity(double displacement, int time) {
        double zero99ToT = Math.pow(0.99, time);
        double numerator = 5 * zero99ToT - log099 * displacement - 5 * log099 * time - 5;
        double denominator = 1 - zero99ToT;
        return numerator / denominator;
    }


    private double solveForInitialHorizontalVelocity(double displacement, int time) {
        double zero99ToT = Math.pow(0.99, time);
        double numerator = -log099 * displacement;
        double denominator = 1 - zero99ToT;
        return numerator / denominator;
    }


    private double calculateHorizontalVelocity(int ticksInFuture, double velocity) {
        return velocity * Math.pow(0.99, ticksInFuture);
    }

}
