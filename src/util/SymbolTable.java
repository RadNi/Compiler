package util;

import java.util.HashMap;

/**
 * Created by msi1 on 6/7/2018.
 */
public class SymbolTable
{
    private HashMap<String, Attribute> table = new HashMap<>();

    public Attribute getSymbolTableEntry(String entry)
    {
        return table.get(entry);
    }

    public void setSymbolTableEntry(String key, Attribute attribute)
    {
        this.table.put(key, attribute);
    }
}
