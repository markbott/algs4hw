import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        RandomizedQueue<String> rq = new RandomizedQueue<>();
        for (String s : StdIn.readAllStrings()) {
            rq.enqueue(s);
        }

        for (int j = Integer.parseInt(args[0]); j > 0; j--) {
            StdOut.println(rq.dequeue());
        }
    }
}
