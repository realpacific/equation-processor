package com.demo.equationprocessor.processors;

import com.demo.equationprocessor.exceptions.InvalidUserInputException;
import com.demo.equationprocessor.operationmanager.Mediator;
import com.demo.equationprocessor.readers.Reader;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.demo.equationprocessor.utils.Utils.isDouble;
import static com.demo.equationprocessor.utils.Utils.isVariable;

public class EquationProcessor extends Processor {

    private Stack<Double> operandStack = new Stack<>();
    private Stack<Character> operationStack = new Stack<>();
    private Reader variableValueReader;
    private Mediator operationMediator;
    private VariableRegistry variableRegistry;

    public EquationProcessor(String equation, Reader variableValueReader, Mediator operationMediator) {
        super(equation);
        this.variableValueReader = variableValueReader;
        this.operationMediator = operationMediator;
        variableRegistry = new VariableRegistry();
    }

    @Override
    protected String cleanup(String input) {
        String output = removeWhiteSpaces(input);
        output = prefixZeroOnEquationIfNecessary(output);
        output = transformSubtractionToAdditionIfAppearsAloneBetweenOperands(output);
        return output;
    }

    String transformSubtractionToAdditionIfAppearsAloneBetweenOperands(String equation) {
        StringBuilder output = new StringBuilder(equation);
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]-[a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(equation);
        while (matcher.find()) {
            int indexOfSubtractSign = output.indexOf("-", matcher.start());
            output.replace(indexOfSubtractSign, indexOfSubtractSign + 1, "+-");
            equation = output.toString();
            // Resubmit the updated equation for matching again
            matcher = pattern.matcher(equation);
        }
        return output.toString();
    }

    private String prefixZeroOnEquationIfNecessary(String output) {
        if (output.startsWith("-") || output.startsWith("+")) {
            output = "0" + output;
        }
        return output;
    }

    private String removeWhiteSpaces(String input) {
        return input.replaceAll("\\s", "");
    }

    @Override
    protected boolean isValidEquation(String equation) {
        System.out.println(equation);
        String regexOfRegisteredSymbols = operationMediator.buildRegexFromRegisteredSymbolSortedByPriorityValue();
        Pattern p = Pattern.compile("^[\\-\\+]?([0-9]+|[a-zA-Z])([" + regexOfRegisteredSymbols + "-]{1}((-?[0-9]+)|[a-zA-Z]))+$");
        Matcher m = p.matcher(equation);
        return m.matches();
    }

    @Override
    protected double evaluateEquation(String input) {
        List<Integer> indicesOfOperations = findIndicesOfOperationSymbols(input);

        int startIndex = 0;
        for (int i = 0; i <= indicesOfOperations.size(); i++) {
            Integer currentIndex = (i == indicesOfOperations.size()) ? startIndex : indicesOfOperations.get(i);
            String subString = (currentIndex == startIndex) ? input.substring(startIndex) : input.substring(startIndex, currentIndex);

            pushStringToRespectiveStackBasedOnType(subString);

            if (startIndex != currentIndex) {
                char currentOperation = input.charAt(currentIndex);
                evaluateOperationSymbol(currentOperation);
            }
            startIndex = currentIndex + 1;
        }

        evaluateRemainingOperationAndOperand();
        if (operationStack.size() != 0) throw new InvalidUserInputException("Invalid operation symbols mismatch.");
        // Result is the last remaining value in the operand stack
        return operandStack.pop();
    }

    private void pushStringToRespectiveStackBasedOnType(String subString) {
        if (isDouble(subString)) {
            operandStack.add(Double.valueOf(subString));
        } else if (isVariable(subString)) {
            // Since '-' is not read as a operator and pushed to stack along with the operand, we have to keep track of whether'-' exits.
            boolean isNegationRequired = subString.startsWith("-");
            char variableName = isNegationRequired ? subString.charAt(1) : subString.charAt(0);
            if (!variableRegistry.contains(variableName)) {
                String userInput = variableValueReader.read("Enter value for " + variableName);
                if (isDouble(userInput)) {
                    double variableValue = Double.valueOf(userInput) * (isNegationRequired ? -1 : 1);
                    variableRegistry.add(variableName, variableValue);
                    // If the variable starts with "-", negate the user's input before pushing it to stack
                    operandStack.add(variableValue);
                } else throw new InvalidUserInputException("Invalid input for variables. Must be numerical.");
            } else {
                operandStack.add(variableRegistry.get(variableName) * (isNegationRequired ? -1 : 1));
            }
        }
    }

    private void evaluateOperationSymbol(char currentOperation) {
        if (isToBePushedToStack(currentOperation))
            operationStack.add(currentOperation);
        else {
            do {
                popTopTwoOperandsAndTopOperation();
            } while (requiresPopping(currentOperation));
            operationStack.add(currentOperation);
        }
    }

    private void evaluateRemainingOperationAndOperand() {
        int totalItemsRemainingInOperationStack = operationStack.size();
        for (int i = 0; i < totalItemsRemainingInOperationStack; i++) {
            popTopTwoOperandsAndTopOperation();
        }
    }

    private void popTopTwoOperandsAndTopOperation() {
        try {
            char poppedOperation = operationStack.pop();
            double topmostOperand = operandStack.pop();
            double secondTopmostOperand = operandStack.pop();
            operandStack.push(operationMediator.evaluateExpression(secondTopmostOperand, topmostOperand, poppedOperation));
        } catch (EmptyStackException e) {
            e.printStackTrace();
            throw new InvalidUserInputException("Failed while processing. Invalid equation.");
        }
    }

    private boolean requiresPopping(char operation) {
        return operationStack.size() > 0 && (getPriorityOrderOf(operationStack.peek()) > getPriorityOrderOf(operation));
    }

    private boolean isToBePushedToStack(char operation) {
        if (operationStack.size() == 0) return true;
        int priorityOrderOfCurrentOperation = getPriorityOrderOf(operation);
        int priorityOrderOfOperationAtTopOfStack = getPriorityOrderOf(operationStack.peek());
        return priorityOrderOfCurrentOperation > priorityOrderOfOperationAtTopOfStack;
    }

    private int getPriorityOrderOf(char operation) {
        return operationMediator.getPriorityOrder(operation);
    }

    List<Integer> findIndicesOfOperationSymbols(String input) {
        Pattern operationPattern = Pattern.compile("[" + operationMediator.buildRegexFromRegisteredSymbolSortedByPriorityValue() + "]");
        Matcher matcher = operationPattern.matcher(input);
        List<Integer> indicesOfOperations = new ArrayList<>();
        while (matcher.find()) {
            indicesOfOperations.add(matcher.start());
        }
        return indicesOfOperations;
    }

}
