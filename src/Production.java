import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by msi1 on 6/7/2018.
 */
public class Production
{
    private String LHS;
    private ArrayList<Pair<String, String>> RHS;

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
