import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class WordNet {

    private final Map<Integer, String> synsetsMap; 
    private final Map<String, Set<Integer>> nounsMap;
    private final Digraph graph;
    private final int numSynsets;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        synsetsMap = new HashMap<>();
        nounsMap = new HashMap<>();
        numSynsets = readSynsets(synsets);
        graph = new Digraph(numSynsets);
        readHypernyms(hypernyms);
        this.sap = new SAP(graph);
    }

    private int readSynsets(String synsets){
        int count = 0;
        In syn_file = new In(synsets);
        while (syn_file.hasNextLine()) {
            String[] line = syn_file.readLine().split(",");
            int syn_id = Integer.parseInt(line[0]);
            String synset = line[1];
            synsetsMap.put(syn_id, synset);
            for (String word : synset.split(" ")) {
                nounsMap.computeIfAbsent(word, k -> new HashSet<>()).add(syn_id);
            }
            count ++;
        }
        return count;
    }

    private void readHypernyms(String hypernums) {
        In hyper_file = new In(hypernums);
        while (hyper_file.hasNextLine()) {
            String[] line = hyper_file.readLine().split(",");
            int syn_id = Integer.parseInt(line[0]);
            for (int i = 1; i < line.length; i ++) {
                int hyper_id = Integer.parseInt(line[i]);
                graph.addEdge(syn_id, hyper_id);
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounsMap.keySet();
    }
 
    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("null word");
        return nounsMap.containsKey(word);
    }
 
    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("not in word set");
        Set<Integer> synsetA = nounsMap.get(nounA);
        Set<Integer> synsetB = nounsMap.get(nounB);

        return sap.length(synsetA, synsetB);
    }
 
    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("not in word set");
        Set<Integer> synsetA = nounsMap.get(nounA);
        Set<Integer> synsetB = nounsMap.get(nounB);

        int ancestor = sap.ancestor(synsetA, synsetB);
        return synsetsMap.get(ancestor);
    }
 
    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}