import javafx.util.Pair;

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
    private Node rootNode;


    public Parser(SymbolTable symbolTable, CompilerScanner scanner)
    {
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
            String parseOperation = parseTable.get(getStackTop() + token.getKey());

            if (parseOperation == null)
            {
                // TODO handle error
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

    private boolean reduceFromStack(String reduceNumber, Pair<String, String> inputToken)
    {
        Production production = productions.get(Integer.parseInt(reduceNumber));
        Pair<String, String> stackElement = new Pair<>("non_terminal", production.getLHS());
        Node newNode = reduceToNewNode(production, inputToken);

        return false;
    }

    private Node reduceToNewNode(Production production, Pair<String, String> inputToken)
    {
        ArrayList<Pair<String, String>> popedElements = popElements(production.size());
        ArrayList<Pair<String, String>> productionRHS = new ArrayList<>();
        productionRHS.addAll(production.getRHS());

        Node resultNode = new Node(production.getLHS());
        ArrayList<Node> resultNodeChildren = resultNode.getChildren();

        for (int i = 0; i < popedElements.size(); i++)
        {
            addSymbolChildren(resultNode, resultNodeChildren, productionRHS);

        }
    }

    private void addSymbolChildren(Node parent, ArrayList<Node> parentNodeChildren,
                                   ArrayList<Pair<String, String>> productionRHS)
    {
        for (int i = 0; i < productionRHS.size();)
        {
            Pair<String, String> productionRHSElement = productionRHS.get(i);

            if (!productionRHSElement.getKey().equals("action_symbol"))
            {
                return;
            }

            parentNodeChildren.add(new Node(parent, productionRHSElement.getKey(), productionRHSElement.getValue()));
            productionRHS.remove(productionRHSElement);
        }
    }

    private ArrayList<Pair<String, String>> popElements(int size)
    {
        ArrayList<Pair<String, String>> popedElements = new ArrayList<>();

        for (int i = 0; i < size; i++)
        {
            parseStack.remove(parseStack.size() - 1);
            popedElements.add(parseStack.remove(parseStack.size() - 1));
        }

        return popedElements;
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
