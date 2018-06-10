import javafx.util.Pair;
import util.SymbolTable;

import java.util.ArrayList;

/**
 * Created by RadNi on 6/5/18.
 */
public class Compiler {
    public static void main(String[] args) {

        CompilerScanner compilerScanner = new CompilerScanner(new SymbolTable(), "src/tst_code.txt");
        Pair<String, String> pair = compilerScanner.getNextToken();
        System.out.println(pair.getKey() + " " + pair.getValue());
        for (int i = 0; i < 10; i++) {
            pair = compilerScanner.getNextToken();
            System.out.println(pair.getKey() + " " + pair.getValue());

        }

//        new Compiler().start();
    }

    private void start()
    {
        ArrayList<SymbolTable> symbolTables = new ArrayList<>();
        SymbolTable mainScopeSymbolTable = new SymbolTable(); // TODO implement util.SymbolTable
        symbolTables.add(mainScopeSymbolTable);

        CompilerScanner scanner = new CompilerScanner(symbolTables, "code"); // TODO implement scanner
        CodeGenerator codeGenerator = new CodeGenerator(symbolTables, scanner);
        Parser parser = new Parser(codeGenerator, scanner);
        parser.parse();
    }
}
