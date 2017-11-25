import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
	private WordNet wordnet;

	public Outcast(WordNet wordnet) { // constructor takes a WordNet object
		checkNull(wordnet);
		this.wordnet = wordnet;
	}

	public String outcast(String[] nouns) { // given an array of WordNet nouns,
											// return an outcast
		
		checkNull(nouns);
		
		int maxsum = Integer.MIN_VALUE;
		String outcast = null;
		
		for(int i = 0; i < nouns.length; i++) {
			int sum = 0;
			for(int j = 0; j < nouns.length; j++) {
				if(i == j) continue;
				
				sum += wordnet.distance(nouns[i], nouns[j]);
			}
			
			if(sum > maxsum) { 
				maxsum = sum;
				outcast = nouns[i];
			}
		}
		
		return outcast;
	}

	private void checkNull(Object o) {
		if(o == null) throw new IllegalArgumentException();
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