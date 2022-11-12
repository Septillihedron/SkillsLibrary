package me.xemor.skillslibrary2.effects;

import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public class Effects {

    private static final HashBiMap<String, Integer> nameToEffect = HashBiMap.create();
    private static final List<Class<? extends Effect>> effectToData = new ArrayList<>();
    private static int counter = 0;

    private static final List<ExtensionEffectEntry> extensionEffects = new ArrayList<>();

    private record ExtensionEffectEntry(UnaryOperator<String> nameConverter, Class<? extends ExtensionEffect> extensionEffect) {};

    static {
        registerEffect("DAMAGE", DamageEffect.class);
        registerEffect("AOE", AOE.class);
        registerEffect("VELOCITY", VelocityEffect.class);
        registerEffect("CANCEL", CancelEffect.class);
        registerEffect("GLIDING", GlidingEffect.class);
        registerEffect("WAIT", WaitEffect.class);
        registerEffect("POTION", Potion.class);
        registerEffect("WEB", BlockEntityEffect.class);
        registerEffect("BLOCKENTITY", BlockEntityEffect.class);
        registerEffect("ARROW", ArrowEffect.class);
        registerEffect("ATTRIBUTE", AttributeEffect.class);
        registerEffect("BREAKBLOCK", BreakBlockEffect.class);
        registerEffect("LAUNCH", LaunchEffect.class);
        registerEffect("MESSAGE", MessageEffect.class);
        registerEffect("PICKUP", Pickup.class);
        registerEffect("REPULSE", Repulse.class);
        registerEffect("RESIZE", ResizeEffect.class);
        registerEffect("SMITE", LightningEffect.class);
        registerEffect("LIGHTNING", LightningEffect.class);
        registerEffect("VELOCITY", VelocityEffect.class);
        registerEffect("FLING", VelocityEffect.class);
        registerEffect("METADATA", MetadataEffect.class);
        registerEffect("GIVEITEM", GiveItemEffect.class);
        registerEffect("SCRAMBLEINVENTORY", ScrambleInventoryEffect.class);
        registerEffect("RANDOMTELEPORT", RandomTeleportEffect.class);
        registerEffect("PROJECTILE", ProjectileEffect.class);
        registerEffect("SOUND", SoundEffect.class);
        registerEffect("COMMAND", CommandEffect.class);
        registerEffect("LUNGE", LungeEffect.class);
        registerEffect("NEAREST", NearestEffect.class);
        registerEffect("SHOOTER", ShooterEffect.class);
        registerEffect("KNOCKBACK", KnockbackEffect.class);
        registerEffect("IGNITE", IgniteEffect.class);
        registerEffect("FIRE", IgniteEffect.class);
        registerEffect("LOSETARGET", LoseTargetEffect.class);
        registerEffect("TARGET", TargetEntityEffect.class);
        registerEffect("SWAP", SwapEffect.class);
        registerEffect("LOCATIONCUBE", LocationCubeEffect.class);
        registerEffect("PLACEBLOCK", PlaceBlockEffect.class);
        registerEffect("SPAWNENTITY", SpawnEffect.class);
        registerEffect("REMOVEPOTION", RemovePotionEffect.class);
        registerEffect("TELEPORT", TeleportEffect.class);
        registerEffect("RAYTRACE", RaytraceEffect.class);
        registerEffect("TIMER", TimerEffect.class);
        registerEffect("REMOVEENTITY", RemoveEntityEffect.class);
        registerEffect("ITEMSTACK", ItemStackWrapperEffect.class);
        registerEffect("ITEMAMOUNT", ItemAmountEffect.class);
        registerEffect("SWITCHEROO", SwitcherooWrapperEffect.class);
        registerEffect("LOCATIONOFFSET", LocationOffsetEffect.class);
        registerEffect("BONEMEAL", BonemealEffect.class);
        registerEffect("HUNGER", HungerEffect.class);
        registerEffect("VEINMINER",VeinMinerEffect.class);
        registerEffect("ACTIONBAR", ActionBarEffect.class);

        registerExtensionEffect(name -> name + EvaluatingEffect.SUFFIX, EvaluatingEffect.class);
    }

    public static void registerExtensionEffect(UnaryOperator<String> nameConverter, Class<? extends ExtensionEffect> extensionEffectClass) {
        extensionEffects.add(new ExtensionEffectEntry(nameConverter, extensionEffectClass));
        
        var effectNames = Set.copyOf(nameToEffect.keySet());
        for (String name : effectNames) {
            registerInternal(nameConverter.apply(name), extensionEffectClass);
        }
    }

    public static void registerEffect(String name, Class<? extends Effect> effectDataClass) {
        registerInternal(name, effectDataClass);

        for (ExtensionEffectEntry entry : extensionEffects) {
            registerInternal(entry.nameConverter.apply(name), entry.extensionEffect);
        }
    }
    
    private static void registerInternal(String name, Class<? extends Effect> effectDataClass) {
        nameToEffect.put(name, counter);
        effectToData.add(effectDataClass);
        counter++;
    }

    public static Class<? extends Effect> getClass(int effect) {
        Class<? extends Effect> effectClass = effectToData.get(effect);
        return effectClass == null ? Effect.class : effectClass;
    }

    public static int getEffect(String name) {
        return nameToEffect.getOrDefault(name, -1);
    }

    public static String getName(int effect) {
        return nameToEffect.inverse().get(effect);
    }

}
