package com.demo.equationprocessor.readers;

public class FakeReader implements Reader {
    private String value;


    /**
     * Used only for testing purposes to avoid asking user input via console.
     *
     * @param value used to set value of variables in the equation.
     */
    public FakeReader(String value) {
        this.value = value;
    }

    @Override
    public String read(String displayMessage) {
        System.out.println(displayMessage + ": " + value);
        return value;
    }

}
