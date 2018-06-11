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
    private int nextVarAddress = 1000;
    private int i = 1;
    private String mainAddress = "";
    private ArrayList<Integer> counters = new ArrayList<>();
    private ArrayList<ArrayList<String>> importantTemps = new ArrayList<>();
    private String stackPointer = "500";
    private String topStackPointer = "504";


    public CodeGenerator(ArrayList<SymbolTable> symbolTables, CompilerScanner scanner)
    {
        this.symbolTables = symbolTables;
        this.scanner = scanner;
        this.programBlock = new String[1000];
        this.semanticStack = new ArrayList<>();
        this.counters.add(0);
        this.importantTemps.add(new ArrayList<>());
    }

    public void generateCode(String actionSymbol, String input)
    {
        switch (actionSymbol)
        {
            case "#file_start":
                startFile();
                break;
            case "#main":
                jumpMain();
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
                insertFunction();
                break;
            case "#end_fun":
                functionEnd();
                break;
            case "#add_set_param":
                addAndSetParam(input);
                break;
            case "#override_to_array":
                addAndSetParam(input);
                break;
            case "#scope_start":
                symbolTables.add(new SymbolTable());
                break;
            case "#scope_end":
                symbolTables.remove(symbolTables.size() - 1);
                break;
            case "#pop":
                popTemporary();
                pop(1);
                break;
            case "#save":
                push(String.valueOf(i++));
                break;
            case "#jpf_save":
                jumpFalseAndSave();
                break;
            case "#jp":
                jump();
                break;
            case "#label":
                push(String.valueOf(i));
                break;
            case "#while":
                jumpOnWhile();
                break;
            case "#return_void":
                returnVoid();
                break;
            case "#return_int":
                returnInt();
                break;
            case "#assign":
                assign();
                break;
            case "#push_add":
                pushAddress();
                break;
            case "#push_final_address":
                pushArrayFinalAddress();
                break;
            case "#push_op_result":
                pushOperationResult();
                break;
            case "#call_start":
                startCall();
                break;
            case "#call_end":
                endCall();
                break;
            case "#push_num":
                pushNumber();
                break;
            case "#jp_to_func":
                jumpToFunction();
                break;
            case "#add_arg":
                addArgument();
                break;
            case "#pop_and_scope_start":
                pop(1);
                symbolTables.add(new SymbolTable());
                break;
        }
    }

    private void addArgument()
    {
        String argValue = getFromSSTop(0);
        popTemporary();
        pop(1);

        pushToMemoryStack(argValue);
    }

    private void jumpToFunction()
    {
        String functionName = getFromSSTop(0);
        pop(1);

        String functionAddress = findFunctionAddress(functionName);
        addToProgramBlock("jp", new String[]{functionAddress}, i);
        i++;
    }

    private String findFunctionAddress(String functionName)
    {
        Attribute attribute = findAttribute(functionName);
        return attribute.getFunctionAddress();
    }

    private void pushNumber()
    {
        String num = getFromSSTop(0);
        pop(1);

        String numAddress = getTemp();
        addToProgramBlock("assign", new String[]{"#" + num, numAddress}, i);
        i++;

        push(numAddress);
        importantTemps.get(importantTemps.size() - 1).add(numAddress);
    }

    private void endCall()
    {
        String returnValue = getTemp();
        addToProgramBlock("sub", new String[]{topStackPointer, "#8", returnValue}, i);
        i++;
//        String returnAddress = getTemp();
//        addToProgramBlock("sub", new String[]{topStackPointer, "#4", returnAddress}, i);
//        i++;

        addToProgramBlock("sub", new String[]{topStackPointer, "#8", stackPointer}, i);
        i++;
        addToProgramBlock("assign", new String[]{"@" + topStackPointer, topStackPointer}, i);
        i++;

        ArrayList<String> temps = importantTemps.get(importantTemps.size() - 1);
        for (String temp : temps)
        {
            String prevTempResult = popFromMemoryStack();
            addToProgramBlock("assign", new String[]{prevTempResult, temp}, i);
            i++;
        }

//        addToProgramBlock("jp", new String[]{returnAddress}, i);
//        i++;

        push(returnValue);
        importantTemps.get(importantTemps.size() - 1).add(returnValue);
    }

    private void startCall()
    {
        ArrayList<String> temps = importantTemps.get(importantTemps.size() - 1);

        for (String temp : temps)
        {
            pushToMemoryStack(temp);
        }

        addToProgramBlock("add", new String[]{stackPointer, "#8", stackPointer}, i);
        i++;
        addToProgramBlock("assign", new String[]{topStackPointer, "@" + stackPointer}, i);
        i++;
        addToProgramBlock("assign", new String[]{stackPointer, topStackPointer}, i);
        i++;
        addToProgramBlock("add", new String[]{stackPointer, "#4", stackPointer}, i);
        i++;

//        String returnAddressTemp = getTemp();
//        addToProgramBlock("sub", new String[]{topStackPointer, "#4", returnAddressTemp}, i);
//        i++;
//        addToProgramBlock("assign", new String[]{"#" + String.valueOf(i + 1), "@" + returnAddressTemp}, i);
//        i++;
    }

    private String popFromMemoryStack()
    {
        String temp = getTemp();
        addToProgramBlock("sub", new String[]{stackPointer, "#4"}, i);
        i++;
        addToProgramBlock("assign", new String[]{"@" + stackPointer, temp}, i);
        i++;

        return temp;
    }

    private void pushToMemoryStack(String temp)
    {
        addToProgramBlock("assign", new String[]{temp, "@" + stackPointer}, i);
        i++;
        addToProgramBlock("add", new String[]{stackPointer, "#4", stackPointer}, i);
        i++;
    }

    private void pushOperationResult()
    {
        String secondExpression = getFromSSTop(0);
        popTemporary();
        pop(1);

        String operation = getFromSSTop(0);
        pop(1);

        String firstExpression = getFromSSTop(0);
        popTemporary();
        pop(1);

        String resultAddress = getTemp();
        String instruction = null;

        switch (operation)
        {
            case "<":
                instruction = "lt";
                break;
            case "==":
                instruction = "eq";
                break;
            case "+":
                instruction = "add";
                break;
            case "-":
                instruction = "sub";
                break;
            case "*":
                instruction = "mult";
                break;
        }
        addToProgramBlock(instruction, new String[]{firstExpression, secondExpression, resultAddress}, i);
        i++;
        push(resultAddress);
        importantTemps.get(importantTemps.size() - 1).add(resultAddress);
    }

    private void pushArrayFinalAddress()
    {
        String arrayIndex = getFromSSTop(0);
        popTemporary();
        pop(1);

        String arrayAddress = getFromSSTop(0);
        popTemporary();
        pop(1);

        String result = getTemp();

        addToProgramBlock("add", new String[]{arrayAddress, arrayIndex, result}, i);
        i++;
        addToProgramBlock("add", new String[]{result, "#1", result}, i);
        i++;

//        addToProgramBlock("assign", new String[]{arrayAddress, result}, i);
//        i++;
//        addToProgramBlock("add", new String[]{result, arrayIndex, result}, i);
//        i++;

        push(result);
        importantTemps.get(importantTemps.size() - 1).add(result);
    }

    private void pushAddress()
    {
        String input = getFromSSTop(0);
        pop(1);
        String varNum = findVarAddress(input);
        String addressTemp = getTemp();
        addToProgramBlock("mult", new String[]{"#" + varNum, "#4", addressTemp}, i);
        i++;
        addToProgramBlock("add", new String[]{addressTemp, topStackPointer, addressTemp}, i);
        i++;

        push(addressTemp);
        importantTemps.get(importantTemps.size() - 1).add(addressTemp);
    }

    private void assign()
    {
        String expressionAddress = getFromSSTop(0);
        popTemporary();
        pop(1);

        String variableAddress = getFromSSTop(0);
        popTemporary();
        pop(1);

        addToProgramBlock("assign", new String[]{expressionAddress, variableAddress}, i);
        i++;
    }

    private void returnInt() // TODO check if this is correct
    {
        String returnValue = getFromSSTop(0);
        popTemporary();
        pop(1);

        String functionName = getFromSSTop(0);
        pop(1);

        String functionOutputType = getFromSSTop(0);
        pop(1);

//        String previousAddress = getFromSSTop(0);
//        pop(1);
//
//        String stackPointer = getFromSSTop(0);

        if (functionOutputType.equals("void"))
        {
            System.out.println("Error: function" + functionName + " shouldn't return anything.");
        }

        String retValAddress = getTemp();
        addToProgramBlock("assign", new String[]{topStackPointer, retValAddress}, i);
        i++;
        addToProgramBlock("sub", new String[]{retValAddress, "#8"}, i);
        i++;
        addToProgramBlock("assign", new String[]{returnValue, retValAddress}, i);
        i++;

        String returnAddress = getTemp();
        addToProgramBlock("assign", new String[]{topStackPointer, returnAddress}, i);
        i++;
        addToProgramBlock("sub", new String[]{returnAddress, "#4", returnAddress}, i);
        i++;
        addToProgramBlock("jp", new String[]{"@" + returnAddress}, i);
        i++;

//        addToProgramBlock("assign", new String[]{returnValue, "@" + stackPointer}, i);
//        i++;
//        addToProgramBlock("jp", new String[]{previousAddress}, i);
//        i++;
    }

    private void returnVoid()
    {
        String functionName = getFromSSTop(0);
        pop(1);

        String functionOutputType = getFromSSTop(0);
        pop(1);

//        String previousAddress = getFromSSTop(0);
//        pop(1);

        if (!functionOutputType.equals("void"))
        {
            System.out.println("Error: function " + functionName + " must return void");
        }

        String returnAddress = getTemp();
        addToProgramBlock("assign", new String[]{topStackPointer, returnAddress}, i);
        i++;
        addToProgramBlock("sub", new String[]{returnAddress, "#4", returnAddress}, i);
        i++;
        addToProgramBlock("jp", new String[]{"@" + returnAddress}, i);
        i++;
    }

    private void jumpOnWhile()
    {
        String pbAddress = getFromSSTop(0);
        pop(1);

        String condition = getFromSSTop(0);
        pop(1);

        String jumpAddress = getFromSSTop(0);
        pop(1);

        addToProgramBlock("jp", new String[]{jumpAddress}, i);
        i++;
        addToProgramBlock("jpf", new String[]{condition, String.valueOf(i)}, Integer.parseInt(pbAddress));
    }

    private void jump()
    {
        String pbAddress = getFromSSTop(0);
        pop(1);
        addToProgramBlock("jp", new String[]{String.valueOf(i)}, Integer.parseInt(pbAddress));
    }

    private void jumpFalseAndSave()
    {
        String pbAddress = getFromSSTop(0);
        pop(1);
        String condition = getFromSSTop(0);
        pop(1);

        addToProgramBlock("jpf", new String[]{condition, String.valueOf(i)}, Integer.parseInt(pbAddress));
        push(String.valueOf(i));
        i++;
    }

    private void overrideTypeToArray()
    {
        String input = getFromSSTop(0);
        String functionName = getFromSSTop(0);
        Attribute attribute = findAttribute(functionName);
        attribute.getParams().remove(attribute.getParams().size() - 1);
        attribute.addParam("array", input);
    }

    private void addAndSetParam(String input)
    {
        String paramType = getFromSSTop(0);
        if (paramType.equals("void"))
        {
            System.out.println("Error: Invalid type for parameter " + input);
        }
        pop(1);

        String functionName = getFromSSTop(0);
        Attribute attribute = findAttribute(functionName);
        attribute.addParam("int", input);

        findVarAddress(input);
        push(input);

//        String varNum = attribute.getVariableNumber();
//        String address = getTemp();
//        addToProgramBlock("mult", new String[]{"#" + varNum, "#4", address}, i);
//        i++;
//        addToProgramBlock("add", new String[]{address, topStackPointer, address}, i);
//        i++;


//        String stackPointer = getFromSSTop(3);
//        addToProgramBlock("sub", new String[]{stackPointer, "#1", stackPointer}, i);
//        i++;
//        String varAddress = findVarAddress(input);
//        addToProgramBlock("assign", new String[]{"@" + stackPointer, varAddress}, i);
//        i++;
    }

    private void functionEnd()
    {
        counters.remove(counters.size() - 1);
        importantTemps.remove(importantTemps.size() - 1);
    }

    private void insertFunction() // TODO handle output
    {
        String input = getFromSSTop(0);
        pop(1);

        if (!mainAddress.equals(""))
        {
            System.out.println("Error: main is not the last function");
        } else if (input.equals("main"))
        {
            mainAddress = String.valueOf(i);
        }
        Attribute attribute = findAttribute(input);
        attribute.setFunctionAddress(String.valueOf(i));
        attribute.setFunctionType(getFromSSTop(0));
        push(input);

//        addToProgramBlock("add", new String[]{stackPointer, "#8", stackPointer}, i);
//        i++;
//        addToProgramBlock("assign", new String[]{topStackPointer, "@" + stackPointer}, i);
//        i++;
//        addToProgramBlock("assign", new String[]{stackPointer, topStackPointer}, i);
//        i++;
//        addToProgramBlock("add", new String[]{stackPointer, "#4", stackPointer}, i);
//        i++;

        counters.add(0);
        importantTemps.add(new ArrayList<>());
        symbolTables.add(new SymbolTable());
    }

    private void insertArrayVariable()
    {
        int arraySize = Integer.parseInt(getFromSSTop(0));
//        String firstArrayIndexAddress = String.valueOf(nextVarAddress);
//        nextVarAddress += arraySize; // TODO check if this is correct
        int currentCounter = counters.get(counters.size() - 1);
        currentCounter += arraySize;
        counters.set(counters.size() - 1, currentCounter);
        pop(1);

        String idName = getFromSSTop(0);
        pop(1);

        String varType = getFromSSTop(0);
        pop(1);
        if (varType.equals("void"))
        {
            System.out.println("Error: Variable" + idName + "'s type is void");
        } else if (arraySize < 0)
        {
            System.out.println("Error: Array " + idName + "'s size is less than zero");
        }

        Attribute attribute = findAttribute(idName); // TODO check if attribute is null
        attribute.setVarType("array");
        attribute.setArraySize(arraySize);
        String arrayVarNum = attribute.getVariableNumber();
        String arrayAddress = getTemp();
        addToProgramBlock("mult", new String[]{"#" + arrayVarNum, "#4", arrayAddress}, i);
        i++;
        addToProgramBlock("add", new String[]{arrayAddress, topStackPointer, arrayAddress}, i);
        i++;
        addToProgramBlock("assign", new String[]{arrayAddress, "@" + arrayAddress}, i); // TODO check if correct
        i++;
        addToProgramBlock("add", new String[]{"@" + arrayAddress, "#4", "@" + arrayAddress}, i);
        i++;
    }

    private void insertNormalVariable()
    {
        String idName = getFromSSTop(0);
        pop(1);

        String varType = getFromSSTop(0);
        if (varType.equals("void"))
        {
            System.out.println("Error: Variable" + idName + "'s type is void");
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

    private void jumpMain()
    {
        String jumpAddress = getFromSSTop(0);
        if (jumpAddress.equals(""))
        {
            // TODO no main error
            System.out.println("Error: Function main doesn't exist");
        }
        addToProgramBlock("jp", new String[]{mainAddress}, Integer.parseInt(jumpAddress));
        pop(3);
    }

    private void startFile()
    {
//        String counterAdd = getTemp();
//        push(counterAdd);

        String spAdd = getTemp();
        push(spAdd);
        addToProgramBlock("assign", new String[]{"#400", spAdd}, i);
        i++;
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

        programBlock[index - 1] = instruction;
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

        String varAddress = attribute.getVariableNumber();
        if (varAddress == null)
        {
//            String address = String.valueOf(nextVarAddress);
//            attribute.setVarAddress(address);
//            nextVarAddress++;
            int currentCounter = counters.get(counters.size() - 1);
            attribute.setVariableNumber(String.valueOf(currentCounter));
            counters.set(counters.size() - 1, currentCounter + 1);
            return String.valueOf(currentCounter);
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

    private void popTemporary()
    {
        ArrayList<String> lastTemps = importantTemps.get(importantTemps.size() - 1);
        lastTemps.remove(lastTemps.size() - 1);
    }
}
