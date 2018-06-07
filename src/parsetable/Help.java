package parsetable;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by msi1 on 6/4/2018.
 */
public class Help
{
    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        Scanner in = new Scanner(System.in);
        ParseTableData parseTableData;

        FileInputStream fis = new FileInputStream("pt.data");
        ObjectInputStream ois = new ObjectInputStream(fis);
        parseTableData = (ParseTableData) ois.readObject();

//        System.out.println("Enter Non-Terminals: ");
//        setNonTerminals(in, parseTableData);
//        System.out.println("Enter Terminals: ");
//        setTerminals(in, parseTableData);
//
//        saveParseTableData(parseTableData);
        System.out.println("Enter something to get it's ID: ");
        showProductions(in, parseTableData);
//        startGettingIds(in, parseTableData);
    }

    private static void showProductions(Scanner in, ParseTableData parseTableData)
    {
        HashMap<Character, String> idToTerminals = parseTableData.getIdToTerminals();
        HashMap<Character, String> idToNonTerminals = parseTableData.getIdToNonTerminals();

        while (true)
        {
            String input = in.next();
            if (input.equals("exit"))
            {
                return;
            }

            System.out.print(idToNonTerminals.get(input.charAt(0)) + " to ");
            for (int i = 1; i < input.length(); i++)
            {
                char thisChar = input.charAt(i);
                if (thisChar > 'Z' + 2)
                   System.out.print(idToTerminals.get(thisChar) + " ");
                else
                    System.out.print(idToNonTerminals.get(thisChar) + " ");
            }
            System.out.println();
        }
    }

    private static void startGettingIds(Scanner in, ParseTableData parseTableData)
    {
        HashMap<String, Character> nonTerminalsToId = parseTableData.getNonTerminalsToId();
        HashMap<String, Character> terminalsToId = parseTableData.getTerminalsToId();

        while (true)
        {
            String type = in.next();
            if (type.equals("exit"))
            {
                return;
            }

            String name = in.next();
            Character result = type.equals("terminal") ? terminalsToId.get(name) : nonTerminalsToId.get(name);
            System.out.println(result);
        }
    }

    private static void saveParseTableData(ParseTableData parseTableData)
    {
        try(FileOutputStream fos = new FileOutputStream("pt.data");
                ObjectOutputStream oos = new ObjectOutputStream(fos))
        {
            oos.writeObject(parseTableData);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static void setTerminals(Scanner in, ParseTableData parseTableData)
    {
        char id = (char) ('a' + parseTableData.getTerminalsToId().size());

        while (true)
        {
            String input = in.next();
            if (input.equals("exit"))
            {
                return;
            }

            parseTableData.addTerminal(input, id);
            id++;
        }
    }

    private static void setNonTerminals(Scanner in, ParseTableData parseTableData)
    {
        char id = (char) ('A' + parseTableData.getNonTerminalsToId().size());

        while (true)
        {
            String input = in.next();
            if (input.equals("exit"))
            {
                return;
            }

            parseTableData.addNonTerminal(input, id);
            id++;
        }
    }
}
