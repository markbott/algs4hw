import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int n;
    // QuickFindUF uf;
    private WeightedQuickUnionUF uf;
    private WeightedQuickUnionUF uf2;
    private boolean[] opened;

    public Percolation(int n) { // create n-by-n grid, with all sites blocked
        if (n <= 0)
            throw new IllegalArgumentException();
        this.n = n;
        // this.uf = new QuickFindUF(n*n + 2);
        this.uf = new WeightedQuickUnionUF(n * n + 2);
        this.uf2 = new WeightedQuickUnionUF(n * n + 1);
        this.opened = new boolean[n * n + 2];

        opened[0] = true;
        opened[n * n + 1] = true;
    }

    // coordinate to index
    private int c2i(int row, int col) {
        checkCoordinate(row);
        checkCoordinate(col);
        return (row - 1) * n + col;
    }

    public void open(int row, int col) { // open site (row, col) if it is not
                                         // open already
        checkCoordinate(row);
        checkCoordinate(col);

        int pos = c2i(row, col);
        opened[pos] = true;

        if (row == 1) {
            uf.union(0, pos);
            uf2.union(0, pos);
        }
        
        if (row == n)
            uf.union(n * n + 1, pos);

        if (row > 1 && opened[c2i(row - 1, col)]) {
            uf.union(pos, c2i(row - 1, col));
            uf2.union(pos, c2i(row - 1, col));
        }
        
        if (col < n && opened[c2i(row, col + 1)]) {
            uf.union(pos, c2i(row, col + 1));
            uf2.union(pos, c2i(row, col + 1));
        }
        
        if (row < n && opened[c2i(row + 1, col)]) {
            uf.union(pos, c2i(row + 1, col));
            uf2.union(pos, c2i(row + 1, col));
        }
        
        if (col > 1 && opened[c2i(row, col - 1)]) {
            uf.union(pos, c2i(row, col - 1));
            uf2.union(pos, c2i(row, col - 1));
        }
    }

    public boolean isOpen(int row, int col) { // is site (row, col) open?
        checkCoordinate(row);
        checkCoordinate(col);
        return opened[c2i(row, col)];
    }

    public boolean isFull(int row, int col) { // is site (row, col) full?
        return isOpen(row, col) && uf2.connected(0, c2i(row, col));
    }

    public boolean percolates() { // does the system percolate?
        return uf.connected(0, n * n + 1);
    }

    private void checkCoordinate(int x) {
        if (x < 1 || x > n)
            throw new IndexOutOfBoundsException();
    }

    public static void main(String[] args) { // test client (optional)
        Percolation p = new Percolation(4);
        
        assert (!p.isFull(1,1));

        assert (!p.percolates());
        assert (!p.isOpen(2, 2));
        assert (p.isFull(3, 3));

        p.open(2, 2);
        p.open(3, 2);
        assert (!p.percolates());
        p.open(4, 2);
        assert (!p.percolates());
        p.open(1, 2);
        assert (p.isFull(1,2));
        assert (p.percolates());
    }
}