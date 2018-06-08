import javafx.util.Pair;
import util.SymbolTable;

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
        SymbolTable symbolTable = new SymbolTable(); // TODO implement util.SymbolTable
        CompilerScanner scanner = new CompilerScanner(symbolTable, "code"); // TODO implement scanner
        Parser parser = new Parser(symbolTable, scanner);
        parser.parse();
    }
}
