package util;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by RadNi on 6/8/18.
 */
public class Attribute { // I blame RadNi for this mess
    private String type;

    private String varType;
    private String varAddress;
    private int arraySize;

    private String functionType;
    private String functionAddress;
    private ArrayList<Pair<String, String>> params;

    public Attribute(String type) {
        this.type = type;
        this.varType = null;
        this.varAddress = null;
        this.arraySize = -1;
        this.functionType = null;
        this.functionAddress = null;
        this.params = new ArrayList<>();
    }

    public void addParam(String type, String name)
    {
        params.add(new Pair<>(type, name));
    }

    public String getType() {
        return type;
    }

    public String getVarType()
    {
        return varType;
    }

    public void setVarType(String varType)
    {
        this.varType = varType;
    }

    public String getVarAddress()
    {
        return varAddress;
    }

    public void setVarAddress(String varAddress)
    {
        this.varAddress = varAddress;
    }

    public String getFunctionType()
    {
        return functionType;
    }

    public void setFunctionType(String functionType)
    {
        this.functionType = functionType;
    }

    public String getFunctionAddress()
    {
        return functionAddress;
    }

    public void setFunctionAddress(String functionAddress)
    {
        this.functionAddress = functionAddress;
    }

    public ArrayList<Pair<String, String>> getParams()
    {
        return params;
    }

    public void setParams(ArrayList<Pair<String, String>> params)
    {
        this.params = params;
    }

    public int getArraySize()
    {
        return arraySize;
    }

    public void setArraySize(int arraySize)
    {
        this.arraySize = arraySize;
    }
}
