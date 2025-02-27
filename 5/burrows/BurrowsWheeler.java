import edu.princeton.cs.algs4.BinaryStdOut;
import java.util.Arrays;
import edu.princeton.cs.algs4.BinaryStdIn;

public class BurrowsWheeler {

    private static final int R = 265;
    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output 
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray suffix = new CircularSuffixArray(s);
        int len = suffix.length();
	for (int i = 0; i < len; i++) {
            if (suffix.index(i) == 0) BinaryStdOut.write(i);
        }
        for (int i = 0; i < len; i++) {
            BinaryStdOut.write(s.charAt((suffix.index(i) + len - 1) % len));
        }
	
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String t = BinaryStdIn.readString();

        char[] input = t.toCharArray();
        char[] sortedT = t.toCharArray();
        int[] count = new int[R + 1];
        int[] next = new int[input.length];

        for (int i = 0; i < input.length; i++) {
            count[t.charAt(i) + 1]++;
        }

        for (int r = 0; r < R; r++) {
            count[r + 1] += count[r];
        }

        for (int i = 0; i < input.length; i++) {
            char c = t.charAt(i);
            int pos = count[c]++;
            sortedT[pos] = c;
            next[pos] = i;
        }
        
        int current = first;
        for (int i = 0; i < input.length; i++) {
            BinaryStdOut.write(sortedT[current]);
            current = next[current];
        }

        BinaryStdOut.close();

    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}