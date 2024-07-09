package me.xemor.skillslibrary2.triggers;

import com.google.common.collect.Iterators;
import me.xemor.skillslibrary2.Skill;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Triggers implements Listener {

    private final Set<Material> armour = EnumSet.of(Material.TURTLE_HELMET,
            Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS,
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
            Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);

    public Triggers() {
        AtomicInteger tick = new AtomicInteger();

        SkillsLibrary.getScheduling().globalRegionalScheduler().runAtFixedRate(
                () -> {
                    Collection<Skill> skillDatas = SkillsLibrary.getSkillsManager().getSkills(Trigger.getTrigger("LOOP"));
                    for (Skill skill : skillDatas) {
                        LoopData loopData = (LoopData) skill.getTriggerData();
                        if (tick.get() % loopData.getPeriod() == 0) {
                            for (UUID uuid : SkillsLibrary.getSkillsManager().getLoopEntities()) {
                                Entity entity = Bukkit.getEntity(uuid);
                                SkillsLibrary.getScheduling().entitySpecificScheduler(entity).run(() -> {
                                    if (entity instanceof LivingEntity livingEntity) {
                                        skill.handleEffects(livingEntity);
                                    }
                                }, () -> {});
                            }
                        }
                    }
                    tick.incrementAndGet();
                }, 1L, 1L
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent e) {
        SkillsLibrary.getSkillsManager().addLoopEntity(e.getPlayer().getUniqueId());
        handleSkills(Trigger.getTrigger("PLAYERJOIN"), e.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        SkillsLibrary.getSkillsManager().removeLoopEntity(e.getPlayer().getUniqueId());
        handleSkills(Trigger.getTrigger("PLAYERQUIT"), e.getPlayer());
    }

    @EventHandler
    public void onPotionEffect(EntityPotionEffectEvent e) {
        Entity entity = e.getEntity();
        Collection<Skill> skills = SkillsLibrary.getSkillsManager().getSkills(Trigger.getTrigger("POTIONEFFECT"));
        boolean cancel = false;
        for (Skill skill : skills) {
            TriggerData triggerData = skill.getTriggerData();
            if (triggerData instanceof PotionEffectTriggerData potionEffectTriggerData) {
                if (potionEffectTriggerData.actionInSet(e.getAction()) && potionEffectTriggerData.causeInSet(e.getCause())) {
                    if (potionEffectTriggerData.potionInSet(e.getModifiedType())) cancel |= skill.handleEffects(entity);
                }
            }
        }
        e.setCancelled(cancel);
    }

    @EventHandler
    public void onSwitchHand(PlayerSwapHandItemsEvent e) {
        handleSkills(Trigger.getTrigger("SWAPHANDS"), e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Collection<Skill> skills = SkillsLibrary.getSkillsManager().getSkills(Trigger.getTrigger("INTERACT"));
        boolean cancel = false;
        for (Skill skill : skills) {
            InteractData interactData = (InteractData) skill.getTriggerData();
            if (interactData.hasAction(e.getAction())) {
                boolean cancelled = skill.handleEffects(player);
                if (cancelled) cancel = true;
            }
        }
        if (cancel) e.setCancelled(cancel);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof LivingEntity) {
            handleSkills(Trigger.getTrigger("INTERACTENTITY"), e.getPlayer(), e.getRightClicked());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onJump(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        double difference = e.getTo().clone().subtract(e.getFrom()).getY();
        if (difference >= 0.33319999363422426 && difference <= 0.3332) { //the second condition must be there to support geyser bedrock players
            handleSkills(Trigger.getTrigger("PLAYERJUMP"), e.getPlayer());
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getTo() == null) return;
        handleSkills(Trigger.getTrigger("MOVE"), e.getPlayer(), e.getTo());
    }

    @EventHandler(ignoreCancelled = true)
    public void onGlide(EntityToggleGlideEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) e.getEntity();
            boolean result = handleSkills(Trigger.getTrigger("TOGGLEGLIDE"), livingEntity);
            if (result) e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) e.getDamager();
            boolean cancel = handleSkills(Trigger.getTrigger("DAMAGEDENTITY"), livingEntity, e.getEntity());
            cancel |= handleSkills(Trigger.getTrigger("COMBAT"), livingEntity, e.getEntity());
            if (cancel) e.setCancelled(true);
        }
        if (e.getEntity() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) e.getEntity();
            boolean cancel = handleSkills(Trigger.getTrigger("DAMAGEDBYENTITY"), livingEntity, e.getDamager());
            cancel |= handleSkills(Trigger.getTrigger("COMBAT"), livingEntity, e.getDamager());
            if (cancel) e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void damagedByProjectile(EntityDamageByEntityEvent e) {
        Collection<Skill> damagedByProjectileSkills = SkillsLibrary.getSkillsManager().getSkills(Trigger.getTrigger("DAMAGEDBYPROJECTILE"));
        Collection<Skill> projectileCombatSkills = SkillsLibrary.getSkillsManager().getSkills(Trigger.getTrigger("PROJECTILECOMBAT"));
        Iterator<Skill> skills = Iterators.concat(damagedByProjectileSkills.iterator(), projectileCombatSkills.iterator());
        boolean cancel = false;
        while (skills.hasNext()) {
            Skill skill = skills.next();
            ProjectileData projectileData = (ProjectileData) skill.getTriggerData();
            Entity damager = null;
            if (e.getDamager() instanceof Projectile) {
                ProjectileSource source = ((Projectile) e.getDamager()).getShooter();
                if (source instanceof Entity) {
                    damager = (Entity) source;
                }
            }
            else if (!projectileData.onlyProjectiles()) {
                damager = e.getDamager();
            }
            if (damager == null) continue;
            cancel |= skill.handleEffects(e.getEntity(), damager);
        }
        e.setCancelled(cancel);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void damagedWithProjectile(EntityDamageByEntityEvent e) {
        Collection<Skill> damagedByProjectileSkills = SkillsLibrary.getSkillsManager().getSkills(Trigger.getTrigger("DAMAGEDENTITYWITHPROJECTILE"));
        Collection<Skill> projectileCombatSkills = SkillsLibrary.getSkillsManager().getSkills(Trigger.getTrigger("PROJECTILECOMBAT"));
        Iterator<Skill> skills = Iterators.concat(damagedByProjectileSkills.iterator(), projectileCombatSkills.iterator());
        boolean cancel = false;
        while (skills.hasNext()) {
            Skill skill = skills.next();
            ProjectileData projectileData = (ProjectileData) skill.getTriggerData();
            Entity damager = null;
            if (e.getDamager() instanceof Projectile) {
                ProjectileSource source = ((Projectile) e.getDamager()).getShooter();
                if (source instanceof Entity) {
                    damager = (Entity) source;
                }
            }
            else if (!projectileData.onlyProjectiles()) {
                damager = e.getDamager();
            }
            if (damager == null) continue;
            cancel |= skill.handleEffects(damager, e.getEntity());
        }
        e.setCancelled(cancel);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        boolean cancel = false;
        if (e.getEntity() instanceof LivingEntity livingEntity) {
            Collection<Skill> skills = SkillsLibrary.getSkillsManager().getSkills(Trigger.getTrigger("DAMAGED"));
            for (Skill skill : skills) {
                DamageData damageData = (DamageData) skill.getTriggerData();
                if (damageData.getDamageCauses().inSet(e.getCause())) {
                    if (skill.handleEffects(livingEntity)) cancel = true;
                }
            }
        }
        e.setCancelled(cancel);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.isSneaking()) {
            Player player = e.getPlayer();
            handleSkills(Trigger.getTrigger("SNEAK"), player);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTarget(EntityTargetLivingEntityEvent e) {
        if (e.getTarget() != null) {
            e.setCancelled(handleSkills(Trigger.getTrigger("TARGET"), e.getEntity(), e.getTarget()) || handleSkills(Trigger.getTrigger("TARGETED"), e.getTarget(), e.getEntity()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleEnter(VehicleEnterEvent e) {
        if (e.getEntered() instanceof LivingEntity) {
            boolean cancel = handleSkills(Trigger.getTrigger("ENTERVEHICLE"), e.getEntered(), e.getVehicle());
            cancel |= handleSkills(Trigger.getTrigger("VEHICLE"), e.getEntered(), e.getVehicle());
            cancel |= handleSkills(Trigger.getTrigger("BECOMEVEHICLE"), e.getVehicle(), e.getEntered());
            e.setCancelled(cancel);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleExit(VehicleExitEvent e) {
        e.setCancelled(handleSkills(Trigger.getTrigger("EXITVEHICLE"), e.getExited(), e.getVehicle()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        Projectile projectile = e.getEntity();
        if (projectile.getShooter() instanceof LivingEntity) {
            LivingEntity shooter = (LivingEntity) projectile.getShooter();
            e.setCancelled(handleSkills(Trigger.getTrigger("LAUNCHPROJECTILE"), shooter, projectile));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent e) {
        Projectile projectile = e.getEntity();
        if (projectile.getShooter() instanceof LivingEntity) {
            LivingEntity shooter = (LivingEntity) projectile.getShooter();
            e.setCancelled(handleSkills(Trigger.getTrigger("PROJECTILEHIT"), shooter, projectile));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        handleSkills(Trigger.getTrigger("DEATH"), e.getEntity(), e.getEntity().getKiller());
        handleSkills(Trigger.getTrigger("KILL"), e.getEntity().getKiller(), e.getEntity());
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        e.setCancelled(handleSkills(Trigger.getTrigger("CONSUME"), e.getPlayer()));
    }

    @EventHandler
    public void onTotem(EntityResurrectEvent e) {
        if (e.isCancelled()) return;
        e.setCancelled(handleSkills(Trigger.getTrigger("TOTEM"), e.getEntity(), e.getEntity().getKiller()));
    }


    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        Entity entity = e.getEntity();
        e.setCancelled(handleSkills(Trigger.getTrigger("SPAWN"), entity));
    }

    @EventHandler
    public void onPlayerSpawn(PlayerJoinEvent e) {
        handleSkills(Trigger.getTrigger("SPAWN"), e.getPlayer());
    }

    @EventHandler
    public void changeMainHand(PlayerItemHeldEvent e) {
        handleSkills(Trigger.getTrigger("CHANGEMAINHAND"), e.getPlayer(), e.getPlayer().getInventory().getItem(e.getNewSlot()));
    }

    @EventHandler
    public void equip(InventoryClickEvent e) {
        if (e.getClick() == ClickType.LEFT || e.getClick() == ClickType.RIGHT) {
            if (e.getSlotType() == InventoryType.SlotType.ARMOR) {
                e.setCancelled(handleSkills(Trigger.getTrigger("EQUIPARMOR"), e.getWhoClicked(), e.getCurrentItem()));
            }
        }
    }

    @EventHandler
    public void equip(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() == null) return;
            if (armour.contains(e.getItem().getType())) {
                e.setCancelled(handleSkills(Trigger.getTrigger("EQUIPARMOR"), e.getPlayer(), e.getItem()));
            }
        }
    }

    @EventHandler
    public void onSprint(PlayerToggleSprintEvent e) {
        if (e.isSprinting()) handleSkills(Trigger.getTrigger("SPRINT"), e.getPlayer());
    }

    @EventHandler
    public void riptide(PlayerRiptideEvent e) {
        Entity entity = e.getPlayer();
        handleSkills(Trigger.getTrigger("RIPTIDE"), entity);
    }

    @EventHandler
    public void onTame(EntityTameEvent e) {
        if (e.getOwner() instanceof Entity) {
            handleSkills(Trigger.getTrigger("TAME"), (Entity) e.getOwner(), e.getEntity());
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        Entity entity = e.getPlayer();
        Block block = e.getBlock();
        handleSkills(Trigger.getTrigger("BLOCKBREAK"), entity, block.getLocation());
    }

    public boolean handleSkills(int trigger, @Nullable Entity entity, Object... objects) {
        Collection<Skill> skills = SkillsLibrary.getSkillsManager().getSkills(trigger);
        boolean cancel = false;
        for (Skill skill : skills) {
            cancel |= skill.handleEffects(entity, objects);
        }
        return cancel;
    }


}
