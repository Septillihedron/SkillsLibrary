package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.Mode;
import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.security.Signature;
import java.util.Map;

public class LungeEffect extends Effect implements EntityEffect, TargetEffect {

    private final String horizontalVelocityExpr;
    private final String verticalVelocityExpr;
    private final boolean overwrite;

    public LungeEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        horizontalVelocityExpr = configurationSection.getString("horizontalVelocity");
        verticalVelocityExpr = configurationSection.getString("verticalVelocity");
        overwrite = configurationSection.getBoolean("overwrite", true);
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            SkillsLibrary.getFoliaHacks().runASAP(livingEntity, () -> {
                setVelocity(execution, livingEntity);
            });
        }
    }

    @Override
    public void useEffectAgainst(Execution execution, Entity target) {
        if (target instanceof LivingEntity livingTarget) {
            SkillsLibrary.getFoliaHacks().runASAP(livingTarget, () -> {
                setVelocity(execution, livingTarget);
            });
        }
    }

    public void setVelocity(Execution execution, LivingEntity entity) {
        Vector direction;
        if (entity instanceof Player)
            direction = entity.getEyeLocation().getDirection();
        else {
            direction = entity.getLocation().getDirection();
        }
        String entityMode = getMode() == Mode.SELF ? "self" : "other";
        double horizontalVelocity = execution.expression(horizontalVelocityExpr, Map.of(entityMode, entity.getPersistentDataContainer()));
        double velocityVelocity = execution.expression(verticalVelocityExpr, Map.of(entityMode, entity.getPersistentDataContainer()));
        double x = direction.getX() * horizontalVelocity;
        double z = direction.getZ() * horizontalVelocity;
        double y = direction.getY() * velocityVelocity;
        if (overwrite) entity.setVelocity(new Vector(x, y, z));
        else entity.setVelocity(entity.getVelocity().add(new Vector(x, y, z)));
    }
}
