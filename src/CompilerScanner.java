import com.sun.istack.internal.Nullable;
import javafx.util.Pair;
import util.Attribute;
import util.ErrorHandler;
import util.SymbolTable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by msi1 on 6/7/2018.
 */
public class CompilerScanner
{
    private Scanner stringScanner;
    private Scanner lineScanner;
    private String currentString="";
    private String currentLine="";
    private int lineNumber = 0;
    private boolean checkDecleration = false;
    private ArrayList<SymbolTable> symbolTables;
    private Node root;
    private ArrayList<Node>nodes = new ArrayList<>();
    private boolean lastTokenTypeDigitOrLetter = false;
    private ErrorHandler errorHandler;


    class Node{

        public int nodeNumber;
        public boolean isFinal = false;
        public ArrayList<Node>neighbors = new ArrayList<>();
        public ArrayList<String>transitions = new ArrayList<>();
        public String tokenType;
        public String token;


        public Node(int nodeNumber, boolean isFinal, String token, String tokenType) {
            this.nodeNumber = nodeNumber;
            this.isFinal = isFinal;
            this.tokenType = tokenType;
            this.token = token;
            nodes.add(this);
        }

        public void addNeighbor(Node neighbor, String transition) {
            this.neighbors.add(neighbor);
            this.transitions.add(transition);
        }

        public boolean canIterate(int index, char input) {
            switch (this.transitions.get(index)) {
                case "$":
                    if (input == '$')
                        return true;
                    break;
                case "not =":
                    if (input != '=')
                        return true;
                    break;
                case "not digit valid" :
                    if ("<*(),;[]{}=+-$".contains(Character.toString(input)) || Character.isLetter(input))
                        return true;
                    break;
                case "letter":
                    if (Character.isLetter(input))
                        return true;
                    break;
                case "digit":
                    if (Character.isDigit(input))
                        return true;
                    break;
                case "letter digit":
                    if (Character.isLetterOrDigit(input))
                        return true;
                    break;
                case "+ -":
                    if (input == '+' || input == '-')
                        return true;
                    break;
                case "=":
                    if (input == '=')
                        return true;
                    break;
                case "<*(),;[]{}":
                    if ("<*(),;[]{}".contains(Character.toString(input)))
                        return true;
                    break;
                case "not op":
                    if ("<*(),;[]{}+-".contains(Character.toString(input)))
                        return true;
                    break;
                case "/":
                    if (input == '/')
                        return true;
                    break;
                case "*":
                    if (input == '*')
                        return true;
                    break;
                case "not /":
                    if (input != '/')
                        return true;
                case "not *":
                    if (input != '*')
                        return true;
                    break;
                case "not all":
                    if (!(Character.isLetterOrDigit(input) || "<*(),;[]{}+-=".contains(Character.toString(input))))
                        return true;
                    break;
                default:
                    System.out.println("something where wrong" + this.transitions.get(index));
            }
            return false;
        }

        private Pair<String, String > getTokenFromInput(String input) {
            if (!this.isFinal) {
                try {
                    throw new Exception("Non-Final state can't return Token");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                String token_="", tokenType_="";
                switch (this.token) {
                    case "this":
                        token_ = input;
                        break;
                    case "getUntilLast":
                        token_ = input.substring(0, input.length() - 1);
                        break;
                }
                switch (this.tokenType) {
                    case "this":
                        tokenType_ = input;
                        break;
                    case "getUntilLast":
                        tokenType_ = input.substring(0, input.length() - 1);
                        break;
                    case "letter":
                        switch (token_){
                            case "EOF":
                                tokenType_ = "EOF";
                                break;
                            case "int":
                                tokenType_ = "int";
                                break;
                            case "void":
                                tokenType_ = "void";
                                break;
                            case "if":
                                tokenType_ = "if";
                                break;
                            case "else":
                                tokenType_ = "else";
                                break;
                            case "while":
                                tokenType_ = "while";
                                break;
                            case "return":
                                tokenType_ = "return";
                                break;
                            default:
                                tokenType_ = "ID";
                                break;
                        }
                        break;
                    case "digit":
                        tokenType_ = "NUM";
                        break;
                }
                return new Pair<>(token_, tokenType_);
            }
            return null;
        }
    }

