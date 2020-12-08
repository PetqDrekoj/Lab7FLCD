package ro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MyScanner {

    private List<String> tokens;
    private List<MyPair<MyPair<Integer, Integer>, String>> PIF;
    private SymbolTable symbolTable;
    private FA FAid;
    private FA FAconst;

    public MyScanner() {
        tokens = new ArrayList<>();
        PIF = new ArrayList<>();
        symbolTable = new SymbolTable();
        FAid = new FA();
        FAconst = new FA();
        FAid.readFromFile("FAid.in");
        FAconst.readFromFile("FAconst.in");
        this.getTokens();
    }

    private void getTokens() {
        /*
            input:none
            preconditions: a file named token.in must be present in resources or in src folder
            postconditions: creates a list of tokens taken from file token.in
            output:none
         */
        try {
            File file = new File("token.in");
            Scanner fileDescriptor = new Scanner(file);
            fileDescriptor.nextLine(); // 0 - identifiers
            fileDescriptor.nextLine(); // 1 - constants
            while (fileDescriptor.hasNextLine()) {
                String line = fileDescriptor.nextLine();
                String[] split_strings = line.split(" - ");
                String token = split_strings[split_strings.length - 1].strip();
                if (token.equals("space")) tokens.add(" ");
                else tokens.add(token);
            }
        } catch (FileNotFoundException e) {
            System.out.println("token.in was not found");
        }
    }

    public void Start() {
        /*
            this function starts calling the other functions, it is the main function
            it will wait for paramters given from the console, 0 to exit, 1 for scanning and then the name of the file
            input:none
            preconditions: none
            postconditions: none
            output:none
         */
        int choice = 2;
        while (choice != 0) {
            System.out.print("0. Exit\n");
            System.out.print("1. Enter the name of the file.extension\n");
            Scanner scanner = new Scanner(System.in);
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            }
            if (choice == 1) {
                String filename = "none";
                if (scanner.hasNextLine()) filename = scanner.nextLine();
                Scan(filename);
            }
        }

    }

    public void Scan(String filename) {
        /*
            input: filename : string
            preconditions: the file that has the name filename must be present and must contain a program
            postconditions: creates the PIF and ST if all goes well
            output:none
            throws: Runtime Exception if error found in program, with the line and atom that we're not correct.
         */
        try {
            File file = new File(filename);
            Scanner fileDescriptor = new Scanner(file);
            int linenumber = 0;

            while (fileDescriptor.hasNextLine()) {
                String line = fileDescriptor.nextLine();
                linenumber++;
                List<String> oldAtoms = new ArrayList<>();
                oldAtoms.add(line);
                List<String> newAtoms = new ArrayList<>();

                for (String token : tokens) {
                    if (line.contains(token)) {
                        for (int i = 0; i < oldAtoms.size(); i++) {
                            if (oldAtoms.get(i).contains(token)) {

                                // if I find a string constant e.g. "abc"

                                if (token.equals("\"")) {
                                    String secondString = oldAtoms.get(i);
                                    if (!secondString.matches(".*\"[A-Za-z0-9., ]*\".*"))
                                        throw new RuntimeException("lexical error at line " + linenumber + " caused by " + oldAtoms.get(i) + ". The string is not closed.");
                                    while (secondString.matches(".*\"[A-Za-z0-9., ]*\".*")) {
                                        int firstPosition = secondString.indexOf("\"");
                                        int secondPosition = secondString.indexOf("\"", firstPosition + 1);
                                        String newString = secondString.substring(firstPosition, secondPosition + 1);
                                        List<String> stringList = Arrays.asList(secondString.split("\"[A-Za-z0-9., ]*\"", 2));
                                        if (!stringList.get(0).equals("") && !stringList.get(0).equals(" "))
                                            newAtoms.add(stringList.get(0));
                                        newAtoms.add(newString);
                                        secondString = stringList.get(1);
                                    }
                                    if (!secondString.equals("")) newAtoms.add(secondString);
                                }


                                // if I find a char constant e.g. 'a'


                                else if (token.equals("'")) {
                                    String secondString = oldAtoms.get(i);
                                    if (!secondString.matches(".*'[A-Za-z0-9., ]'.*"))
                                        throw new RuntimeException("lexical error at line " + linenumber + " caused by " + oldAtoms.get(i) + ". Error character constant.");
                                    while (secondString.matches(".*'[A-Za-z0-9., ]'.*")) {
                                        int firstPosition = secondString.indexOf("'");
                                        int secondPosition = secondString.indexOf("'", firstPosition+1);
                                        String newString = secondString.substring(firstPosition, secondPosition + 1);
                                        List<String> stringList = Arrays.asList(secondString.split("'[A-Za-z0-9., ]'", 2));
                                        if (!stringList.get(0).equals("") && !stringList.get(0).equals(" "))
                                            newAtoms.add(stringList.get(0));
                                        newAtoms.add(newString);
                                        secondString = stringList.get(1);
                                    }
                                    if (!secondString.equals("")) newAtoms.add(secondString);
                                }

                                /// dealing with operators that are part of regex and need to deactivate them

                                else if (token.equals("(") || token.equals(")") || token.equals("{") || token.equals("}") || token.equals("+") || token.equals("]") || token.equals("[") || token.equals("*")) {
                                    String secondString = oldAtoms.get(i);
                                    while (secondString.contains(token)) {
                                        if (secondString.equals(token)) {
                                            newAtoms.add(secondString);
                                            secondString = "";
                                        } else {
                                            List<String> stringList = Arrays.asList(secondString.split("\\" + token, 2));
                                            if (!stringList.get(0).equals("")) newAtoms.add(stringList.get(0));
                                            newAtoms.add(token);
                                            secondString = stringList.get(1);
                                        }
                                    }
                                    if (!secondString.equals("")) newAtoms.add(secondString);
                                }


                                else {

                                    String secondString = oldAtoms.get(i);

                                    // we dont split by space a constant string or char
                                    if ((secondString.contains("\"") || secondString.contains("'")) && token.equals(" ")) {
                                        newAtoms.add(secondString);
                                        continue;
                                    }

                                    // we don't split by = if there is <= >= == !=
                                    if (token.equals("=")) {
                                        int i1 = secondString.indexOf("=");
                                        if (i1+1 < secondString.length() && secondString.charAt(i1 + 1) == '=') {
                                            newAtoms.add(secondString.substring(0, i1 + 2));
                                            secondString = secondString.substring(i1 + 2, secondString.length());
                                        }
                                        if (i1 > 0 && (secondString.charAt(i1 - 1) == '<' || secondString.charAt(i1 - 1) == '>' || secondString.charAt(i1 - 1) == '!')) {
                                            newAtoms.add(secondString.substring(0, i1 + 1));
                                            secondString = secondString.substring(i1 + 1, secondString.length());
                                        }
                                    }
                                    while (secondString.contains(token)) {
                                        if (secondString.equals(token)) {
                                            newAtoms.add(secondString);
                                            secondString = "";
                                        } else {
                                            List<String> stringList = Arrays.asList(secondString.split(token, 2));

                                            if (!stringList.get(0).equals("") && !stringList.get(0).equals(" "))
                                                newAtoms.add(stringList.get(0));
                                            if (!token.equals(" ")) newAtoms.add(token);
                                            secondString = stringList.get(1);
                                        }
                                    }
                                    if (!secondString.equals("") && !secondString.equals(" "))
                                        newAtoms.add(secondString);
                                }
                            } else if (!oldAtoms.get(i).equals(" ")) newAtoms.add(oldAtoms.get(i));

                        }
                        oldAtoms = newAtoms;
                        newAtoms = new ArrayList<>();

                    }
                }
                for (int i = 0; i < oldAtoms.size(); i ++) {
                    String s = oldAtoms.get(i);
                    if (this.tokens.contains(s)) {
                        if(i>0 && (s.equals("-") || s.equals("+")) && (oldAtoms.get(i-1).contains("=") || oldAtoms.get(i-1).contains("[") || oldAtoms.get(i-1).equals("<") || oldAtoms.get(i-1).equals(">") || oldAtoms.get(i-1).equals("(")) && i+1 < oldAtoms.size())
                        {
                            if(!oldAtoms.get(i+1).matches("[1-9][0-9]*"))
                                throw new RuntimeException("lexical error at line " + linenumber + " caused by " + s+oldAtoms.get(i+1));
                            if(s.equals("-")) s = s.concat(oldAtoms.get(++i));
                            else s = oldAtoms.get(++i);
                            if (this.symbolTable.get(s) == null) this.symbolTable.put(s);
                            MyPair<Integer, Integer> position = this.symbolTable.getPositionOfKey(s);
                            this.PIF.add(new MyPair<>(position, s));
                        }
                        else
                            this.PIF.add(new MyPair<>(new MyPair<>(-1, -1), s));
                    }
                    else {
                        if (s.charAt(0) == '"' || s.charAt(0) == '\'' || FAid.testSequence(s) || FAconst.testSequence(s)) {
                            if (this.symbolTable.get(s) == null) this.symbolTable.put(s);
                            MyPair<Integer, Integer> position = this.symbolTable.getPositionOfKey(s);
                            if(FAid.testSequence(s)) this.PIF.add(new MyPair<>(position, "id"));
                            else this.PIF.add(new MyPair<>(position, "const"));
                        } else throw new RuntimeException("lexical error at line " + linenumber + " caused by " + s);
                    }

                }

            }
        } catch (FileNotFoundException e) {
            System.out.println(filename + " was not found.");
        }
        writePIFToFile();
        writeSTToFile();
        System.out.println("lexically correct");
    }

    private void writePIFToFile() {
        /*
            input:none
            preconditions: none
            postconditions: creates a file PIF.out that will contain the symbol table
            output:none
         */
        try {
            FileWriter myWriter = new FileWriter("PIF.out");
            String pif = this.PIF.stream().map(t -> t.toString() + "\n").collect(Collectors.joining());
            String st = this.symbolTable.getHashTable().stream().map(s -> {
                if (s != null)
                    return s.getKey().toString() + " la pozitia " + this.symbolTable.getPositionOfKey(s.getKey()).getFirst().toString() + " la randul " + this.symbolTable.getPositionOfKey(s.getKey()).getSecond().toString();
                else return "";
            }).collect(Collectors.joining());
            myWriter.write(pif);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void writeSTToFile() {
        /*
            input:none
            preconditions: none
            postconditions: creates a file ST.out that will contain the symbol table
            output:none
         */
        try {
            FileWriter myWriter = new FileWriter("ST.out");
            String pif = this.PIF.stream().map(t -> t.toString() + "\n").collect(Collectors.joining());
            String st = "Symbol table represented as hash table with separate chaining. Symbol + position\n";
            st += this.symbolTable.getHashTable().stream().map(s -> {
                if (s != null)
                    return s.getKey() + " " + this.symbolTable.getPositionOfKey(s.getKey()).toString() + "\n";
                else return "";
            }).collect(Collectors.joining());
            myWriter.write(st);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}
