package me.xemor.skillslibrary2.execution;

import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Execution {

    private final ConcurrentHashMap<String, Double> variables = new ConcurrentHashMap<>();
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    public Execution() {}

    public void setCancelled(boolean cancel) {
        cancelled.set(cancel);
    }

    public void setValue(String variable, double value) {
        variables.put(variable, value);
    }

    public String message(String message, Entity entity) {
        return message(message, Map.of("self", entity.getPersistentDataContainer()));
    }

    public String message(String message, Entity entity, Entity other) {
        return message(message, Map.of("self", entity.getPersistentDataContainer(), "other", other.getPersistentDataContainer()));
    }

    public String message(String message, Map<String, PersistentDataContainer> containers) {
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(message);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String expression = matcher.group(1);
            double evaluatedValue = expression(expression, containers);
            matcher.appendReplacement(result, String.valueOf(evaluatedValue));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public String message(String message) {
        return message(message, Map.of());
    }

    public double expression(String expression) {
        return expression(expression, Map.of());
    }

    public double expression(String expression, Entity self) {
        return expression(expression, Map.of("self", self.getPersistentDataContainer()));
    }

    public double expression(String expression, Entity self, Entity target) {
        return expression(expression, Map.of("self", self.getPersistentDataContainer(), "other", target.getPersistentDataContainer()));
    }

    public double expression(String expression, Map<String, PersistentDataContainer> containers) {
        List<String> postfix = infixToPostfix(expression);
        try {
            return evaluatePostfix(postfix, containers);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static List<String> infixToPostfix(String expression) {
        List<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        StringTokenizer tokenizer = new StringTokenizer(expression, "()+-*/^ ", true);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty()) continue;
            if (isNumeric(token)) {
                output.add(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    output.add(stack.pop());
                }
                stack.pop();
            } else if (isOperator(token)) {
                while (!stack.isEmpty() && precedence(token) <= precedence(stack.peek())) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            output.add(stack.pop());
        }

        return output;
    }

    private double evaluatePostfix(List<String> postfix, Map<String, PersistentDataContainer> containers) throws IllegalArgumentException {
        Deque<Double> stack = new ArrayDeque<>();

        for (String token : postfix) {
            if (isNumeric(token)) {
                stack.push(Double.valueOf(token));
            } else if (isOperator(token)) {
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                    case "^": stack.push(Math.pow(a, b)); break;
                }
            } else {
                stack.push(getValue(token, containers));
            }
        }

        return stack.pop();
    }

    private double getValue(String variable, Map<String, PersistentDataContainer> containers) throws IllegalArgumentException {
        Double newValue = null;
        for (Map.Entry<String, PersistentDataContainer> entry : containers.entrySet()) {
            String prefix = entry.getKey() + "_";
            if (variable.startsWith(prefix)) {
                variable = variable.replaceFirst(prefix, "");
                newValue = entry.getValue().get(new NamespacedKey(SkillsLibrary.getInstance(), variable), PersistentDataType.DOUBLE);
            }
        }
        Double localValue = variables.get(variable.toLowerCase());
        if (localValue != null) {
            newValue = localValue;
        }
        if (newValue == null) {
            throw new IllegalArgumentException(variable + " is undefined!");
        }
        return newValue;
    }

    private static boolean isNumeric(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isOperator(String token) {
        return "+-*/^".contains(token);
    }

    private static int precedence(String operator) {
        return switch (operator) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> 0;
        };
    }
}
