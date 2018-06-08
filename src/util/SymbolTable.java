package util;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by msi1 on 6/7/2018.
 */
public class SymbolTable
{
    private HashMap<Pair<String, String>, Attribute> table;
    public Pair<Pair<String, String>, Attribute> getSymbolTableEntry(Pair<String, String> entry) {
        if (table.containsKey(entry)) {
            return new Pair<>(entry, table.get(entry));
        }
        return null;
    }
    public void setSymbolTableEntry(Pair<String, String> entry, Attribute attribute) {
        this.table.put(entry, attribute);
    }
}
