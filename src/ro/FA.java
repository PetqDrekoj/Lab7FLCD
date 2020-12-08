package ro;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class FA {

    private List<String> setOfStates;
    private List<String> setOfFinalStates;
    private List<String> alphabet;
    private String initialState;
    private List<ThreePair<String, String, String>> transitions;
    private boolean okay = false;


    public FA() {
        setOfStates = new ArrayList<>();
        setOfFinalStates = new ArrayList<>();
        alphabet = new ArrayList<>();
        transitions = new ArrayList<>();
    }


    public void start() {
        Scanner s = new Scanner(System.in);
        int choice = -1;
        while (choice == -1) {
            try {
                System.out.println("Please enter a file name:");
                String fileName = s.nextLine();
                readFromFile(fileName);
                choice = -2;
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }

        while (choice != 0) {
            System.out.println("0 - Exit");
            System.out.println("1 - Display set of states");
            System.out.println("2 - Display alphabet.");
            System.out.println("3 - Display transitions.");
            System.out.println("4 - Display set of final states.");
            System.out.println("5 - Test sequence.");
            try {
                choice = Integer.parseInt(s.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid option");
                choice = -1;
            }
            if (choice == 0) continue;
            else if (choice == 1) getSetOfStates();
            else if (choice == 2) getAlphabet();
            else if (choice == 3) getTransitions();
            else if (choice == 4) getSetOfFinalStates();
            else if (choice == 5) {
                System.out.println("Insert sequence");
                String sequence = s.nextLine();
                testSequence(sequence);
            } else System.out.println("Invalid option");
        }

    }

    public boolean testSequence(String sequence) {
        okay = false;
        transitions.sort((s1, s2) -> {
            if (s1.getA().compareTo(s2.getA()) == 0) return s1.getB().compareTo(s2.getB());
            else return s1.getA().compareTo(s2.getA());
        });
        for (int i = 1; i < transitions.size(); i++)
            if (transitions.get(i).getA().equals(transitions.get(i - 1).getA()) && transitions.get(i).getB().equals(transitions.get(i - 1).getB()) &&
                    !transitions.get(i).getC().equals(transitions.get(i - 1).getC())) {
                System.out.println("not dfa");
                okay = true;
                return false;
            }
        test(sequence, 0, initialState);
        //if(!okay) System.out.println("err");
        return okay;
    }

    private void test(String sequence, int index, String currentState) {
        if (!okay) {
            if (index == sequence.length() && setOfFinalStates.contains(currentState)) {
                //System.out.println("accepted");
                this.okay = true;
                return;
            }
            if (index == sequence.length()) return;
            for (ThreePair<String, String, String> p : transitions) {
                if (okay) return;
                if (p.getA().equals(currentState) && p.getB().equals(sequence.charAt(index) + ""))
                    test(sequence, index + 1, p.getC());
            }
        }
    }

    private void getSetOfFinalStates() {
        this.setOfFinalStates.forEach(s -> System.out.println(s.toString()));
    }

    private void getTransitions() {
        this.transitions.forEach(s -> System.out.println("(" + s.getA() + "," + s.getB() + ")->" + s.getC()));
    }

    private void getAlphabet() {
        this.alphabet.forEach(s -> System.out.println(s.toString()));
    }

    private void getSetOfStates() {
        this.setOfStates.forEach(s -> System.out.println(s.toString()));
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
                    setOfStates.addAll(Arrays.asList(s));
                }
                if (index == 2) {
                    String[] s = line.split(" ");
                    alphabet.addAll(Arrays.asList(s));
                }
                if (index == 3) {
                    initialState = line;
                }
                if (index == 4) {
                    String[] s = line.split(" ");
                    setOfFinalStates.addAll(Arrays.asList(s));
                }
                if (index > 4) {
                    String[] s = line.split("->");
                    String[] p1 = s[0].split(",");
                    transitions.add(new ThreePair<>(p1[0], p1[1], s[1]));
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
