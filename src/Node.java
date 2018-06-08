import java.util.ArrayList;

/**
 * Created by msi1 on 6/7/2018.
 */
public class Node
{
    private Node parent;
    private ArrayList<Node> children; // Order is important
    private String value;
    private String nodeType;

    public Node(String value)
    {
        this.value = value;
        this.nodeType = "non_terminal";
        this.children = new ArrayList<>();
    }

    public Node(Node parent, String nodeType, String value)
    {
        this.parent = parent;
        this.value = value;
        this.nodeType = nodeType;
        this.children = new ArrayList<>();
    }


    public Node getParent()
    {
        return parent;
    }

    public ArrayList<Node> getChildren()
    {
        return children;
    }

    public void setChildren(ArrayList<Node> children)
    {
        this.children = children;
    }

    public String getValue()
    {
        return value;
    }

    public String getNodeType()
    {
        return nodeType;
    }
}
