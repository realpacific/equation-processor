package com.demo.equationprocessor.processors;

import java.util.HashMap;
import java.util.Map;

class VariableRegistry {
    private Map<Character, Double> mapOfVariables = new HashMap<>();

    boolean contains(char character) {
        return mapOfVariables.containsKey(character);
    }

    void add(char character, double value) {
        if (!contains(character)) mapOfVariables.put(character, value);
    }

    double get(char character) {
        return mapOfVariables.get(character);
    }

}
