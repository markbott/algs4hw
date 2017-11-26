import java.util.HashMap;
import java.util.Map;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

public class SAP {
    private final Digraph G;
    private final int G_V;
    private final Map<Integer, BFSResult> bfses = new HashMap<>();

    private static class AncestorInfo {
        int ancestor;
        int length;

        AncestorInfo(int anc, int len) {
            ancestor = anc;
            length = len;
        }
    }

    private class BFSResult {
        boolean[] marked = new boolean[G.V()];
        //int[] edgeTo = new int[G.V()];
        int[] dist = new int[G.V()];
    }

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        checkNull(G);
        this.G = new Digraph(G);
        G_V = G.V();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        return findShortestAncestor(v, w).length;
    }

    // a common ancestor of v and w that participates in a shortest ancestral
    // path; -1 if no such path
    public int ancestor(int v, int w) {
        return findShortestAncestor(v, w).ancestor;
    }

    private AncestorInfo findShortestAncestor(int v, int w) {
        checkVertex(v);
        checkVertex(w);

        BFSResult bfsV = bfs(v);
        BFSResult bfsW = bfs(w);

        return findShortestAncestor(bfsV, v, bfsW, w);
    }

    private AncestorInfo findShortestAncestor(BFSResult bfsV, int v, BFSResult bfsW, int w) {

        int ancestor = -1;
        int len = Integer.MAX_VALUE;
        
        int distV = bfsV.dist[v];
        int distW = bfsW.dist[w];

        for (int i = 0; i < G.V(); i++) {
            if ((bfsV.marked[i] && bfsV.dist[i] >= distV) && (bfsW.marked[i] && bfsW.dist[i] >= distW)) {
                int ancestorLen = (bfsV.dist[i] - bfsV.dist[v]) + (bfsW.dist[i] - bfsW.dist[w]);
                if (ancestorLen < len) {
                    len = ancestorLen;
                    ancestor = i;
                }
            }
        }

        return new AncestorInfo(ancestor, len == Integer.MAX_VALUE ? -1 : len);
    }

    private BFSResult bfs(int s) {
        BFSResult r = new BFSResult();
        Queue<Integer> q = new Queue<>();

        q.enqueue(s);
        r.marked[s] = true;
        r.dist[s] = 0;

        while (!q.isEmpty()) {
            int v = q.dequeue();
            int curdist = r.dist[v] + 1;
            
            for (int w : G.adj(v)) {
                if (!r.marked[w]) {
                    q.enqueue(w);
                    r.marked[w] = true;
                    //r.edgeTo[w] = v;
                    r.dist[w] = curdist;
                }
            }
        }

        return r;
    }

    private void buildBFSInfo(Iterable<Integer> vertices, Map<Integer, BFSResult> bfses) {

        for (int a : vertices) {
            checkVertex(a);

            if(bfses.containsKey(a)) {
                continue;
            }
            
            boolean found = false;
            
            for (BFSResult bfs : bfses.values()) {
                if (bfs.marked[a]) {
                    bfses.put(a, bfs);
                    found = true;
                    break;
                }
            }

            if (!found) {
                bfses.put(a, bfs(a));
            }
        }
    }

    // length of shortest ancestral path between any vertex in v and any vertex
    // in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return findShortestAncestor(v, w).length;
    }

    private AncestorInfo findShortestAncestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkNull(v);
        checkNull(w);
        
        buildBFSInfo(v, bfses);
        buildBFSInfo(w, bfses);

        AncestorInfo shortest = new AncestorInfo(-1, -1);

        for (int a : v) {
            for (int b : w) {
                BFSResult bfsA = bfses.get(a);
                BFSResult bfsB = bfses.get(b);

                AncestorInfo ancestor = findShortestAncestor(bfsA, a, bfsB, b);
                if (ancestor.ancestor < 0)
                    continue; // no ancestor here!

                if (shortest.ancestor < 0 || ancestor.length < shortest.length) {
                    shortest = ancestor;
                }
            }
        }
        return shortest;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no
    // such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return findShortestAncestor(v, w).ancestor;

    }

    // do unit testing of this class
    public static void main(String[] args) {
        /*
        In in = new In("digraph1.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        int[][] tests = new int[][] { { 3, 11 }, { 9, 12 }, { 7, 2 }, { 1, 6 } };
        int[][] results = new int[][] { { 4, 1 }, { 3, 5 }, { 4, 0 }, { -1, -1 } };

        for (int j = 0; j < tests.length; j++) {
            assert (sap.length(tests[j][0], tests[j][1]) == results[j][0]);
            assert (sap.ancestor(tests[j][0], tests[j][1]) == results[j][1]);

            assert (sap.length(tests[j][1], tests[j][0]) == results[j][0]);
            assert (sap.ancestor(tests[j][1], tests[j][0]) == results[j][1]);

            Bag<Integer> a = new Bag<>();
            a.add(tests[j][0]);
            Bag<Integer> b = new Bag<>();
            b.add(tests[j][1]);

            assert (sap.length(a, b) == results[j][0]);
            assert (sap.ancestor(a, b) == results[j][1]);

        }

        StdOut.println(sap.ancestor(3, 1) + "  len = " + sap.length(3, 1));

        Bag<Integer> a = new Bag<>();
        a.add(11);
        // a.add(10);

        Bag<Integer> b = new Bag<>();
        // b.add(4);
        // b.add(7);
        b.add(12);
        ;
        StdOut.println("len = " + sap.length(a, b));
        StdOut.println("anc = " + sap.ancestor(a, b));
        */
        /*
         * In in = new In(args[0]); Digraph G = new Digraph(in); SAP sap = new
         * SAP(G); while (!StdIn.isEmpty()) { int v = StdIn.readInt(); int w =
         * StdIn.readInt(); int length = sap.length(v, w); int ancestor =
         * sap.ancestor(v, w); StdOut.printf("length = %d, ancestor = %d\n",
         * length, ancestor); }
         */
        /*
        In in = new In("digraph4.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        StdOut.println("anc = " + sap.ancestor(1, 5));
        StdOut.println("len = " + sap.length(1, 5));
        */
    }

    static private void checkNull(Object o) {
        if (o == null)
            throw new IllegalArgumentException("null parameter");
    }

    private void checkVertex(int v) {
        if (v < 0 || v >= G_V)
            throw new IllegalArgumentException("invalid vertex");
    }
}