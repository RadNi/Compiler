import javafx.util.Pair;
import util.ErrorHandler;
import util.Node;
import util.Production;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by msi1 on 6/7/2018.
 */
public class Parser
{
    private ArrayList<Production> productions;
    private HashMap<String, String> parseTable;
    private CompilerScanner scanner;
    private ArrayList<Pair<String, String>> parseStack;
    private HashMap<Pair<String, String>, Node> nodesMap;
    private CodeGenerator codeGenerator;
    private ErrorHandler errorHandler;


    public Parser(CodeGenerator codeGenerator, CompilerScanner scanner, ErrorHandler errorHandler)
    {
        this.codeGenerator = codeGenerator;
        this.scanner = scanner;
        this.errorHandler = errorHandler;
        initStack();
        // TODO init parseTable
        // TODO init productions
    }

    private void initStack()
    {
        parseStack = new ArrayList<>();
        parseStack.add(new Pair<>("stack_num", "0"));
    }

    public void parse()
    {
        while (true) {
            Pair<String, String> token = scanner.getNextToken();
            String parseOperation = parseTable.get(getStackTop() + getLabel(token));

            if (parseOperation == null) {
                // TODO handle error with panic mode
                ArrayList<String>error = new ArrayList<>();
                int lineNumber = scanner.getEntryInSymbolTables(token.getValue()).getLineNumber();
                int stateNumber=0;
                while (true) {
                    stateNumber = giveStateNumberFromTopOfStack();
                    if (haseEntry(stateNumber) == null)
                        break;
                }
                if (haseEntry(stateNumber) != null) {
                    String label = haseEntry(stateNumber);
                    while (!follow(label).contains(token.getValue())) {
                        token = scanner.getNextToken();
                        error.add("token " + token.getValue()+ " removed from input to continue parsing");
                    }
                }
                errorHandler.addError(error, Integer.toString(lineNumber), "Unexpected Token");
                parseOperation = parseTable.get(getStackTop() + getLabel(token));
            }
            // now you can continue parsing with token and stack

            if (doParseOperation(parseOperation, token))
            {
                break;
            }
        }
    }

    private int giveStateNumberFromTopOfStack() {   //TODO  please return state number of Top of the stack !!!
        return 0;
    }

    private ArrayList<String> follow(String label) {
        //  TODO    need follow of label
        return null;
    }

    private String haseEntry(int stateNumber) {
        ArrayList<String>labels = getAllValidLables();
        for (int i = 0; i < labels.size(); i++) {
            if (parseStack.contains(Integer.toString(stateNumber)+labels.get(i))) {
                return labels.get(i);
            }
        }
        return null;
    }

    private ArrayList<String> getAllValidLables() {
        //  TODO this method should return all Valid column name in GOTO table
        return null;
    }


    private boolean doParseOperation(String parseOperation, Pair<String, String> inputToken)
    {
        switch (parseOperation.charAt(0))
        {
            case 's':
                shiftToStack(parseOperation.substring(1), inputToken);
                return false;
            case 'r':
                return reduceFromStack(parseOperation.substring(1), inputToken);
            case 'a':
                return true;
        }

        return false;
    }

    /**
     *
     * @param reduceNumber util.Production number
     * @param inputToken The Input Token
     * @return true if reached acc
     */
    private boolean reduceFromStack(String reduceNumber, Pair<String, String> inputToken)
    {
        Production production = productions.get(Integer.parseInt(reduceNumber));
        popElements(production.size());
        Pair<String, String> stackElement = new Pair<>("non_terminal", production.getLHS());
        String topNum = getStackTop();
        String newNum = parseTable.get(topNum + stackElement.getValue());

        parseStack.add(stackElement);
        parseStack.add(new Pair<>("stack_number", newNum));

        Pair<String, String> lastRHSPart = production.getRHS().get(production.size() - 1);
        if (lastRHSPart.getKey().equals("action_symbol"))
        {
            codeGenerator.generateCode(lastRHSPart.getValue(), inputToken.getValue());
        }

        String parseOperation = parseTable.get(newNum + getLabel(inputToken));
        return doParseOperation(parseOperation, inputToken);
    }

    /**
     *
     * @param token An input token
     * @return The token's label on the parse table
     */
    private String getLabel(Pair<String, String> token)
    {
        if (token.getKey().equals("non_terminal"))
        {
            return token.getValue();
        }

        return token.getKey();
    }

    private ArrayList<Pair<String, String>> popElements(int size)
    {
        ArrayList<Pair<String, String>> poppedElements = new ArrayList<>();

        for (int i = 0; i < size; i++)
        {
            parseStack.remove(parseStack.size() - 1);
            poppedElements.add(parseStack.remove(parseStack.size() - 1));
        }

        return poppedElements;
    }

    private void shiftToStack(String shiftNumber, Pair<String, String> inputToken)
    {
        parseStack.add(inputToken);
        parseStack.add(new Pair<>("stack_num", shiftNumber));
    }

    private String getStackTop()
    {
        return parseStack.get(parseStack.size() - 1).getValue();
    }
}
