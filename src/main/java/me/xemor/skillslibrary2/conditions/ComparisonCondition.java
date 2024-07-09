package me.xemor.skillslibrary2.conditions;

import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.configuration.ConfigurationSection;

public abstract class ComparisonCondition extends Condition {

    private Comparison comparison;
    private final String comparedValueExpression;

    public ComparisonCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        String comparisonStr = configurationSection.getString("comparison", "EQUAL");
        try {
            comparison = Comparison.fromString(comparisonStr);
        } catch (IllegalArgumentException e) {
            SkillsLibrary.getInstance().getLogger().severe("This is not a valid comparison type! " + comparisonStr + " This was found at " + configurationSection.getCurrentPath() + ".comparison");
            e.printStackTrace();
        }
        comparedValueExpression = configurationSection.getString("value");
    }

    public boolean checkComparison(Execution execution, double value) {
        double comparedValue = execution.expression(comparedValueExpression);
        return switch (comparison) {
            case EQUAL -> value == comparedValue;
            case LESS -> value < comparedValue;
            case GREATER -> value > comparedValue;
            case LESSEQUAL -> value <= comparedValue;
            case GREATEREQUAL -> value >= comparedValue;
        };
    }

    public enum Comparison {
        EQUAL, LESS, GREATER, GREATEREQUAL, LESSEQUAL;

        public static Comparison fromString(String string) {
            string = string.toUpperCase();
            return switch (string) {
                case "=", "==", "EQUAL" -> EQUAL;
                case "<", "LESS" -> LESS;
                case ">", "GREATER" -> GREATER;
                case "<=", "LESSEQUAL" -> LESSEQUAL;
                case ">=", "GREATEREQUAL" -> GREATEREQUAL;
                default -> throw new IllegalArgumentException("This is not a valid comparison type!");
            };
        }
    }


}
