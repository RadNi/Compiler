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

    public CompilerScanner(ArrayList<SymbolTable> symbolTables, String fileName)
    {

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
                Pair<String, String> entry = new Pair<>("$", "$");
                if (this.symbolTable.getSymbolTableEntry(entry) == null) {
                    this.symbolTable.setSymbolTableEntry(entry, new Attribute());
                }
                Pair<String, Pair<String, Attribute>> temp = this.symbolTable.getSymbolTableEntry(entry);
                return new Pair<>(temp.getValue().getKey(), temp.getKey());
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
            Pair<String, String> entry = new Pair<>(token, tokenType);
            if (this.symbolTable.getSymbolTableEntry(entry) == null) {
                this.symbolTable.setSymbolTableEntry(entry, new Attribute());
            }
            Pair<String, Pair<String, Attribute>> temp = this.symbolTable.getSymbolTableEntry(entry);
            return new Pair<>(temp.getValue().getKey(), temp.getKey());
        }
        if (current.length() > 0) {
            System.out.println("unexpected Token detected"); //TODO error unhandled
        }

        return null;
    }
}
