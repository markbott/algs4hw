import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {
    private Set<Point2D> points;

    public PointSET() { // construct an empty set of points
        points = new TreeSet<>();
    }

    public boolean isEmpty() { // is the set empty?
        return points.isEmpty();
    }

    public int size() { // number of points in the set
        return points.size();
    }

    public void insert(Point2D p) { // add the point to the set (if it is not
                                    // already in the set)
        points.add(p);
    }

    public boolean contains(Point2D p) { // does the set contain point p?
        return points.contains(p);
    }

    public void draw() { // draw all points to standard draw
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D p : points) {
            StdDraw.filledCircle(p.x(), p.y(), 0.01);
        }
    }

    public Iterable<Point2D> range(RectHV rect) { // all points that are inside
                                                  // the rectangle
        TreeSet<Point2D> r = new TreeSet<>();
        for (Point2D p : points) {
            if (rect.contains(p))
                r.add(p);
        }
        return r;
    }

    public Point2D nearest(Point2D that) { // a nearest neighbor in the set to
                                           // point p; null if the set is empty
        Point2D minPoint = null;
        double minDist = Double.POSITIVE_INFINITY;
        for (Point2D p : points) {
            double dist = p.distanceSquaredTo(that);
            if (dist < minDist) {
                minDist = dist;
                minPoint = p;
            }
        }
        return minPoint;
    }

    public static void main(String[] args) { // unit testing of the methods
                                             // (optional)
        Point2D[][] grid = new Point2D[11][11];
        for (int x = 0; x <= 10; x++) {
            for (int y = 0; y <= 10; y++) {
                grid[x][y] = new Point2D((double) x / 10, (double) y / 10);
            }
        }

        PointSET set = new PointSET();

        assert set.isEmpty();
        assert set.size() == 0;
        assert !set.contains(grid[0][0]);

        set.insert(grid[1][2]);

        assert !set.isEmpty();
        assert set.size() == 1;
        assert set.contains(grid[1][2]);

        set.insert(grid[5][5]);
        set.insert(grid[5][8]);

        int cnt = 0;
        for (Iterator<Point2D> iterator = set.range(new RectHV(0, 0, 1.0, 1.0)).iterator(); iterator.hasNext(); iterator
                .next()) {
            cnt++;
        }
        assert cnt == 3;

        cnt = 0;
        for (Iterator<Point2D> iterator = set.range(new RectHV(0.4, 0, 0.6, 0.9)).iterator(); iterator
                .hasNext(); iterator.next()) {
            cnt++;
        }
        assert cnt == 2;

        assert !set.contains(grid[9][9]);

        assert set.nearest(grid[5][6]).equals(grid[5][5]);
        
        set.draw();
    }
}
