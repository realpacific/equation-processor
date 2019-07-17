package com.demo.equationprocessor.processors;

import com.demo.equationprocessor.entities.Addition;
import com.demo.equationprocessor.entities.Division;
import com.demo.equationprocessor.entities.Multiply;
import com.demo.equationprocessor.exceptions.InvalidUserInputException;
import com.demo.equationprocessor.operationmanager.Mediator;
import com.demo.equationprocessor.operationmanager.OperationMediator;
import org.junit.Before;
import org.junit.Test;
import com.demo.equationprocessor.readers.FakeReader;
import com.demo.equationprocessor.readers.Reader;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class EquationProcessorTest {
    private Processor processor;
    private final double delta = 0.25;
    private Reader reader = new FakeReader("12");
    private Mediator mediator;
    private Processor defaultEquationProcessor;

    @Before
    public void setUp() {
        mediator = new OperationMediator();
        mediator.registerOperation(new Addition());
        mediator.registerOperation(new Division());
        mediator.registerOperation(new Multiply());
        defaultEquationProcessor = new EquationProcessor("6 + 7", reader, mediator);

    }

    @Test
    public void testForCleanup_shouldRemoveWhiteSpaceAndAppendZeroIfStartsWithOperation() {
        processor = new EquationProcessor(" - 30 * 12 - 12 ", reader, mediator);
        assertEquals("0+-30*12+-12", processor.cleanup(processor.getEquation()));
    }

    @Test
    public void testForExtractWithVariables_ShouldReplaceWithValueOf12() {
        processor = new EquationProcessor("- 30 * x - y", reader, mediator);
        String cleanedEquation = processor.cleanup(processor.getEquation());
        assertEquals("0+-30*x+-y", cleanedEquation);
        processor.evaluateEquation(cleanedEquation);
    }


    @Test
    public void testFromQuestion() {
        processor = new EquationProcessor("31 + 5 * 6 - 7", reader, mediator);
        String cleanedEquation = processor.cleanup(processor.getEquation());
        assertEquals((double) 31 + 5 * 6 - 7, processor.evaluateEquation(cleanedEquation), delta);
    }

    @Test
    public void testForAlternatingOperations() {
        processor = new EquationProcessor("-12+12-12+12", reader, mediator);
        String cleanedEquation = processor.cleanup(processor.getEquation());
        assertEquals((double) 0, processor.evaluateEquation(cleanedEquation), delta);
    }


    @Test
    public void testForVariables() {
        processor = new EquationProcessor("20 + x - y -x", reader, mediator);
        String cleanedEquation = processor.cleanup(processor.getEquation());
        System.out.println(cleanedEquation);
        assertEquals((double) 20 + 12 - 12 - 12, processor.evaluateEquation(cleanedEquation), delta);
    }

    @Test
    public void testForHigherToLowerOrderOperation() {
        processor = new EquationProcessor("90/30*12+1-12", reader, mediator);
        String cleanedEquation = processor.cleanup(processor.getEquation());
        assertEquals(90.0 / 30 * 12 + 1 - 12, processor.evaluateEquation(cleanedEquation), delta);
    }

    @Test
    public void testForLowerToHigherOrderOperation() {
        processor = new EquationProcessor("90-40+10*10/2", reader, mediator);
        String cleanedEquation = processor.cleanup(processor.getEquation());
        assertEquals(90 - 40 + 10 * 10.0 / 2, processor.evaluateEquation(cleanedEquation), delta);
    }


    @Test
    public void testForLowerToRandomlyOccurringOperation() {
        processor = new EquationProcessor(" 90 - 40 * 10 - 10 /2+12-2+12*12", reader, mediator);
        String cleanedEquation = processor.cleanup(processor.getEquation());
        assertEquals(90 - 40 * 10 - 10 / 2 + 12 - 2 + 12 * 12, processor.evaluateEquation(cleanedEquation), delta);
    }


    @Test
    public void testForBadDataFromQuestion() {
        processor = new EquationProcessor("5*+7*6", reader, mediator);
        String cleanedEquation = processor.cleanup(processor.getEquation());
        assertEquals(5 * +7 * 6, processor.evaluateEquation(cleanedEquation), delta);
    }

    @Test
    public void testForPatternCompile() {
        processor = new EquationProcessor("6 + 7", reader, mediator);
        List<Integer> list2 = ((EquationProcessor) processor).findIndicesOfOperationSymbols(processor.cleanup(processor.getEquation()));
        assertEquals(1, list2.size());

        processor = new EquationProcessor("6*7", reader, mediator);
        List<Integer> list3 = ((EquationProcessor) processor).findIndicesOfOperationSymbols(processor.cleanup(processor.getEquation()));
        assertEquals(1, list3.size());

        processor = new EquationProcessor("6 / 7", reader, mediator);
        List<Integer> list4 = ((EquationProcessor) processor).findIndicesOfOperationSymbols(processor.cleanup(processor.getEquation()));
        assertEquals(1, list4.size());

        processor = new EquationProcessor("6 - 7", reader, mediator);
        List<Integer> list5 = ((EquationProcessor) processor).findIndicesOfOperationSymbols(processor.cleanup(processor.getEquation()));
        assertEquals(1, list5.size());

        processor = new EquationProcessor("31 + 5 * 6 - 7", reader, mediator);
        List<Integer> list = ((EquationProcessor) processor).findIndicesOfOperationSymbols(processor.cleanup(processor.getEquation()));
        assertEquals(3, list.size());
    }


    @Test
    public void testForRegexBuilder() {
        processor = new EquationProcessor("6 + 7", reader, mediator);
        assertEquals("\\\\+\\\\*\\\\/", mediator.buildRegexFromRegisteredSymbolSortedByPriorityValue());
    }

    @Test
    public void testForValidityOfEquation() {
        assertTrue(defaultEquationProcessor.isValidEquation("12+x+y-z+12-12"));
        assertTrue(defaultEquationProcessor.isValidEquation("+12+x+y-z+12-12"));
        assertTrue(defaultEquationProcessor.isValidEquation("-12+x+y-z+12-12"));
        assertTrue(defaultEquationProcessor.isValidEquation("-12+x+y-z+-12--12"));
        assertFalse(defaultEquationProcessor.isValidEquation("12+x+y-z+12-12-"));
        assertFalse(defaultEquationProcessor.isValidEquation("12+x+y-z12"));
    }

    @Test(expected = InvalidUserInputException.class)
    public void testForInvalidEquation_ShouldThrowException() {
        processor = new EquationProcessor("6 +*7", reader, mediator);
        processor.execute();

        processor = new EquationProcessor("- 6  +- 7", reader, mediator);
        processor.execute();

        processor = new EquationProcessor("-9-x+-90", reader, mediator);
        processor.execute();


        processor = new EquationProcessor("-9-x90", reader, mediator);
        processor.execute();
    }

    @Test
    public void testForValidEquation_MinusAfterOperationIsStillValid() {
        processor = new EquationProcessor("20+-10", reader, mediator);
        assertEquals(10, processor.execute(), delta);
    }

    @Test
    public void testForReplaceOperationOfSubtractSign_ShouldOnlyReplaceIfItAppearsAloneInBetweenOperands() {
        assertEquals("90+-90+-90+-90",
                ((EquationProcessor) defaultEquationProcessor)
                        .transformSubtractionToAdditionIfAppearsAloneBetweenOperands("90-90-90+-90"));

        assertEquals("y+-x",
                ((EquationProcessor) defaultEquationProcessor)
                        .transformSubtractionToAdditionIfAppearsAloneBetweenOperands("y-x"));
        assertEquals("20+x+-y+-x",
                ((EquationProcessor) defaultEquationProcessor)
                        .transformSubtractionToAdditionIfAppearsAloneBetweenOperands("20+x-y-x"));
    }
}