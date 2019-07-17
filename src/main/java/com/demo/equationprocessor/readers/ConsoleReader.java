package com.demo.equationprocessor.readers;

import java.util.Scanner;

public class ConsoleReader implements Reader {
    @Override
    public String read(String displayMessage) {
        System.out.print(displayMessage + ": ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

}
