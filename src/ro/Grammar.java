package ro;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Grammar {
    private List<String> setOfNonterminals;
    private List<String> setOfTerminals;
    private String initialState;
    private List<MyPair<String, List<String>>> productions;

    public Grammar(String filename) {
        setOfNonterminals = new ArrayList<>();
        setOfTerminals = new ArrayList<>();
        productions = new ArrayList<>();
        initialState = "";
        readFromFile(filename);
    }


    public void start() {
        Scanner s = new Scanner(System.in);
        int choice = -1;

        while (choice != 0) {
            System.out.println("0 - Exit");
            System.out.println("1 - Display set of nonterminals");
            System.out.println("2 - Display set of terminals.");
            System.out.println("3 - Display productions.");
            System.out.println("4 - Display productions for a given nonterminal.");
            try {
                choice = Integer.parseInt(s.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid option");
                choice = -1;
            }
            if (choice == 0) continue;
            else if (choice == 1) getSetOfNonterminals().forEach(System.out::println);
            else if (choice == 2) getSetOfTerminals().forEach(System.out::println);
            else if (choice == 3)
                getProductions().forEach(p -> System.out.println(p.getFirst() + "->" + p.getSecond().stream().map(c -> c + " ").collect(Collectors.joining())));
            else if (choice == 4) {
                System.out.println("Insert nonterminal");
                String nonterminal = s.nextLine();
                getProductionsOfNonterminal(nonterminal).forEach(p -> System.out.println(p.getFirst() + "->" + p.getSecond().stream().map(c -> c + " ").collect(Collectors.joining())));
            } else System.out.println("Invalid option");
        }
    }

    public List<MyPair<String, List<String>>> getProductionsOfNonterminal(String nonterminal) {
        List<MyPair<String, List<String>>> resultList = new ArrayList<>();
        for (MyPair pair : this.productions) {
            if (pair.getFirst().equals(nonterminal))
                resultList.add(pair);
        }
        return resultList;
    }

    public List<String> getSetOfNonterminals() {
        return setOfNonterminals;
    }

    public List<String> getSetOfTerminals() {
        return setOfTerminals;
    }

    public String getInitialState() {
        return initialState;
    }

    public List<MyPair<String, List<String>>> getProductions() {
        return this.productions;
    }

    public void readFromFile(String filename) {
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            int index = 1;
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                if (index == 1) {
                    String[] s = line.split(" ");
                    setOfNonterminals.addAll(Arrays.asList(s));
                }
                if (index == 2) {
                    String[] s = line.split(" ");
                    setOfTerminals.addAll(Arrays.asList(s));
                }
                if (index == 3) {
                    initialState = line;
                }
                if (index > 3) {
                    String[] s = line.split("->");
                    List<String> left = new ArrayList<String>(Arrays.asList(s[1].split(" ")));
                    productions.add(new MyPair<>(s[0], left));
                }
                index++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
