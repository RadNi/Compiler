package util;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by msi1 on 6/7/2018.
 */
public class SymbolTable
{
    private HashMap<String, Pair<String, Attribute>> table = new HashMap<>();
    public Pair<String, Pair<String, Attribute>> getSymbolTableEntry(Pair<String, String> entry) {
        if (table.containsKey(entry.getKey())) {
            return new Pair<>(entry.getKey(), table.get(entry.getKey()));
        }
        return null;
    }
    public void setSymbolTableEntry(Pair<String, String> entry, Attribute attribute) {
        this.table.put(entry.getKey(), new Pair<>(entry.getValue(), attribute));
    }
}
