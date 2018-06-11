package parsetable;

import util.Production;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by msi1 on 6/4/2018.
 */
public class Help
{
    private static HashMap<String, String> parseTable = new HashMap<>();
    private static ArrayList<Production> productions = new ArrayList<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException
    {
        Scanner in = new Scanner(System.in);

//        FileInputStream fis = new FileInputStream("productions.data");
        FileInputStream fis = new FileInputStream("pt.data");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ParseTableData parseTableData = (ParseTableData) ois.readObject();
//        productions = (ArrayList<Production>) ois.readObject();

        readDataFromFile(parseTableData);

        writeAllProductions(parseTableData);
//        System.out.println();
//        setProductions(in, parseTableData);
//        saveProductions();
    }

    private static void writeAllProductions(ParseTableData parseTableData) throws FileNotFoundException
    {
        HashMap<String, Character> nonTerminalsToId = parseTableData.getNonTerminalsToId();
        HashMap<String, Character> terminalsToId = parseTableData.getTerminalsToId();

        try(Scanner in = new Scanner(new File("productions.txt")))
        {
            while (in.hasNext())
            {
                String line = in.nextLine();
                String[] lineParts = line.split(" ");
                System.out.print(lineParts[0] + " -> ");
                for (int i = 3; i < lineParts.length; i++)
                {
                    if (nonTerminalsToId.containsKey(lineParts[i]))
                    {
                        System.out.print(nonTerminalsToId.get(lineParts[i]));
                    } else if (terminalsToId.containsKey(lineParts[i]))
                    {
                        System.out.print(terminalsToId.get(lineParts[i]));
                    }
                }
                System.out.println();
            }
        }
    }

    private static void readDataFromFile(ParseTableData parseTableData) throws FileNotFoundException
    {
        HashMap<Character, String> idToNonTerminals = parseTableData.getIdToNonTerminals();
        HashMap<String, Character> nonTerminalsToId = parseTableData.getNonTerminalsToId();

        idToNonTerminals.clear();
        nonTerminalsToId.clear();

        try(Scanner in = new Scanner(new File("productions.txt")))
        {
            while (in.hasNext())
            {
                String line = in.nextLine();
                String[] lineParts = line.split(" ");
                idToNonTerminals.put(lineParts[0].charAt(0), lineParts[1]);
                nonTerminalsToId.put(lineParts[1], lineParts[0].charAt(0));
            }
        }
        System.out.println();
        saveParseTableData(parseTableData);
    }

    private static void saveProductions() throws IOException
    {
        FileOutputStream fos = new FileOutputStream("productions.data");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(productions);
    }

    private static void setProductions(Scanner in, ParseTableData parseTableData)
    {
        while (true)
        {
            String lhs = in.next();

            if (lhs.equals("exit"))
            {
                return;
            } else if (lhs.equals("remove"))
            {
                productions.remove(productions.size() - 1);
                continue;
            }

            String rhs = in.next();

            productions.add(new Production(lhs.charAt(0), rhs, parseTableData));
        }
    }

    private static void saveParseTable() throws IOException
    {
        FileOutputStream fos = new FileOutputStream("parse_table.data");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(parseTable);
    }

    private static void setParseTable(Scanner in, ParseTableData parseTableData)
    {
        HashMap<Character, String> idToNonTerminals = parseTableData.getIdToNonTerminals();
        HashMap<Character, String> idToTerminals = parseTableData.getIdToTerminals();

        while (true)
        {
            String num = in.next();

            if (num.equals("exit"))
            {
                return;
            } else if (num.equals("remove"))
            {
                parseTable.remove(in.next());
                continue;
            }

            Character labelID = in.next().charAt(0);
            String label;
            if (idToNonTerminals.containsKey(labelID))
            {
                label = idToNonTerminals.get(labelID);
            } else if (idToTerminals.containsKey(labelID))
            {
                label = idToTerminals.get(labelID);
            } else if (labelID == '$')
            {
                label = "$";
            } else
            {
                System.out.println("Invalid input, try again.");
                continue;
            }

            String key = num + label;
            String value = in.next();
            parseTable.put(key, value);
        }
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
