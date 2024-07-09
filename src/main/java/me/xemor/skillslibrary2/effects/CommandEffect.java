package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class CommandEffect extends Effect implements EntityEffect, ComplexTargetEffect {

    private Executor executor;
    private List<String> commands;

    public CommandEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        String executorStr = configurationSection.getString("executor", "CONSOLE").toUpperCase();
        commands = configurationSection.getStringList("commands");
        try {
            executor = Executor.valueOf(executorStr);
        } catch (IllegalArgumentException e) {
            SkillsLibrary.getInstance().getLogger().severe("You have entered an invalid command executor! " + configurationSection.getCurrentPath() + ".executor");
        }
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        for (String command : commands) {
            String parsedCommand = parse(command, execution, entity, null);
            execute(parsedCommand, entity);
        }
    }

    @Override
    public void useEffect(Execution execution, Entity entity, Entity target) {
        for (String command : commands) {
            String parsedCommand = parse(command, execution, entity, target);
            execute(parsedCommand, entity);
        }
    }

    public void execute(String command, Entity entity) {
        if (entity instanceof Player) {
            if (executor == Executor.PLAYER) {
                Bukkit.dispatchCommand(entity, command);
                return;
            }
        }
        if (executor == Executor.CONSOLE) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public String parse(String command, Execution execution, Entity entity, Entity target) {
        var map = target == null ? Map.of("self", entity.getPersistentDataContainer()) : Map.of("self", entity.getPersistentDataContainer(), "other", target.getPersistentDataContainer());
        String parsedCommand = execution.message(command, map);
        if (entity instanceof Player player) {
            parsedCommand = parsedCommand.replace("%self_name%", player.getName());
        }
        if (target instanceof Player player) {
            parsedCommand = parsedCommand.replace("%other_name%", player.getName());
            parsedCommand = parsedCommand.replace("%target_name%", player.getName());
        }
        return parsedCommand;
    }

    /**
     * An enum representing who will execute the command.
     * TODO Implement an OP executor with minimal danger of server crashing mid-instruction.
     */
    private enum Executor {
        CONSOLE, PLAYER;
    }

}
