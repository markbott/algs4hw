import java.io.File;
import java.util.HashMap;
import java.util.Map;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

public class WordNet {
	private Map<Integer, Bag<String>> id2word = new HashMap<>();
	private Map<String, Bag<Integer>> word2id = new HashMap<>();

	private Digraph hypers;

	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) {
		checkNull(synsets);
		checkNull(hypernyms);

		In infile = new In(new File(synsets));

		String line;
		while ((line = infile.readLine()) != null) {
			String[] tokens = line.split(",\\s*");
			Integer id = Integer.valueOf(tokens[0]);
			String[] words = tokens[1].split(" ");
			for (String w : words) {
				id2word.computeIfAbsent(id, k -> new Bag<String>()).add(w);
				word2id.computeIfAbsent(w, k -> new Bag<Integer>()).add(id);
			}
		}

		infile.close();

		infile = new In(new File(hypernyms));
		Map<Integer, Bag<Integer>> hypermap = new HashMap<>();
		int vertices = 0;

		while ((line = infile.readLine()) != null) {
			String[] tokens = line.split(",\\s*");
			Integer syn = Integer.valueOf(tokens[0]);

			if (!id2word.containsKey(syn)) {
				throw new IllegalArgumentException("Invalid synset found in hypernym file: " + syn);
			}

			Bag<Integer> hyperBag = new Bag<>();
			hypermap.put(syn, hyperBag);
			vertices = Math.max(vertices, syn);
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
	}

	// returns all WordNet nouns
	public Iterable<String> nouns() {
		Bag<String> nouns = new Bag<>();
		id2word.values().forEach(bag -> bag.forEach(w -> nouns.add(w)));
		return nouns;
	}

	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		return word2id.containsKey(word);
	}

	// distance between nounA and nounB (defined below)
	public int distance(String nounA, String nounB) {
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new IllegalArgumentException();
		}

		Bag<Integer> ids1 = word2id.get(nounA);
		Bag<Integer> ids2 = word2id.get(nounB);
		
		SAP sap = new SAP(hypers);
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
		
		SAP sap = new SAP(hypers);
		int ancestor = sap.ancestor(ids1, ids2);
		
		if(ancestor == -1) {
			return null;
		}
			
		return id2word.get(ancestor).iterator().next();
	}
	
	private int sapId(String nounA, String nounB) {
		if (!isNoun(nounA) || !isNoun(nounB)) {
			throw new IllegalArgumentException();
		}

		Bag<Integer> ids1 = word2id.get(nounA);
		Bag<Integer> ids2 = word2id.get(nounB);
		
		SAP sap = new SAP(hypers);
		int ancestor = sap.ancestor(ids1, ids2);
		return ancestor;
	}

	private static void checkNull(Object o) {
		if (o == null)
			throw new IllegalArgumentException();
	}

	// do unit testing of this class
	public static void main(String[] args) {
		WordNet w = new WordNet("c:/users/marek/algs4/assignment6-wordnet/wordnet/synsets100-subgraph.txt",
				"c:/users/marek/algs4/assignment6-wordnet/wordnet/hypernyms100-subgraph.txt");

		assert (w.isNoun("proaccelerin"));
		assert (w.isNoun("prothrombin_accelerator"));
		assert (w.isNoun("accelerator_factor"));
		assert (w.isNoun("factor_V"));
		assert (!w.isNoun("something I made up"));

		assert (w.nouns().iterator().hasNext());
	}

}