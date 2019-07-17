package com.demo.equationprocessor.operationmanager;

import com.demo.equationprocessor.entities.Operation;
import com.demo.equationprocessor.exceptions.DuplicateSymbolsException;
import com.demo.equationprocessor.exceptions.InvalidUserInputException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OperationMediator implements Mediator {
    private List<Operation> operations = new ArrayList<>();

    @Override
    public List<Operation> getRegisteredOperations() {
        return operations;
    }

    @Override
    public void registerOperation(Operation entry) {
        checkForDuplicates(entry);
        operations.add(entry);
    }

    private void checkForDuplicates(Operation entry) {
        for (Operation operation : operations) {
            if (operation.getSymbol().equals(entry.getSymbol()))
                throw new DuplicateSymbolsException("The symbol " + entry.getSymbol().toString() + " already exists.");
            if (operation.getSymbol().equals('-'))
                throw new DuplicateSymbolsException("Subtraction is handled by '+' operator. No need to add this operation.");
        }
    }

    @Override
    public int getPriorityOrder(char symbol) {
        Optional<Operation> optionalOperation = operations.stream().filter(operation -> operation.getSymbol().equals(symbol)).findFirst();
        if (optionalOperation.isPresent()) return optionalOperation.get().getPriorityValue();
        else throw new InvalidUserInputException("Symbol " + symbol + " has not been registered.");
    }

    @Override
    public double evaluateExpression(double leftOperand, double rightOperand, char symbol) {
        for (Operation operation : operations) {
            if (operation.getSymbol().equals(symbol))
                return operation.calculate(leftOperand, rightOperand);
        }
        throw new InvalidUserInputException("Symbol " + symbol + " has not been registered.");
    }
}