    class Error extends Node {

        public Error(int nodeNumber, String token, String tokenType) {
            super(nodeNumber, true, token, tokenType);
        }

        public String handleError(String input, int index) {
            ArrayList<String>error = new ArrayList<>();
            error.add("Irregular token:"+ input.charAt(index) + " detected at line #" + lineNumber);
            switch (this.token) {
                case "*":
                    input = input.substring(0, index) + "*" + input.substring(index);
                    error.add("token removed from input");

                    break;
                case "delete":
                    input = input.substring(0, index) + input.substring(index+1);
                    error.add("token replaced with *");

                    break;
            }
            errorHandler.addError(error, Integer.toString(lineNumber), "Unexpected Token");

//            input = input.substring(0, index) + input.substring(index + 1);
//            System.out.println("in error :          " + input);
            return input;
        }

    }


    private Node makeTransitionDFA(){
        this.root = new Node(0, false, "", "");
        Error error = new Error(17, "delete", "delete");
        root.addNeighbor(new Node(1, true, "this", "this"), "<*(),;[]{}");
        Node temp = new Node(2, false, "", "");
        root.addNeighbor(temp, "=");
        temp.addNeighbor(new Node(3, true, "this", "this"), "=");
        temp.addNeighbor(new Node(4, true, "getUntilLast", "getUntilLast"), "not =");
        temp = new Node(5, false, "", "");
        root.addNeighbor(temp, "letter");
        temp.addNeighbor(temp, "letter digit");
        temp.addNeighbor(new Node(6, true, "getUntilLast", "letter"), "not op");
        temp.addNeighbor(new Node(18, true, "getUntilLast", "letter"), "$");
        temp.addNeighbor(error, "not all");
        temp = new Node(7, false, "", "");
        root.addNeighbor(temp, "digit");
        temp.addNeighbor(temp, "digit");
        temp.addNeighbor(new Error(11, "*", "*"), "letter");
        temp.addNeighbor(new Node(8, true, "getUntilLast", "digit"), "not digit valid");
        temp.addNeighbor(new Node(19, true, "getUntilLast", "digit"), "$");
        temp.addNeighbor(error, "not all");
        Node temp2 = new Node(9, false, "", "");
        root.addNeighbor(temp2, "+ -");
        temp2.addNeighbor(temp, "digit");
        temp2.addNeighbor(new Node(10, true, "getUntilLast", "getUntilLast"), "not digit valid");
        temp2 = new Node(12, false, "", "");
        root.addNeighbor(temp2, "/");
        temp = new Node(13, false, "", "");
        temp2.addNeighbor(temp, "*");
        temp2.addNeighbor(error, "not *");
        temp2 = new Node(14, false, "", "");
        temp.addNeighbor(temp2, "*");
        temp.addNeighbor(temp, "not *");
        Node temp3 = new Node(15, true, "comment", "comment");
        temp2.addNeighbor(temp3, "/");
        temp2.addNeighbor(temp, "not /");
        root.addNeighbor(error, "not all");

        return root;
    }

