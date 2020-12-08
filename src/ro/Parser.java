package ro;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Parser {
    private Grammar grammar;
    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;
    private Map<MyPair<String, String>, MyPair<List<String>, Integer>> LL1Table;
    private List<Node> tree;

    public Parser(Grammar grammar) {
        this.grammar = grammar;
        first = new HashMap<>();
        follow = new HashMap<>();
        LL1Table = new HashMap<>();
        this.tree = new ArrayList<>();
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void parse(String s) throws IOException {
        this.First();
        this.Follow();
//        System.out.println("First");
//        this.first.forEach((key, value) -> System.out.println(key + " | " + value));
//        System.out.println("\n");
//        System.out.println("Follow");
//        this.follow.forEach((key, value) -> System.out.println(key + " | " + value));
        System.out.println(this.constructLL1Table());
//        System.out.println("\n");
//        System.out.println("Table");
//        this.LL1Table.forEach((key, value) -> System.out.println(key + " | " + value));
        List<Integer> integerList = verifyString(Arrays.asList(s.split(" ")));
//        System.out.println("\n");
//        System.out.println(integerList);
        if (integerList != null) {
            constructTable(integerList);
            System.out.println("Crt. " + "  Node  " + " Father " + "  Sibling ");
            this.tree.forEach(z -> System.out.println(z.getCrt() + "   " + z.getNode() + "   " + z.getFather() + "   " + z.getRight_sibling()));
            this.writeToFile(this.tree, "out.txt");
        }
        //System.out.println("done");

    }

    private void writeToFile(List<Node> tree, String filename) throws IOException {
        File file = new File(filename);
        FileWriter writer = new FileWriter(file);
        String output = "Crt. " + "  Node  " + " Father " + "  Sibling \n";
        for (Node z : this.tree) {
            output += z.getCrt() + "       " + z.getNode() + "      " + z.getFather() + "        " + z.getRight_sibling() + "\n";
        }
        writer.write(output);
        writer.close();
    }

    public void constructTable(List<Integer> input) { //1 4 8 ...
        int crt = 1;
        int index = 0;
        Stack<Node> stack = new Stack<Node>();
        Node r = new Node(crt, this.grammar.getProductions().get(input.get(0) - 1).getFirst(), 0, 0);
        stack.push(r);
        this.tree.add(r);


        while (stack.size() > 0) {
            Node current_node = stack.pop();
            List<String> childs = this.grammar.getProductions().get(input.get(index) - 1).getSecond();
            for (int i = childs.size() - 1; i >= 0; i--) {
                crt++;
                Node n = null;
                if (i == childs.size() - 1) n = new Node(crt, childs.get(i), current_node.getCrt(), 0);
                else n = new Node(crt, childs.get(i), current_node.getCrt(), crt - 1);
                this.tree.add(n);
                if (!this.grammar.getSetOfTerminals().contains(n.getNode())) stack.push(n);
            }
            index++;
        }


    }

    public List<Integer> verifyString(List<String> w) {
        List<Integer> output = new ArrayList<>();
        Stack<String> inputStack = new Stack<>();
        Stack<String> workingStack = new Stack<>();

        //initialize input stack
        inputStack.push("$");
        for (int i = w.size() - 1; i >= 0; i--)
            inputStack.push(w.get(i));

        //initialize working stack
        workingStack.push("$");
        workingStack.push(this.grammar.getInitialState());
        int il = 0;
        boolean go = true;
        while (go) {

            String headInputStack = inputStack.peek();
            while (workingStack.peek().equals("epsilon")) workingStack.pop();
            String headWorkingStack = workingStack.peek();
            Map.Entry<MyPair<String, String>, MyPair<List<String>, Integer>> entry = this.LL1Table.entrySet().stream().filter(k -> k.getKey().getFirst().equals(headWorkingStack) && k.getKey().getSecond().equals(headInputStack)).findFirst().orElse(null);
            //we consider that the input is correct
            if (entry == null) {
                System.out.println("head input stack is" + headInputStack + "\n head working stack is " + headWorkingStack);
            }
            MyPair<List<String>, Integer> value;
            try {
                value = entry.getValue();
            } catch (Exception E) {
                value = null;
                System.out.println(E.toString());
            }
            if (value.getFirst().get(0).equals("pop")) {
                il++;
                workingStack.pop();
                inputStack.pop();
            } else if (value.getFirst().get(0).equals("acc")) {
                go = false;
                System.out.println("Sequence is accepted");
                return output;
            } else if (value.getFirst().get(0).equals("err")) {
                go = false;
                System.out.println("Sequence is not accepted");
                System.out.println("Error at element " + (il + 1));
                String beforeError = "";
                String afterError = "";
                if (il - 1 > 0) beforeError = w.get(il - 1);
                if (il + 1 < w.size()) afterError = w.get(il + 1);
                System.out.println("more exactly: " + beforeError + w.get(il) + afterError);
                System.out.println(headInputStack);
                System.out.println(inputStack);
                return null;
            } else {
                workingStack.pop();
                for (int i = value.getFirst().size() - 1; i >= 0; i--)
                    workingStack.push(value.getFirst().get(i));
                output.add(value.getSecond());
            }
        }

        return output;
    }

    private String constructLL1Table() {
        // initialization
        for (String row : this.grammar.getSetOfTerminals()) {
            for (String column : this.grammar.getSetOfTerminals()) {
                if (!(row.equals("epsilon") || column.equals("epsilon"))) {
                    if (row.equals(column))
                        LL1Table.put(new MyPair<String, String>(row, column), new MyPair<>(new ArrayList<>(Collections.singletonList("pop")), 0));
                    else
                        LL1Table.put(new MyPair<String, String>(row, column), new MyPair<>(new ArrayList<>(Collections.singletonList("err")), 0));
                }
            }
        }

        for (String terminal : this.grammar.getSetOfTerminals()) {
            if (!terminal.equals("epsilon")) {
                LL1Table.put(new MyPair<String, String>(terminal, "$"), new MyPair<>(new ArrayList<>(Collections.singletonList("err")), 0));
                LL1Table.put(new MyPair<String, String>("$", terminal), new MyPair<>(new ArrayList<>(Collections.singletonList("err")), 0));
            }
        }

        LL1Table.put(new MyPair<>("$", "$"), new MyPair<>(new ArrayList<>(Collections.singletonList("acc")), 0));


        for (String A : this.grammar.getSetOfNonterminals()) {
            // M(A,a) = (alfa,i)    A=nonterminal   alfa=production(A)   a=First(alfa)  i=order of production
            for (MyPair<String, List<String>> production : this.grammar.getProductionsOfNonterminal(A)) {
                String alfa = production.getSecond().get(0);
                Set<String> a = this.first.get(alfa);
                for (String ai : a) {
                    if (!ai.equals("epsilon")) {

                        boolean okay = true;
                        for (MyPair<String, String> positions : LL1Table.keySet()) {
                            if (positions.getFirst().equals(A) && positions.getSecond().equals(ai)) {
                                okay = false;
                                break;
                            }
                        }
                        if (okay)
                            LL1Table.put(new MyPair<>(A, ai), new MyPair<>(new ArrayList<>(production.getSecond()), this.grammar.getProductions().indexOf(production) + 1));
                        else
                            return "Grammar is not LL(1) and the error is at position (row:" + A + " ; column:" + ai + ")";

                    } else {
                        for (String f : this.follow.get(A)) {

                            boolean okay = true;
                            for (MyPair<String, String> positions : LL1Table.keySet()) {
                                if (positions.getFirst().equals(A) && (positions.getSecond().equals(f) || (f.equals("epsilon") && positions.getSecond().equals("$")))) {
                                    okay = false;
                                    break;
                                }
                            }

                            if (okay && f.equals("epsilon"))
                                LL1Table.put(new MyPair<>(A, "$"), new MyPair<>(new ArrayList<>(production.getSecond()), this.grammar.getProductions().indexOf(production) + 1));
                            else if (okay)
                                LL1Table.put(new MyPair<>(A, f), new MyPair<>(new ArrayList<>(production.getSecond()), this.grammar.getProductions().indexOf(production) + 1));
                            else if (f.equals("epsilon"))
                                return "Grammar is not LL(1) and the error is at position (row:" + A + " ; column:$)";
                            else
                                return "Grammar is not LL(1) and the error is at position (row:" + A + " ; column:" + f + ")";
                        }
                    }

                }
            }
        }

        for (String row : this.grammar.getSetOfNonterminals()) {
            for (String column : this.grammar.getSetOfTerminals()) {
                boolean okay = true;
                for (MyPair<String, String> positions : LL1Table.keySet()) {
                    if (positions.getFirst().equals(row) && (positions.getSecond().equals(column) || (column.equals("epsilon") && positions.getSecond().equals("$")))) {
                        okay = false;
                        break;
                    }
                }
                if (okay && !column.equals("epsilon")) {
                    LL1Table.put(new MyPair<>(row, column), new MyPair<>(new ArrayList<>(Collections.singletonList("err")), 0));
                } else if (okay) {
                    LL1Table.put(new MyPair<>(row, "$"), new MyPair<>(new ArrayList<>(Collections.singletonList("err")), 0));
                }
            }
        }


        return "This grammar is LL(1)";
    }

    private void First() {
        this.grammar.getSetOfTerminals().forEach(t -> first.put(t, new HashSet<>(Set.of(t))));
        this.grammar.getSetOfNonterminals().forEach(t -> first.put(t, new HashSet<>()));
        this.grammar.getProductions().forEach(p -> {
            if (this.grammar.getSetOfTerminals().contains(p.getSecond().get(0)))
                this.first.get(p.getFirst()).add(p.getSecond().get(0));
        });
        boolean changesMade;
        boolean weCanStillApply;
        do {
            changesMade = false;
            for (String nonterminal : this.grammar.getSetOfNonterminals()) {
                int before = first.get(nonterminal).size();
                for (MyPair<String, List<String>> p : this.grammar.getProductionsOfNonterminal(nonterminal)) {
                    weCanStillApply = true;
                    Set<String> fminus1 = new HashSet<>(first.get(p.getSecond().get(0)));
                    for (String c : p.getSecond()) {
                        if (this.first.get(c).size() == 0) {
                            weCanStillApply = false;
                            break;
                        }
                        fminus1.addAll(concatenationWithOne(fminus1, this.first.get(c)));
                    }
                    if (weCanStillApply) this.first.get(nonterminal).addAll(fminus1);
                }
                if (!(before == this.first.get(nonterminal).size())) changesMade = true;
            }
        } while (changesMade);
    }


    private Set<String> concatenationWithOne(Set<String> a, Set<String> b) {
        Set<String> result = new HashSet<>(a);
        if (a.contains("epsilon")) {
            result.remove("epsilon");
            result.addAll(b);
        }
        return result;
    }

    private void Follow() {
        /// add the non terminals
        this.grammar.getSetOfNonterminals().forEach(t -> follow.put(t, new HashSet<>()));
        this.follow.get(this.grammar.getInitialState()).add("epsilon");
        boolean changesMade = false;
        do {
            changesMade = false;
            for (String nonterminal : this.grammar.getSetOfNonterminals()) {
                int before = this.follow.get(nonterminal).size();
                for (MyPair<String, List<String>> production : this.grammar.getProductions()) {
                    String A = production.getFirst();
                    if (production.getSecond().contains(nonterminal)) {
                        String gamma = "";
                        try {
                            gamma = production.getSecond().get(production.getSecond().indexOf(nonterminal) + 1);
                        } catch (Exception e) {
                            gamma = "epsilon";
                        }
                        for (String a : this.first.get(gamma)) {
                            if (this.first.get(a).contains("epsilon")) {
                                this.follow.get(nonterminal).addAll(this.follow.get(A));
                            } else {
                                this.follow.get(nonterminal).addAll(this.first.get(a));
                            }
                        }
                    }
                }
                if (!(before == this.follow.get(nonterminal).size())) changesMade = true;
            }
        } while (changesMade);
    }

    public void parseFile(String fileName) {
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                this.parse(myReader.nextLine());
            }
            myReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void parsePIF(String pifFileName) {
        try {
            String secondColumnOfPIF = "";
            File myObj = new File(pifFileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String x1 = myReader.nextLine().split("}, ")[1];
                String x = x1.substring(0, x1.length() - 1);
                secondColumnOfPIF += x + " ";
            }
            System.out.println(secondColumnOfPIF);
            this.parse(secondColumnOfPIF);
            myReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
