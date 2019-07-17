package com.demo.equationprocessor.entities;

public class Division extends Operation {

    public Division() {
        super('/', 100);
    }

    @Override
    public double calculate(double leftOperand, double rightOperand) {
        return leftOperand / rightOperand;
    }
}
