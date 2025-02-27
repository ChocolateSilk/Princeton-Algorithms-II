import java.util.ArrayList;
import java.util.List;
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int R = 256;
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        List<Character> alphabet = new ArrayList<>();
        for (char c = 0; c < R; c++) {
            alphabet.add(c);
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int p = alphabet.indexOf(c);
            BinaryStdOut.write(p, 8);

            alphabet.remove(p);
            alphabet.add(0,c);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        List<Character> alphabet = new ArrayList<>();
        for (char c = 0; c < R; c++) {
            alphabet.add(c);
        }
        while (!BinaryStdIn.isEmpty()) {
            int p = BinaryStdIn.readInt(8);
            char c = alphabet.get(p);
            BinaryStdOut.write(c);

            alphabet.remove(p);
            alphabet.add(0,c);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
