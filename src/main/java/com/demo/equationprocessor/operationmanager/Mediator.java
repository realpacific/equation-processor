package com.demo.equationprocessor.operationmanager;

import com.demo.equationprocessor.entities.Operation;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface Mediator {
    List<Operation> getRegisteredOperations();

    void registerOperation(Operation entry);

    int getPriorityOrder(char symbol);

    double evaluateExpression(double leftOperand, double rightOperand, char symbol);

    default String buildRegexFromRegisteredSymbolSortedByPriorityValue() {
        return getRegisteredOperations().stream().sorted(Comparator.comparing(Operation::getPriorityValue))
                .map(Operation::getSymbol)
                .map(character -> "\\\\" + character)
                .collect(Collectors.joining());
    }
}
