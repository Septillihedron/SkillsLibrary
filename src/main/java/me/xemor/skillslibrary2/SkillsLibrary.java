package me.xemor.skillslibrary2;

import me.xemor.foliahacks.FoliaHacks;
import me.xemor.skillslibrary2.triggers.Triggers;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

public final class SkillsLibrary extends JavaPlugin {

    private static SkillsLibrary skillsLibrary;
    private static BukkitAudiences bukkitAudiences;
    private static SkillsManager skillsManager;
    private static FoliaHacks foliaHacks;

    @Override
    public void onEnable() {
        skillsLibrary = this;
        bukkitAudiences = BukkitAudiences.create(this);
        skillsManager = new SkillsManager();
        foliaHacks = new FoliaHacks(this);
        this.getServer().getPluginManager().registerEvents(new Triggers(), this);
    }

    public static FoliaHacks getFoliaHacks() {
        return foliaHacks;
    }

    public static GracefulScheduling getScheduling() {
        return foliaHacks.getScheduling();
    }

    public static SkillsLibrary getInstance() {
        return skillsLibrary;
    }

    public static BukkitAudiences getBukkitAudiences() {
        return bukkitAudiences;
    }

    public static SkillsManager getSkillsManager() {
        return skillsManager;
    }

}
