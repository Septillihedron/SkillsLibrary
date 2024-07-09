package me.xemor.skillslibrary2.effects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class RemovePotionEffect extends Effect implements EntityEffect, ComplexTargetEffect {

    private Set<PotionEffectType> types;

    public RemovePotionEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        types = configurationSection.getStringList("types").stream().map(String::toUpperCase).map(name -> PotionEffectType.getByName(name)).collect(Collectors.toSet());
    }

    public boolean inSet(PotionEffectType type) {
        return types.size() == 0 || types.contains(type);
    }

    public void removeEffects(Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            Collection<PotionEffect> activePotionEffects = livingEntity.getActivePotionEffects();
            for (PotionEffect effect : activePotionEffects) {
                if (inSet(effect.getType())) {
                    livingEntity.removePotionEffect(effect.getType());
                }
            }

        }
    }

    @Override
    public boolean useEffect(Execution execution, Entity entity) {
        removeEffects(entity);
        return false;
    }

    @Override
    public boolean useEffect(Entity entity, Entity target) {
        removeEffects(target);
        return false;
    }

}
