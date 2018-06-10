import com.sun.istack.internal.Nullable;
import javafx.util.Pair;
import util.Attribute;
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
    private Scanner scanner;
    private String current="";
    private boolean checkDecleration = false;
    private ArrayList<SymbolTable> symbolTables;
    private Node root;
    private ArrayList<Node>nodes = new ArrayList<>();
    private boolean lastTokenTypeDigitOrLetter = false;


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
                case "other":
                    return true;
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
            }
            return false;
        }

        private Pair<String, String > getTokenFromInput(String input) {
            if (!this.isFinal) {
                try {
                    throw new Exception("Non-Final state cant retun Token");
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
                        tokenType_ = "ID";
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


    private Node makeTransitionDFA(){
        this.root = new Node(0, false, "", "");
        root.addNeighbor(new Node(1, true, "this", "this"), "<*(),;[]{}");
        Node temp = new Node(2, false, "", "");
        root.addNeighbor(temp, "=");
        temp.addNeighbor(new Node(3, true, "this", "this"), "=");
        temp.addNeighbor(new Node(4, true, "getUntilLast", "getUntilLast"), "other");
        temp = new Node(5, false, "", "");
        root.addNeighbor(temp, "letter");
        temp.addNeighbor(temp, "letter digit");
        temp.addNeighbor(new Node(6, true, "getUntilLast", "letter"), "other");
        temp = new Node(7, false, "", "");
        root.addNeighbor(temp, "digit");
        temp.addNeighbor(temp, "digit");
        temp.addNeighbor(new Node(8, true, "getUntilLast", "digit"), "other");
        Node temp2 = new Node(9, false, "", "");
        root.addNeighbor(temp2, "+ -");
        temp2.addNeighbor(temp, "digit");
        temp2.addNeighbor(new Node(10, true, "getUntilLast", "getUntilLast"), "other");

        return root;
    }

    public CompilerScanner(ArrayList<SymbolTable> symbolTables, String fileName)
    {
        try {
            this.scanner = new Scanner(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.symbolTables = symbolTables;
        this.root = this.makeTransitionDFA();

        // for adding output function in global scope
        
        symbolTables.get(0).setSymbolTableEntry("output", new Attribute("ID"));

    }

    private Pair<String, String > insertToTopStackSymbolTable(String token, String tokenType) {
        SymbolTable symbolTable = this.symbolTables.get(this.symbolTables.size()-1);
        if (symbolTable.getSymbolTableEntry(token) == null) {
            symbolTable.setSymbolTableEntry(token, new Attribute(tokenType));
        }
        Pair<String, Attribute> temp = new Pair<>(token, symbolTable.getSymbolTableEntry(token));
        return new Pair<>(temp.getValue().getType(), temp.getKey());

    }



    public Pair<String, String> doDFA(String input) { // TODO  edit current String after this
        Node node = root;
        int index = 0;
        boolean flag = false;
        input += "$";
        while (true) {
            if (node.isFinal) {
                return node.getTokenFromInput(input.substring(0, index));
            }
            for (int i = 0; i < node.neighbors.size(); i++) {
                if (node.canIterate(i, input.charAt(index))) {
                    node = node.neighbors.get(i);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                try {
                    System.out.println(input);
                    throw new Exception("Irregular Token detected"); // TODO error   Irregular token detected
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            index++;
        }
    }

    public Pair<String, String> getNextToken() // TODO unhandled Error while scanning
    {

        if (this.current.length() == 0) {
            if (scanner.hasNext()) {
                current = scanner.next();
            } else {
                return insertToTopStackSymbolTable("$", "$");
            }
        }

        String tokenType;
        String token;


        Pair<String, String> pair = doDFA(this.current);
        token = pair.getKey();
        tokenType = pair.getValue();

        if (lastTokenTypeDigitOrLetter && tokenType.equals("NUM") && (token.charAt(0) == '+'  || token.charAt(0) == '-')) {
            token = Character.toString(token.charAt(0));
            tokenType = token;
        }
        this.current = this.current.substring(token.length(), this.current.length());
        lastTokenTypeDigitOrLetter = tokenType.equals("NUM") || tokenType.equals("ID");

        Attribute attribute = getEntryInSymbolTables(token);
        if (attribute == null) {
            if (tokenType.equals("ID")) {
                if (this.checkDecleration) {
                    insertToTopStackSymbolTable(token, tokenType);
                    return new Pair<>(tokenType, token);
                } else {
                    System.out.println("Error can not declare token here");   //TODO error Unhandled Error
                }
            }else {
                insertToTopStackSymbolTable(token, tokenType);
                return new Pair<>(tokenType, token);
            }

        } else {
            if (this.checkDecleration && tokenType.equals("ID")) {
                System.out.println("Error can not declare a token twice: " + token); //TODO error Unhandled Error
            }
            return new Pair<>(attribute.getType(), token);
        }

        if (current.length() > 0) {
            System.out.println("unexpected Token detected"); //TODO error Unhandled Error
        }

        return null;
    }

    @Nullable
    private Attribute getEntryInSymbolTables(String token) {
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
