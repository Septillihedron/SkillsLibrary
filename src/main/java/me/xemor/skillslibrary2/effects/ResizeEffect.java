package me.xemor.skillslibrary2.effects;

import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Slime;

public class ResizeEffect extends ModifyEffect implements EntityEffect, TargetEffect {

    /**
     * Slime#setSize is a daft method. It sets the maximum health of the slime to n^2, movement speed to 0.1n + 0.2 and attack damage to n.
     * If the slime is alive, then the health is also set to the maximum health, which is often unintended behaviour in a boss fight.
     * Hence, the retainHealth attribute setting it back.
     */
    private boolean retainHealth;

    public ResizeEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        this.retainHealth = configurationSection.getBoolean("retainHealth", true);
    }

    @Override
    public boolean useEffect(Entity boss) {
        if (boss instanceof Slime) {
            Slime slime = (Slime) boss;
            double health = slime.getHealth();
            double maxHealth = slime.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            slime.setSize((int) Math.round(changeValue(slime.getSize())));
            if (retainHealth) {
                slime.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
                if (health > maxHealth) {
                    health = maxHealth;
                }
                slime.setHealth(health);
            }
        }
        else if (boss instanceof Phantom) {
            Phantom phantom = (Phantom) boss;
            phantom.setSize((int) Math.round(changeValue(phantom.getSize())));
        }
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        if (target instanceof Slime) {
            Slime slime = (Slime) target;
            slime.setSize((int) Math.round(changeValue(slime.getSize())));
        }
        else if (target instanceof Phantom) {
            Phantom phantom = (Phantom) target;
            phantom.setSize((int) Math.round(changeValue(phantom.getSize())));
        }
        return false;
    }

}
