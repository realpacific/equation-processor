package com.demo.equationprocessor.entities;

/**
 * The base class for mathematical operation
 */
public abstract class Operation {
    private Character symbol;
    /**
     * The priority order in simplification rule - The higher the value, the higher the priority of an {@link Operation} is. Follows BODMAS rule
     */
    private int priorityValue;

    Operation(Character symbol, int priorityValue) {
        this.symbol = symbol;
        this.priorityValue = priorityValue;
    }

    public Character getSymbol() {
        return symbol;
    }

    public int getPriorityValue() {
        return priorityValue;
    }

    public abstract double calculate(double leftOperand, double rightOperand);
}
