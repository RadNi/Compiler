package util;

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
    private ArrayList<String> paramTypes;

    public Attribute(String type) {
        this.type = type;
        this.varType = null;
        this.varAddress = null;
        this.functionType = null;
        this.functionAddress = null;
        this.paramTypes = new ArrayList<>();
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

    public ArrayList<String> getParamTypes()
    {
        return paramTypes;
    }

    public void setParamTypes(ArrayList<String> paramTypes)
    {
        this.paramTypes = paramTypes;
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
