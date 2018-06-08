import javafx.util.Pair;
import util.Attribute;
import util.SymbolTable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by msi1 on 6/7/2018.
 */
public class CompilerScanner
{
    private Scanner scanner;
    public SymbolTable symbolTable;

    public CompilerScanner(SymbolTable symbolTable, String fileName)
    {
        try {
            this.scanner = new Scanner(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.symbolTable = symbolTable;

    }

    public Pair<Pair<String, String>, Attribute> getNextToken() // TODO unhandled Error while scanning
    {
        String tokenType="";
        String token="";
        if(scanner.hasNext("((\\+|-)?)([0-9])+[A-Za-z]")) {
            System.out.println("Error in scanning");
            System.out.println(scanner.next("((\\+|-)?)([0-9])+[A-Za-z]")); //Error in scanning, unexpected Token detected

        }else if (scanner.hasNext("[A-Za-z]([A-Za-z]|[0-9])*")) { //the token is String
            token = scanner.next("[A-Za-z]([A-Za-z]|[0-9])*");
            if (token.equals("EOF")){
                tokenType = "EOF";
            } else if (token.equals("int")) {
                tokenType = "int";
            } else if (token.equals("void")) {
                tokenType = "void";
            } else if (token.equals("if")) {
                tokenType = "if";
            } else if (token.equals("else")) {
                tokenType = "else";
            } else if (token.equals("while")) {
                tokenType = "while";
            } else if (token.equals("return")) {
                tokenType = "return";
            } else {
                tokenType = "ID";
            }
        }else if (scanner.hasNext("((\\+|-)?)([0-9])+")) {
            token = scanner.next("((\\+|-)?)([0-9])+");
            tokenType = "NUM";  // this id NUM
        }else if (scanner.hasNext("[0-9]")) {
            token = scanner.next("[0-9]");
            tokenType = "digit";
        }else if (scanner.hasNext("[A-Za-z]")) {
            token = scanner.next("[A-Za-z]");
            tokenType = "letter";
        }else if (scanner.hasNext("==")) {
            token = scanner.next("==");
            tokenType = "==";
        }else if (scanner.hasNext("[-+=()*<,;\\[\\]{}]")) {
            token = scanner.next("[-+=()*<,;\\[\\]{}]");
            tokenType = token;
        }
        Pair<String, String> entry = new Pair<>(token, tokenType);
        if(this.symbolTable.getSymbolTableEntry(entry) == null) {
            this.symbolTable.setSymbolTableEntry(entry, new Attribute());
        }

        return this.symbolTable.getSymbolTableEntry(entry);
    }
}
