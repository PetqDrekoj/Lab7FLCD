package ro;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        Grammar grammar = new Grammar("g1.txt");
        Parser parser = new Parser(grammar);
        parser.parseFile("seq.txt");

//        Grammar grammar = new Grammar("g2.txt");
//        Parser parser = new Parser(grammar);
//        parser.parsePIF("PIF.out");
    }
}
