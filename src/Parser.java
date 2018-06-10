import javafx.util.Pair;
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


    public Parser(CodeGenerator codeGenerator, CompilerScanner scanner)
    {
        this.codeGenerator = codeGenerator;
        this.scanner = scanner;
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
        while (true)
        {
            Pair<String, String> token = scanner.getNextToken();
            String parseOperation = parseTable.get(getStackTop() + getLabel(token));

            if (parseOperation == null)
            {
                // TODO handle error with panic mode
            }

            if (doParseOperation(parseOperation, token))
            {
                break;
            }
        }
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
            codeGenerator.generateCode(lastRHSPart.getValue());
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
