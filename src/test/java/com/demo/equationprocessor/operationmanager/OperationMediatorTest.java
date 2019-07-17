package com.demo.equationprocessor.operationmanager;

import com.demo.equationprocessor.entities.Addition;
import com.demo.equationprocessor.entities.Division;
import com.demo.equationprocessor.entities.Multiply;
import com.demo.equationprocessor.exceptions.DuplicateSymbolsException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OperationMediatorTest {

    private Mediator mediator;

    @Before
    public void setUp() {
        mediator = new OperationMediator();
        mediator.registerOperation(new Addition());
        mediator.registerOperation(new Multiply());
        mediator.registerOperation(new Division());
    }


    @Test
    public void testForRegistrationSuccess() {
        assertEquals(3, mediator.getRegisteredOperations().size());
    }


    @Test(expected = DuplicateSymbolsException.class)
    public void testForDuplicates_AddingDuplicatesShouldThrowAnException() {
        mediator.registerOperation(new Addition());
        assertEquals(3, mediator.getRegisteredOperations().size());
    }


    @Test
    public void testForEvaluationOfSymbol() {
        assertEquals(7, mediator.evaluateExpression(3, 4, '+'), 0);
        assertEquals(20, mediator.evaluateExpression(10, 2, '*'), 0);
        assertEquals(6, mediator.evaluateExpression(30, 5, '/'), 0);
    }

}