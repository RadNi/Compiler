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
    private HashMap<Pair<String, String>, Node> nodesMap;
    private Node rootNode;


    public Parser(SymbolTable symbolTable, CompilerScanner scanner)
    {
        this.scanner = scanner;
        this.nodesMap = new HashMap<>();
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

    /**
     *
     * @param reduceNumber Production number
     * @param inputToken The Input Token
     * @return true if reached acc
     */
    private boolean reduceFromStack(String reduceNumber, Pair<String, String> inputToken)
    {
        Production production = productions.get(Integer.parseInt(reduceNumber));
        this.rootNode = reduceToNewNode(production);
        Pair<String, String> stackElement = new Pair<>("non_terminal", production.getLHS());
        String topNum = getStackTop();
        String newNum = parseTable.get(topNum + stackElement.getValue());

        parseStack.add(stackElement);
        parseStack.add(new Pair<>("stack_number", newNum));

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

    /**
     * Pop items from stack and create a new node for the tree
     * @param production The production related to the reduce operation
     * @return The new node of the tree
     */
    private Node reduceToNewNode(Production production)
    {
        ArrayList<Pair<String, String>> poppedElements = popElements(production.size());
        ArrayList<Pair<String, String>> productionRHS = new ArrayList<>();
        productionRHS.addAll(production.getRHS());

        Node resultNode = new Node(production.getLHS());
        ArrayList<Node> resultNodeChildren = resultNode.getChildren();

        for (Pair<String, String> poppedElement : poppedElements)
        {
            addSymbolChildren(resultNode, resultNodeChildren, productionRHS);

            if (nodesMap.containsKey(poppedElement))
            {
                resultNodeChildren.add(nodesMap.get(poppedElement));
            } else
            {
                resultNodeChildren.add(new Node(resultNode, poppedElement.getKey(), poppedElement.getValue()));
            }
        }

        resultNode.setChildren(resultNodeChildren);
        return resultNode;
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
