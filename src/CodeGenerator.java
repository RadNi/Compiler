import util.Attribute;
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
    private int i = 1;


    public CodeGenerator(ArrayList<SymbolTable> symbolTables, CompilerScanner scanner)
    {
        this.symbolTables = symbolTables;
        this.scanner = scanner;
        this.programBlock = new String[1000];
        this.semanticStack = new ArrayList<>();
    }

    public void generateCode(String actionSymbol, String input)
    {
        switch (actionSymbol)
        {
            case "#file_start":
                fileStart();
                break;
            case "#jp":
                jump();
                break;
            case "#push":
                push(input);
                break;
            case "#dec_start":
                scanner.setCheckDecleration(true);
                break;
            case "#dec_end":
                scanner.setCheckDecleration(false);
                break;
            case "#push_id":
                pushId(input);
                break;
            case "#insert_normal_id":
                insertNormalVariable();
                break;
            case "#insert_array_id":
                insertArrayVariable();
                break;
            case "#insert_fun":
                insertFunction(input);
        }
    }

    private void insertFunction(String input)
    {

    }

    private void insertArrayVariable()
    {
        int arraySize = Integer.parseInt(getFromSSTop(0));
        pop(1);

        String idName = getFromSSTop(0);
        pop(1);

        String varType = getFromSSTop(0);
        pop(1);
        if (varType.equals("void"))
        {
            // TODO error
        } else if (arraySize < 0)
        {
            // TODO error
        }

        Attribute attribute = findAttribute(idName); // TODO check if attribute is null
        attribute.setVarType("array");
        attribute.setArraySize(arraySize);
    }

    private void insertNormalVariable()
    {
        String idName = getFromSSTop(0);
        pop(1);

        String varType = getFromSSTop(0);
        if (varType.equals("void"))
        {
            // TODO error
        }
        pop(1);

        Attribute attribute = findAttribute(idName); // TODO check if attribute is null
        attribute.setVarType("int");
    }

    private void pushId(String input)
    {
        findVarAddress(input);
        push(input);
    }

    private void jump()
    {
        String jumpAddress = getFromSSTop(0);
        addToProgramBlock("jp", new String[] {jumpAddress}, i - 1);
        pop(3);
    }

    private void fileStart()
    {
        String counterAdd = getTemp();
        push(counterAdd);

        String spAdd = getTemp();
        push(spAdd);

        push(String.valueOf(i));
        i++;
    }

    private void addToProgramBlock(String instructionName, String[] inputs, int index)
    {
        String instruction = "";

        switch (instructionName)
        {
            case "add":
                instruction = "(ADD, " + inputs[0] + ", " + inputs[1] + ", " + inputs[2] + ")";
                break;
            case "sub":
                instruction = "(SUB, " + inputs[0] + ", " + inputs[1] + ", " + inputs[2] + ")";
                break;
            case "assign":
                instruction = "(ASSIGN, " + inputs[0] + ", " + inputs[1] + ")";
                break;
            case "eq":
                instruction = "(EQ, " + inputs[0] + ", " + inputs[1] + ", " + inputs[2] + ")";
                break;
            case "jpf":
                instruction = "(JPF, " + inputs[0] + ", " + inputs[1] + ")";
                break;
            case "jp":
                instruction = "(JP, " + inputs[0] + ")";
                break;
            case "lt":
                instruction = "(LT, " + inputs[0] + ", " + inputs[1] + ", " + inputs[2] + ")";
                break;
            case "mult":
                instruction = "(MULT, " + inputs[0] + ", " + inputs[1] + ", " + inputs[2] + ")";
                break;
            case "not":
                instruction = "(NOT, " + inputs[0] + ", " + inputs[1] + ")";
                break;
            case "print":
                instruction = "(PRINT, " + inputs[0] + ", " + inputs[1] + ", " + inputs[2] + ")";
                break;
        }

        programBlock[index] = instruction;
    }

    private String getTemp()
    {
        String result = String.valueOf(nextTempAddress);
        nextTempAddress++;
        return result;
    }

    private void pop(int num)
    {
        for (int i = 0; i < num; i++)
        {
            semanticStack.remove(semanticStack.size() - 1);
        }
    }

    private void push(String element)
    {
        semanticStack.add(element);
    }

    private String findVarAddress(String input) // TODO check if this is correct
    {
        Attribute attribute = findAttribute(input);
        if (attribute == null)
        {
            return null;
        }

        String varAddress = attribute.getVarAddress();
        if (varAddress == null)
        {
            String address = String.valueOf(nextVarAdderess);
            attribute.setVarAddress(address);
            nextVarAdderess++;
            return address;
        }

        return varAddress;
    }

    private Attribute findAttribute(String input)
    {
        for (int j = symbolTables.size() - 1; j <= 0; j--)
        {
            Attribute attribute = symbolTables.get(j).getSymbolTableEntry(input);

            if (attribute != null)
            {
                return attribute;
            }
        }

        return null;
    }

    private String getFromSSTop(int indexFromTop)
    {
        return semanticStack.get(semanticStack.size() - 1 - indexFromTop);
    }
}
