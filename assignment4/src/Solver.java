import java.util.Comparator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private static class SearchNode {
        private Board board;
        private int moves;
        private SearchNode previous;

        public SearchNode(Board b, int m, SearchNode p) {
            board = b;
            moves = m;
            previous = p;
        }
    }

    private SearchNode solution = null;

    public Solver(Board initial) { // find a solution to the initial board
                                   // (using the A* algorithm)

        Comparator<SearchNode> comparator = new Comparator<SearchNode>() {

            @Override
            public int compare(SearchNode o1, SearchNode o2) {
                int p1 = o1.moves + o1.board.manhattan();
                int p2 = o2.moves + o2.board.manhattan();
                return p1 - p2;
            }
        };

        MinPQ<SearchNode> pq = new MinPQ<>(comparator);
        pq.insert(new SearchNode(initial, 0, null));
        MinPQ<SearchNode> pqTwin = new MinPQ<>(comparator);
        pqTwin.insert(new SearchNode(initial.twin(), 0, null));
        findSolution(pq, pqTwin);
    }

    private void findSolution(MinPQ<SearchNode> pq, MinPQ<SearchNode> pqTwin) {
        while (true) {
            SearchNode soln = oneStep(pq);
            if (soln != null) {
                solution = soln;
                return;
            }

            soln = oneStep(pqTwin);
            if (soln != null) {
                solution = null;
                return;
            }
        }
    }

    private static SearchNode oneStep(MinPQ<SearchNode> pq) {
        SearchNode min = pq.delMin();

        if (min.board.isGoal()) {
            return min;
        }

        for (Board neigh : min.board.neighbors()) {
            if (min.previous != null && neigh.equals(min.previous.board))
                continue;
            pq.insert(new SearchNode(neigh, min.moves + 1, min));
        }
        return null;
    }

    public boolean isSolvable() { // is the initial board solvable?
        return solution != null;
    }

    public int moves() { // min number of moves to solve initial board; -1 if
                         // unsolvable
        return solution == null ? -1 : solution.moves;
    }

    public Iterable<Board> solution() { // sequence of boards in a shortest
                                        // solution; null if unsolvable
        if (!isSolvable())
            return null;
        ResizingArrayStack<Board> seq = new ResizingArrayStack<>();
        for (SearchNode cur = solution; cur != null; cur = cur.previous) {
            seq.push(cur.board);
        }
        return seq;
    }

    public static void main(String[] args) { // solve a slider puzzle (given
                                             // below)

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
