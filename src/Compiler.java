/**
 * Created by RadNi on 6/5/18.
 */
public class Compiler {
    public static void main(String[] args) {
        new Compiler().start();
    }

    private void start()
    {
        SymbolTable symbolTable = new SymbolTable(); // TODO implement SymbolTable
        CompilerScanner scanner = new CompilerScanner(symbolTable); // TODO implement scanner
        Parser parser = new Parser(symbolTable, scanner);
        parser.parse();
    }
}
