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



    public CompilerScanner(ArrayList<SymbolTable> symbolTables, String fileName)
    {
        try {
            this.scanner = new Scanner(new FileInputStream(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.symbolTables = symbolTables;
    }

    private Pair<String, String > insertToTopStackSymbolTable(String token, String tokenType) {
        SymbolTable symbolTable = this.symbolTables.get(this.symbolTables.size()-1);
        Pair<String, String> entry = new Pair<>(token, tokenType);
        if (symbolTable.getSymbolTableEntry(entry) == null) {
            symbolTable.setSymbolTableEntry(entry.getKey(), new Attribute(entry.getValue(), ""));
        }
        Pair<String, Attribute> temp = symbolTable.getSymbolTableEntry(entry);
        return new Pair<>(temp.getValue().getType(), temp.getKey());

    }

    public Pair<String, String> getNextToken() // TODO unhandled Error while scanning
    {

        String tokenType="";
        String token="";
        if (this.current.length() == 0) {
            if (scanner.hasNext()){
                current = scanner.next();
            }
            else {
                return insertToTopStackSymbolTable("$", "$");
            }
        }

//        System.out.println("here " + current);

        for (int i = current.length() ; i > 0; i--) {
            Scanner tmpsc = new Scanner(current.substring(0, i));
            if (tmpsc.hasNext("((\\+|-)?)([0-9])+[A-Za-z]+")) {
                System.out.println("Error in scanning");
                System.out.println(tmpsc.next("((\\+|-)?)([0-9])+[A-Za-z]*")); //Error in scanning, unexpected Token detected

            } else if (tmpsc.hasNext("[A-Za-z]([A-Za-z]|[0-9])*")) { //the token is String
                token = tmpsc.next("[A-Za-z]([A-Za-z]|[0-9])*");
                switch (token) {
                    case "EOF":
                        tokenType = "EOF";
                        break;
                    case "int":
                        tokenType = "int";
                        break;
                    case "void":
                        tokenType = "void";
                        break;
                    case "if":
                        tokenType = "if";
                        break;
                    case "else":
                        tokenType = "else";
                        break;
                    case "while":
                        tokenType = "while";
                        break;
                    case "return":
                        tokenType = "return";
                        break;
                    default:
                        tokenType = "ID";
                        break;
                }
            } else if (tmpsc.hasNext("((\\+|-)?)([0-9])+")) {
                token = tmpsc.next("((\\+|-)?)([0-9])+");
                tokenType = "NUM";  // this id NUM
            } else if (tmpsc.hasNext("==")) {
                token = tmpsc.next("==");
                tokenType = "==";
            } else if (tmpsc.hasNext("[-+=()*<,;\\[\\]{}]")) {
                token = tmpsc.next("[-+=()*<,;\\[\\]{}]");
                tokenType = token;
            } else
                continue;
            this.current = this.current.substring(token.length(), this.current.length());
//            System.out.println(this.current);
            Pair<String, Attribute>entry = getEntryInSymbolTables(new Pair<>(token, tokenType));
            if (entry == null) {
                if (this.checkDecleration) {
                    insertToTopStackSymbolTable(token, tokenType);
                    return new Pair<>(entry.getValue().getType(), entry.getKey());
                }else {
                    System.out.println("Error can not declare token here");   //TODO error Unhandled Error
                }

            }
            else {
                if (this.checkDecleration){
                    System.out.println("Error can not declare a token twice"); //TODO error Unhandled Error
                }

                return new Pair<>(entry.getValue().getType(), entry.getKey());
            }
        }
        if (current.length() > 0) {
            System.out.println("unexpected Token detected"); //TODO error Unhandled Error
        }

        return null;
    }

    @Nullable
    private Pair<String, Attribute> getEntryInSymbolTables(Pair<String, String> entry) {
        SymbolTable symbolTable;
        for (int i = this.symbolTables.size() - 1; i >= 0; i--) {
            symbolTable = this.symbolTables.get(i);
            return symbolTable.getSymbolTableEntry(entry);
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
