import util.SymbolTable;

import java.util.ArrayList;

/**
 * Created by msi1 on 6/10/2018.
 */
public class CodeGenerator
{
    private ArrayList<SymbolTable> symbolTables;
    private CompilerScanner scanner;
    private String[] programBlock;
    private ArrayList<String> semanticStack;
    private int nextTempAddress = 100;
    private int nextVarAdderess = 500;


    public CodeGenerator(ArrayList<SymbolTable> symbolTables, CompilerScanner scanner)
    {
        this.symbolTables = symbolTables;
        this.scanner = scanner;
        this.programBlock = new String[1000];
        this.semanticStack = new ArrayList<>();
    }

    public void generateCode(String actionSymbol)
    {
        // TODO switch case
    }

    private void addToProgramBlock(String instructionName, ArrayList<String> inputs, int index)
    {
        String instruction = "";

        switch (instructionName)
        {
            case "add":
                instruction = "(ADD, " + inputs.get(0) + ", " + inputs.get(1) + ", " + inputs.get(2) + ")";
                break;
            case "sub":
                instruction = "(SUB, " + inputs.get(0) + ", " + inputs.get(1) + ", " + inputs.get(2) + ")";
                break;
            case "assign":
                instruction = "(ASSIGN, " + inputs.get(0) + ", " + inputs.get(1) + ")";
                break;
            case "eq":
                instruction = "(EQ, " + inputs.get(0) + ", " + inputs.get(1) + ", " + inputs.get(2) + ")";
                break;
            case "jpf":
                instruction = "(JPF, " + inputs.get(0) + ", " + inputs.get(1) + ")";
                break;
            case "jp":
                instruction = "(JP, " + inputs.get(0) + ")";
                break;
            case "lt":
                instruction = "(LT, " + inputs.get(0) + ", " + inputs.get(1) + ", " + inputs.get(2) + ")";
                break;
            case "mult":
                instruction = "(MULT, " + inputs.get(0) + ", " + inputs.get(1) + ", " + inputs.get(2) + ")";
                break;
            case "not":
                instruction = "(NOT, " + inputs.get(0) + ", " + inputs.get(1) + ")";
                break;
            case "print":
                instruction = "(PRINT, " + inputs.get(0) + ", " + inputs.get(1) + ", " + inputs.get(2) + ")";
                break;
        }

        programBlock[index] = instruction;
    }
}
