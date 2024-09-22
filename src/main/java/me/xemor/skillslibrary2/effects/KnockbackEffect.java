package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class KnockbackEffect extends Effect implements TargetEffect {

    private final String multiplierExpression;
    private final boolean overwriteCurrentVelocity;

    public KnockbackEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        multiplierExpression = configurationSection.getString("multiplier", "1.0");
        overwriteCurrentVelocity = configurationSection.getBoolean("overwriteCurrentVelocity", true);
    }

    @Override
    public void useEffect(Execution exe, Entity livingEntity, Entity target) {
        SkillsLibrary.getFoliaHacks().runASAP(livingEntity, () -> {
            Location location = livingEntity.getLocation();
            if (livingEntity instanceof Player player) {
                location = player.getEyeLocation();
            }
            Vector knockback = location.getDirection().clone().multiply(exe.expression(multiplierExpression));
            SkillsLibrary.getFoliaHacks().runASAP(target, () -> {
                if (overwriteCurrentVelocity) target.setVelocity(knockback);
                else target.setVelocity(target.getVelocity().add(knockback));
            });
        });
    }
}
