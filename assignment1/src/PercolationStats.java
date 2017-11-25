import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    private double[] results;
    private double mean, stdev, sqrtT;

    public PercolationStats(int n, int trials) { // perform trials independent
                                                 // experiments on an n-by-n
                                                 // grid
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException();

        results = new double[trials];

        for (int t = 0; t < trials; t++) {
            results[t] = runTrial(n);
        }

        mean = StdStats.mean(results);
        stdev = StdStats.stddev(results);
        sqrtT = Math.sqrt(trials);
    }

    public double mean() { // sample mean of percolation threshold
        return mean;
    }

    public double stddev() { // sample standard deviation of percolation
                             // threshold
        return stdev;
    }

    public double confidenceLo() { // low endpoint of 95% confidence interval
        return mean - (1.96 * stdev / sqrtT);
    }

    public double confidenceHi() { // high endpoint of 95% confidence interval
        return mean + (1.96 * stdev / sqrtT);
    }

    private double runTrial(int n) {
        Percolation p = new Percolation(n);
        int openSites = 0;
        boolean[] open = new boolean[n * n];

        while (true) {
            int row = StdRandom.uniform(n) + 1;
            int col = StdRandom.uniform(n) + 1;

            // if (!p.isOpen(row, col)) {
            if (!open[(row - 1) * n + col - 1]) {
                openSites++;
                open[(row - 1) * n + col - 1] = true;
                p.open(row, col);
                if (p.percolates()) {
                    return (double) openSites / (double) (n * n);
                }
            }
        }
    }

    public static void main(String[] args) { // test client (described below)
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);

        Stopwatch stopwatch = new Stopwatch();
        PercolationStats ps = new PercolationStats(n, t);

        System.out.println("mean                    = " + ps.mean());
        System.out.println("stddev                  = " + ps.stddev());
        System.out.println("95% confidence interval = " + ps.confidenceLo() + ", " + ps.confidenceHi());

        System.out.println("Stopwatch               = " + stopwatch.elapsedTime() + " seconds");
    }
}