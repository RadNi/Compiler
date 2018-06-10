package util;

/**
 * Created by RadNi on 6/8/18.
 */
public class Attribute {
    private String type;
    private String rest;
    public Attribute(String type, String rest) {
        this.type = type;
        this.rest = rest;
    }

    public String getType() {
        return type;
    }

    public String getRest() {
        return rest;
    }

    public void setRest(String rest) {
        this.rest = rest;
    }
}
