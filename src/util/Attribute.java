package util;

import java.util.ArrayList;

/**
 * Created by RadNi on 6/8/18.
 */
public class Attribute { // I blame RadNi for this mess
    private String type;

    private String varType;
    private String varAddress;

    private String functionType;
    private String functionAddress;
    private ArrayList<String> paramTypes;

    public Attribute(String type) {
        this.type = type;
        this.paramTypes = new ArrayList<>();
    }

    public String getType() {
        return type;
    }
}
