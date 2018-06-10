import javafx.util.Pair;
import util.ErrorHandler;
import util.SymbolTable;

import java.util.ArrayList;

/**
 * Created by RadNi on 6/5/18.
 */
public class Compiler {
    public static void main(String[] args) {

        ArrayList<SymbolTable> symbolTables = new ArrayList<>();
        symbolTables.add(new SymbolTable());
        symbolTables.add(new SymbolTable());
        ErrorHandler scannerErrorHandler = new ErrorHandler("Scanner");
        CompilerScanner compilerScanner = new CompilerScanner(symbolTables, "src/tst_code.txt", scannerErrorHandler);
        Pair<String, String> pair;
        compilerScanner.setCheckDecleration(true);
//        pair = compilerScanner.doDFA("$$");
//        System.out.println(pair.getKey() + " " + pair.getValue());

        do {
            pair = compilerScanner.getNextToken();
            System.out.println(pair.getKey() + " " + pair.getValue());

        }while (!pair.getValue().equals("$"));
        System.out.println(pair.getKey() + " " + pair.getValue());
        for (int i = 0; i < 10; i++) {
            pair = compilerScanner.getNextToken();
            System.out.println(pair.getKey() + " " + pair.getValue());

        }

        scannerErrorHandler.printStack();
//        new Compiler().start();
    }

    private void start()
    {
        ArrayList<SymbolTable> symbolTables = new ArrayList<>();
        SymbolTable mainScopeSymbolTable = new SymbolTable(); // TODO implement util.SymbolTable
        symbolTables.add(mainScopeSymbolTable);

        ErrorHandler errorHandler = new ErrorHandler("Parser");
        CompilerScanner scanner = new CompilerScanner(symbolTables, "code", new ErrorHandler("Scanner")); // TODO implement scanner
        CodeGenerator codeGenerator = new CodeGenerator(symbolTables, scanner);
        Parser parser = new Parser(codeGenerator, scanner, errorHandler);
        parser.parse();
        errorHandler.printStack();
    }
}
