import java.util.Arrays;

import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray{

    private class CircularSuffix implements Comparable<CircularSuffix> {
        private final String str;
        private final int StartIndex;
        private final int length;
        public CircularSuffix(String str, int StartIndex) {
            this.str = str;
            this.StartIndex = StartIndex;
            this.length = str.length();
        }
        public char charAt(int i) {
            return str.charAt((StartIndex + i) % length);
        }
        public int compareTo(CircularSuffix that) {
            for (int i = 0; i < length; i++) {
                char thisChar = this.charAt(i);
                char thatChar = that.charAt(i);
                if (thisChar != thatChar) {
                    return thisChar - thatChar;
                }
            }
            return 0;
        }
    }

    private final int length;
    private final Integer[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("null string");

        this.length = s.length();
        this.index = new Integer[length];
        CircularSuffix[] suffixs = new CircularSuffix[length];
        for (int i = 0; i < length; i++) {
            suffixs[i] = new CircularSuffix(s, i);
            index[i] = i;
        }
        Arrays.sort(index, (a, b) -> suffixs[a].compareTo(suffixs[b]));
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length) throw new IllegalArgumentException();
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String str = args[0];
        CircularSuffixArray suffix = new CircularSuffixArray(str);
        for (int i = 0; i < str.length(); i++) {
            StdOut.println(suffix.index(i));
        }
        StdOut.println("string length:" + suffix.length);
    }
}
