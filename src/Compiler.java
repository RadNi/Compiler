import javafx.util.Pair;
import util.SymbolTable;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by RadNi on 6/5/18.
 */
public class Compiler {
    public static void main(String[] args) {

//        new Compiler().start();
    }

    private void start()
    {
        SymbolTable symbolTable = new SymbolTable(); // TODO implement util.SymbolTable
        CompilerScanner scanner = new CompilerScanner(symbolTable, "code"); // TODO implement scanner
        Parser parser = new Parser(symbolTable, scanner);
        parser.parse();
    }
}
