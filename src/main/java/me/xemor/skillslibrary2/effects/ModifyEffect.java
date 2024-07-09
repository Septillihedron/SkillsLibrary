package me.xemor.skillslibrary2.effects;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.configuration.ConfigurationSection;

public abstract class ModifyEffect extends Effect {

    private Operation operation;
    protected String valueExpr;

    public ModifyEffect(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        String operationStr = configurationSection.getString("operation", "").toUpperCase();
        try {
            this.operation = Operation.valueOf(operationStr);
        } catch(IllegalArgumentException e) {
            SkillsLibrary.getInstance().getLogger().severe(configurationSection.getCurrentPath() + ".operation has an invalid operation specified! Defaulting to SET operation!");
            this.operation = Operation.SET;
            return;
        }
        this.valueExpr = configurationSection.getString("value", "1");
    }

    public double changeValue(Execution execution, double oldValue) {
        return switch (operation) {
            case ADD -> oldValue + execution.expression(valueExpr);
            case SUBTRACT -> oldValue - execution.expression(valueExpr);
            case MULTIPLY -> oldValue * execution.expression(valueExpr);
            case DIVIDE -> oldValue / execution.expression(valueExpr);
            case SET -> execution.expression(valueExpr);
        };
    }

    public enum Operation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, SET;
    }

    public Operation getOperation() {
        return operation;
    }
}