    public CompilerScanner(ArrayList<SymbolTable> symbolTables, String fileName, ErrorHandler errorHandler)
    {
        try {
            this.lineScanner = new Scanner(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.symbolTables = symbolTables;
        this.root = this.makeTransitionDFA();
        this.errorHandler = errorHandler;

        // for adding output function in global scope

        symbolTables.get(0).setSymbolTableEntry("output", new Attribute("ID", 1));

    }

    private Pair<String, String > insertToTopStackSymbolTable(String token, String tokenType) {
        SymbolTable symbolTable = this.symbolTables.get(this.symbolTables.size()-1);
        if (symbolTable.getSymbolTableEntry(token) == null) {
            symbolTable.setSymbolTableEntry(token, new Attribute(tokenType, lineNumber));
        }
        Pair<String, Attribute> temp = new Pair<>(token, symbolTable.getSymbolTableEntry(token));
        return new Pair<>(temp.getValue().getType(), temp.getKey());

    }



    public Pair<String, Pair<String, String>> doDFA(String input) {
        Node node = root;
        int index = 0;
        input += "$";   // TODO     if input contains $ everything will collapse:)
        while (true) {
            boolean errorFlag = false;
            if (node.isFinal) {
                Pair<String, String>pair = node.getTokenFromInput(input.substring(0, index));
                input = input.substring(pair.getKey().length());
                return new Pair<>(input.substring(0, input.length()-1), pair);
            }
            for (int i = 0; i < node.neighbors.size(); i++) {
                if (node.canIterate(i, input.charAt(index))) {
                    if (node.neighbors.get(i) instanceof Error) {
                        input = ((Error) node.neighbors.get(i)).handleError(input, index);
                        errorFlag = true;
                        break;
                    }
                    node = node.neighbors.get(i);
                    break;
                }
            }
            if (!errorFlag)
                index++;
        }
    }

    public Pair<String, String> getNextToken()
    {

        if (this.currentString.length() == 0) {
            if (this.stringScanner == null) {
                this.currentLine = lineScanner.nextLine();
                lineNumber++;
                this.stringScanner = new Scanner(this.currentLine);
                this.currentString = stringScanner.next();
            } else if (!this.lineScanner.hasNextLine() && !this.stringScanner.hasNext()) {
                return insertToTopStackSymbolTable("$", "$");
            } else if (this.lineScanner.hasNextLine() && !this.stringScanner.hasNext()) {
                this.currentLine = lineScanner.nextLine();
                lineNumber++;
                this.stringScanner = new Scanner(currentLine);
                this.currentString = stringScanner.next();
            } else if (this.stringScanner.hasNext()) {
                this.currentString = this.stringScanner.next();
            }
        }
        String tokenType;
        String token;


        Pair<String, Pair<String, String>> pair;
        pair = doDFA(this.currentString);

        token = pair.getValue().getKey();
        tokenType = pair.getValue().getValue();
        this.currentString = pair.getKey();

        if (lastTokenTypeDigitOrLetter && tokenType.equals("NUM") && (token.charAt(0) == '+'  || token.charAt(0) == '-')) {
            token = Character.toString(token.charAt(0));
            tokenType = token;
//            this.currentString = this.currentString.substring(token.length()-1, this.currentString.length());
//            System.out.println("befor edit: " + currentString + " token: " + token);
            this.currentString = pair.getValue().getKey().substring(1) + this.currentString;
//            System.out.println("after edit : " + currentString);

        }

//        this.currentString = this.currentString.substring(token.length()+errorHandlerIndex, this.currentString.length());
        lastTokenTypeDigitOrLetter = tokenType.equals("NUM") || tokenType.equals("ID");


        Attribute attribute = getEntryInSymbolTables(token);
        if (attribute == null) {
            if (tokenType.equals("ID")) {
                if (this.checkDecleration) {
                    insertToTopStackSymbolTable(token, tokenType);
                    return new Pair<>(tokenType, token);
                } else {
                    ArrayList<String>error = new ArrayList<>();
                    error.add("Can not declare token here");
                    this.errorHandler.addError(error, Integer.toString(lineNumber), "not Declaration");
                }
            }else {
                insertToTopStackSymbolTable(token, tokenType);
                return new Pair<>(tokenType, token);
            }

        } else {
            if (this.checkDecleration && tokenType.equals("ID")) {
                ArrayList<String>error = new ArrayList<>();

                error.add("Can not declare a token twice: " + token);
                errorHandler.addError(error, Integer.toString(lineNumber), "Token Duplication");
            }
            return new Pair<>(attribute.getType(), token);
        }

        if (currentString.length() > 0) {
            ArrayList<String>error = new ArrayList<>();
            error.add("Unexpected Token detected");
            errorHandler.addError(error, Integer.toString(lineNumber), "Unexpected Token");
        }

        return null;
    }

    @Nullable
    public Attribute getEntryInSymbolTables(String token) {
        SymbolTable symbolTable;
        for (int i = this.symbolTables.size() - 1; i >= 0; i--) {
            symbolTable = this.symbolTables.get(i);
            Attribute attribute = symbolTable.getSymbolTableEntry(token);
            if (attribute != null)
                return attribute;
        }
        return null;
    }

    public void setCheckDecleration(boolean checkDecleration) {
        this.checkDecleration = checkDecleration;
    }

    public ArrayList<SymbolTable> getSymbolTables() {
        return symbolTables;
    }
}
