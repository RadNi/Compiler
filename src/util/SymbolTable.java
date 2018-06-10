package util;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by msi1 on 6/7/2018.
 */
public class SymbolTable
{
    private HashMap<String, Attribute> table = new HashMap<>();
    public Pair<String, Attribute> getSymbolTableEntry(Pair<String, String> entry) {
        if (table.containsKey(entry.getKey())) {
            return new Pair<>(entry.getKey(), table.get(entry.getKey()));
        }
        return null;
    }
    public void setSymbolTableEntry(String key, Attribute attribute) {
        this.table.put(key, attribute);
    }
}
