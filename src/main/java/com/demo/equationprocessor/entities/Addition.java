package com.demo.equationprocessor.entities;

public class Addition extends Operation {

    public Addition() {
        super('+', 98);
    }

    @Override
    public double calculate(double leftOperand, double rightOperand) {
        return leftOperand + rightOperand;
    }
}
