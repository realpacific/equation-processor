package com.demo.equationprocessor.entities;

public class Multiply extends Operation {

    public Multiply() {
        super('*', 99);
    }

    @Override
    public double calculate(double leftOperand, double rightOperand) {
        return leftOperand * rightOperand;
    }
}
