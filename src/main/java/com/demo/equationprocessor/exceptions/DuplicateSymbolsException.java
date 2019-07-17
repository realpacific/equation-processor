package com.demo.equationprocessor.exceptions;

public class DuplicateSymbolsException extends RuntimeException {
    public DuplicateSymbolsException(String message) {
        super("The symbol " + message + " already exists.");
    }
}
