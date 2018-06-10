import util.SymbolTable;

import java.util.ArrayList;

/**
 * Created by msi1 on 6/10/2018.
 */
public class CodeGenerator
{
    private ArrayList<SymbolTable> symbolTables;
    private CompilerScanner scanner;

    public CodeGenerator(ArrayList<SymbolTable> symbolTables, CompilerScanner scanner)
    {
        this.symbolTables = symbolTables;
        this.scanner = scanner;
    }

    public void generateCode(String actionSymbol)
    {
        // TODO switch case
    }
}
