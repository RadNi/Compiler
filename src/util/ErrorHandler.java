package util;

import java.util.ArrayList;

/**
 * Created by RadNi on 6/10/18.
 */
public class ErrorHandler {

    private ArrayList<ArrayList<String>>errorStack = new ArrayList<>();
    private ArrayList<String>lineNumbers = new ArrayList<>();
    private ArrayList<String>types = new ArrayList<>();
    private String className;

    public ErrorHandler(String className) {
        this.className = className;
    }

    public void addError(ArrayList<String> error, String lineNumber, String type) {
        this.errorStack.add(error);
        this.lineNumbers.add(lineNumber);
        this.types.add(type);
    }

    public void printStack() {
        System.out.println(className + " " + "Errors and Warnings: ");
        for (int i = 0; i < this.errorStack.size(); i++) {
            System.out.println("    "+(i+1)+ ") " + this.types.get(i));
            System.out.println("\t\t@ line #" + this.lineNumbers.get(i));
            for (int j = 0; j < this.errorStack.get(i).size(); j++) {
                System.out.println("\t  " + this.errorStack.get(i).get(j));
            }
        }
    }

}
