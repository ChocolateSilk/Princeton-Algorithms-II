import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns == null || nouns.length == 0) {
            throw new IllegalArgumentException("null nouns");
        }
        String outcast = null;
        int maxDistance = -1;

        for (String nounA : nouns) {
            int totalDistance = 0;
            for (String nounB : nouns) {
                if (!nounA.equals(nounB)) {
                    totalDistance += wordnet.distance(nounA, nounB);
                }
            }

            if (totalDistance > maxDistance) {
                maxDistance = totalDistance;
                outcast = nounA;
            }
        }

        return outcast;
    }
    
    public static void main(String[] args) {
            WordNet wordnet = new WordNet(args[0], args[1]);
            Outcast outcast = new Outcast(wordnet);
            for (int t = 2; t < args.length; t++) {
                In in = new In(args[t]);
                String[] nouns = in.readAllStrings();
                StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
