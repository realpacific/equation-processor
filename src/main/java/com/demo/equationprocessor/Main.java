package com.demo.equationprocessor;

import com.demo.equationprocessor.entities.*;
import com.demo.equationprocessor.exceptions.DuplicateSymbolsException;
import com.demo.equationprocessor.exceptions.InvalidUserInputException;
import com.demo.equationprocessor.operationmanager.OperationMediator;
import com.demo.equationprocessor.processors.EquationProcessor;
import com.demo.equationprocessor.processors.Processor;
import com.demo.equationprocessor.readers.ConsoleReader;
import com.demo.equationprocessor.readers.Reader;

public class Main {

    public static void main(String[] args) {
        OperationMediator mediator = registerOperations();

        while (true) {
            String equation = inputEquation();
            Reader reader = new ConsoleReader();
            Processor processor = new EquationProcessor(equation, reader, mediator);
            try {
                System.out.println("Result: " + processor.execute());
            } catch (InvalidUserInputException | DuplicateSymbolsException e) {
                System.out.println(e.getMessage());
            }

            System.out.println("\n");
        }
    }

    private static OperationMediator registerOperations() {
        OperationMediator mediator = new OperationMediator();
        mediator.registerOperation(new Addition());
        mediator.registerOperation(new Multiply());
        mediator.registerOperation(new Division());
        return mediator;
    }

    private static String inputEquation() {
        Reader reader = new ConsoleReader();
        return reader.read("Input Equation");
    }
}
