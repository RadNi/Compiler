package parsetable;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by msi1 on 6/5/2018.
 */
public class ParseTableData implements Serializable
{
    private HashMap<Character, String> idToNonTerminals = new HashMap<>();
    private HashMap<String, Character> nonTerminalsToId = new HashMap<>();
    private HashMap<Character, String> idToTerminals = new HashMap<>();
    private HashMap<String, Character> terminalsToId = new HashMap<>();

    public void addTerminal(String terminal, char id)
    {
        if (terminalsToId.containsKey(terminal))
        {
            return;
        }

        idToTerminals.put(id, terminal);
        terminalsToId.put(terminal, id);
    }

    public void addNonTerminal(String nonTerminal, char id)
    {
        if (nonTerminalsToId.containsKey(nonTerminal))
        {
            return;
        }

        idToNonTerminals.put(id, nonTerminal);
        nonTerminalsToId.put(nonTerminal, id);
    }

    public HashMap<Character, String> getIdToNonTerminals()
    {
        return idToNonTerminals;
    }

    public HashMap<String, Character> getNonTerminalsToId()
    {
        return nonTerminalsToId;
    }

    public HashMap<Character, String> getIdToTerminals()
    {
        return idToTerminals;
    }

    public HashMap<String, Character> getTerminalsToId()
    {
        return terminalsToId;
    }
}
