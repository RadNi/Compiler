package util;

import parsetable.ParseTableData;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by msi1 on 6/7/2018.
 */
public class Production implements Serializable
{
    private String LHS;
    private ArrayList<Pair<String, String>> RHS;

    public Production(char lhsID, String rhsIDs, ParseTableData parseTableData)
    {
        this.RHS = new ArrayList<>();
        HashMap<Character, String> idToTerminals = parseTableData.getIdToTerminals();
        HashMap<Character, String> idToNonTerminals = parseTableData.getIdToNonTerminals();
        this.LHS = idToNonTerminals.get(lhsID);

        setRHS(rhsIDs, idToTerminals, idToNonTerminals);
    }

    private void setRHS(String rhsIDs, HashMap<Character, String> idToTerminals, HashMap<Character, String> idToNonTerminals)
    {
        for (int i = 0; i < rhsIDs.length(); i++)
        {
            char currentId = rhsIDs.charAt(i);
            if (idToNonTerminals.containsKey(currentId))
            {
                RHS.add(new Pair<>("non_terminal", idToNonTerminals.get(currentId)));
            } else if (idToTerminals.containsKey(currentId))
            {
                RHS.add(new Pair<>(idToTerminals.get(currentId), idToTerminals.get(currentId)));
            }
        }
    }

    public String getLHS()
    {
        return LHS;
    }

    public ArrayList<Pair<String, String>> getRHS()
    {
        return RHS;
    }

    public int size()
    {
        int counter = 0;

        for (Pair<String, String> RHSElement : RHS)
        {
            if (RHSElement.getKey().equals("action_symbol"))
            {
                continue;
            }

            counter++;
        }

        return counter;
    }
}
