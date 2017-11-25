import java.util.Arrays;

import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private int n;
    private int[] board;

    public Board(int[][] blocks) { // construct a board from an n-by-n array of
                                   // blocks
                                   // (where blocks[i][j] = block in row i,
                                   // column j)
        n = blocks.length;
        board = new int[n * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[n * i + j] = blocks[i][j];
            }
        }
    }

    private Board(Board other) {
        n = other.n;
        board = Arrays.copyOf(other.board, other.board.length);
    }

    public int dimension() { // board dimension n
        return n;
    }

    public int hamming() { // number of blocks out of place
        int ham = 0;
        for (int i = 0; i < board.length; i++) {
            if (board[i] != 0 && board[i] != (i + 1))
                ham++;
        }
        return ham;
    }

    public int manhattan() { // sum of Manhattan distances between blocks and
                             // goal
        int manhattan = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int val = board[n * i + j];
                if (val == 0)
                    continue;
                int expectedRow = (val - 1) / n;
                int expectedCol = (val - 1) % n;
                manhattan += Math.abs(expectedRow - i) + Math.abs(expectedCol - j);
            }
        }
        return manhattan;
    }

    public boolean isGoal() { // is this board the goal board?
        return hamming() == 0;
    }

    public Board twin() { // a board that is obtained by exchanging any pair of
                          // blocks
        Board twin = new Board(this);
        int i = 0;
        while (board[i] == 0)
            i++;
        int j = i + 1;
        while (board[j] == 0)
            j++;

        twin.board[i] = board[j];
        twin.board[j] = board[i];

        return twin;

    }

    public boolean equals(Object y) { // does this board equal y?
        if (this == y)
            return true;
        if (y == null)
            return false;
        if (!(y instanceof Board))
            return false;

        Board that = (Board) y;

        if (this.dimension() != that.dimension())
            return false;

        for (int i = 0; i < board.length; i++) {
            if (this.board[i] != that.board[i])
                return false;
        }
        return true;
    }

    public Iterable<Board> neighbors() { // all neighboring boards
        ResizingArrayBag<Board> neigh = new ResizingArrayBag<>();

        // find empty tile
        int i = 0;
        while (i < board.length && board[i] != 0)
            i++;
        if (i == board.length)
            throw new IllegalArgumentException("bad board");

        int row = i / n;
        int col = i % n;

        // move tile from 1 up
        if (row > 0) {
            Board b = new Board(this);
            b.board[i] = board[(row - 1) * n + col];
            b.board[(row - 1) * n + col] = 0;
            neigh.add(b);
        }

        // move tile from 1 down
        if (row < n - 1) {
            Board b = new Board(this);
            b.board[i] = board[(row + 1) * n + col];
            b.board[(row + 1) * n + col] = 0;
            neigh.add(b);
        }

        // move tile from 1 left
        if (col > 0) {
            Board b = new Board(this);
            b.board[i] = board[row * n + col - 1];
            b.board[row * n + col - 1] = 0;
            neigh.add(b);
        }

        // move tile from 1 right
        if (col < n - 1) {
            Board b = new Board(this);
            b.board[i] = board[row * n + col + 1];
            b.board[row * n + col + 1] = 0;
            neigh.add(b);
        }

        return neigh;
    }

    public String toString() { // string representation of this board (in the
                               // output format specified below)
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", board[i * n + j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    private static int[][] parseIntArray(String str) {
        String[] values = str.split("\\s+");
        int n = Integer.valueOf(values[0]);

        int[][] ret = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                ret[i][j] = Integer.valueOf(values[n * i + j + 1]);
            }
        }
        return ret;
    }

    public static void main(String[] args) { // unit tests (not graded)
        Board a = new Board(parseIntArray("3 8 1 3 4 0 2 7 6 5"));
        assert a.hamming() == 5;
        assert a.manhattan() == 10;
        assert a.equals(a);
        assert !a.equals(a.twin());
        assert !a.equals("abc");
        assert !a.equals(null);
        assert !a.isGoal();
        assert a.dimension() == 3;

        StdOut.println(a.toString());
        StdOut.println(a.twin().toString());

        assert a.equals(a.twin().twin());

        int neighborCount = 0;
        for (Board x : a.neighbors()) {
            neighborCount++;
            StdOut.println("Neighbor " + neighborCount + " " + x);
            assert !a.equals(x);
        }
        assert neighborCount == 4;

        Board goal = new Board(parseIntArray("3 1 2 3 4 5 6 7 8 0"));
        assert goal.hamming() == 0;
        assert goal.manhattan() == 0;
        assert goal.isGoal();
        assert !goal.equals(a);
    }
}
