package com.demo.equationprocessor.processors;

public abstract class Processor {
    private String equation;

    Processor(String equation) {
        this.equation = equation;
    }


    String getEquation() {
        return equation;
    }

    abstract String cleanup(String input);

    /**
     * @deprecated
     */
    abstract boolean isValidEquation(String equation);

    abstract double evaluateEquation(String input);

    public double execute() {
        String cleanedEquation = cleanup(equation);
        /*if(!isValidEquation(cleanedEquation)){
            throw new InvalidUserInputException("Pre-processing error: The equation " + equation + " is invalid.");
        }*/
        return evaluateEquation(cleanedEquation);
    }

}
