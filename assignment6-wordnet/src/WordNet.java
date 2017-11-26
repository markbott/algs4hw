import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Topological;

public class WordNet {
    private final Map<Integer, String> id2word = new HashMap<>();
    private final Map<String, Bag<Integer>> word2id = new HashMap<>();

    private final Digraph hypers;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        checkNull(synsets);
        checkNull(hypernyms);

        int vertices = -1;
        In infile = new In(synsets);
        while (true) {
            String line = infile.readLine();
            if (line == null) {
                break;
            }

            String[] tokens = line.split(",\\s*");
            int id = Integer.parseInt(tokens[0]);
            vertices = Math.max(id, vertices);
            id2word.put(id, tokens[1]);
            String[] words = tokens[1].split(" ");
            for (String w : words) {
                word2id.computeIfAbsent(w, k -> new Bag<Integer>()).add(id);
            }
        }

        infile.close();

        infile = new In(hypernyms);
        Map<Integer, Bag<Integer>> hypermap = new HashMap<>();

        while (true) {
            String line = infile.readLine();
            if (line == null) {
                break;
            }

            String[] tokens = line.split(",\\s*");
            int syn = Integer.parseInt(tokens[0]);

            if (!id2word.containsKey(syn)) {
                throw new IllegalArgumentException("Invalid synset found in hypernym file: " + syn);
            }

            Bag<Integer> hyperBag = hypermap.computeIfAbsent(syn, k -> new Bag<Integer>());
            for (int h = 1; h < tokens.length; h++) {
                hyperBag.add(Integer.valueOf(tokens[h]));
            }
        }

        hypers = new Digraph(vertices + 1);
        for (Integer syn : hypermap.keySet()) {
            for (Integer h : hypermap.get(syn)) {
                hypers.addEdge(syn, h);
            }
        }
        
        Topological topo = new Topological(hypers);
        if(!topo.hasOrder()) {
            throw new IllegalArgumentException("not a DAG");
        }
        
        int roots = 0;
        for(int v = 0; v < hypers.V(); v++) {
            if(hypers.indegree(v) > 0 && hypers.outdegree(v) == 0) {
                roots++;
                
                if(roots > 1) {
                    throw new IllegalArgumentException("multiple roots");
                }
            }
        }
        
        sap = new SAP(hypers);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        Set<String> nouns = new HashSet<>();
        id2word.values().forEach(synset -> {         
            for(String n : synset.split(" ")) {
                nouns.add(n);
            }
        });
        return nouns;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        checkNull(word);
        return word2id.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }

        Bag<Integer> ids1 = word2id.get(nounA);
        Bag<Integer> ids2 = word2id.get(nounB);

        return sap.length(ids1, ids2);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of
    // nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }

        Bag<Integer> ids1 = word2id.get(nounA);
        Bag<Integer> ids2 = word2id.get(nounB);

        int ancestor = sap.ancestor(ids1, ids2);

        if (ancestor == -1) {
            return null;
        }

        return id2word.get(ancestor);
    }

    private static void checkNull(Object o) {
        if (o == null)
            throw new IllegalArgumentException();
    }

    // do unit testing of this class
    public static void main(String[] args) {
         /*
         WordNet w = new WordNet(
           "synsets.txt",
           "hypernyms3InvalidTwoRoots.txt"
         );
         int sum = 0;
         for(String x : w.nouns()) { sum++; }
         StdOut.println(sum);
         */
    }

}