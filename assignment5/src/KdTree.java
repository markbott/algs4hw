import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private static class Node {
        private Point2D val;
        private Node left;
        private Node right;
        private boolean vertical;
        private int size;

        public Node(Point2D p, boolean vertical) {
            this.val = p;
            this.vertical = vertical;
            size = 1;
        }

        double compareTo(Point2D p) {
            if (vertical)
                return this.val.x() - p.x();
            return this.val.y() - p.y();
        }

        public String toString() {
            return val.toString() + (vertical ? ", vertical" : ", horizontal");
        }
    }

    private Node root;

    public KdTree() { // construct an empty set of points
        root = null;
    }

    public boolean isEmpty() { // is the set empty?
        return root == null;
    }

    public int size() { // number of points in the set
        return root == null ? 0 : root.size;
    }

    public void insert(Point2D p) { // add the point to the set (if it is not
                                    // already in the set)
        root = insert(root, p, true);
    }

    private Node insert(Node n, Point2D p, boolean vertical) {
        if (n == null)
            return new Node(p, vertical);

        if (n.val.equals(p))
            return n;

        if (n.compareTo(p) > 0)
            n.left = insert(n.left, p, !vertical);
        else
            n.right = insert(n.right, p, !vertical);

        n.size = 1 + (n.left == null ? 0 : n.left.size) + (n.right == null ? 0 : n.right.size);
        return n;
    }

    public boolean contains(Point2D p) { // does the set contain point p?
        return contains(root, p);
    }

    private boolean contains(Node n, Point2D p) {
        if (n == null)
            return false;
        if (n.val.equals(p))
            return true;

        if (n.compareTo(p) > 0)
            return contains(n.left, p);
        else
            return contains(n.right, p);
    }

    public void draw() { // draw all points to standard draw
        RectHV all = new RectHV(0, 0, 1.0, 1.0);

        draw(root, all);
    }

    private void draw(Node n, RectHV space) {
        if (n == null)
            return;

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.filledCircle(n.val.x(), n.val.y(), 0.01);

        if (n.vertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setPenRadius();

            StdDraw.line(n.val.x(), space.ymin(), n.val.x(), space.ymax());

            RectHV leftSpace = new RectHV(space.xmin(), space.ymin(), n.val.x(), space.ymax());
            draw(n.left, leftSpace);
            RectHV rightSpace = new RectHV(n.val.x(), space.ymin(), space.xmax(), space.ymax());
            draw(n.right, rightSpace);
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.setPenRadius();

            StdDraw.line(space.xmin(), n.val.y(), space.xmax(), n.val.y());

            RectHV bottomSpace = new RectHV(space.xmin(), space.ymin(), space.xmax(), n.val.y());
            draw(n.left, bottomSpace);
            RectHV topSpace = new RectHV(space.xmin(), n.val.y(), space.xmax(), space.ymax());
            draw(n.right, topSpace);
        }
    }

    public Iterable<Point2D> range(RectHV rect) { // all points that are inside
                                                  // the rectangle
        Set<Point2D> r = new TreeSet<>();
        range(root, new RectHV(0, 0, 1.0, 1.0), rect, r);
        return r;
    }

    private void range(Node n, RectHV space, RectHV searchRect, Set<Point2D> points) {
        if (n == null)
            return;
        if (searchRect.contains(n.val))
            points.add(n.val);

        if (n.vertical) {
            RectHV leftSpace = new RectHV(space.xmin(), space.ymin(), n.val.x(), space.ymax());
            if (searchRect.intersects(leftSpace))
                range(n.left, leftSpace, searchRect, points);
            RectHV rightSpace = new RectHV(n.val.x(), space.ymin(), space.xmax(), space.ymax());
            if (searchRect.intersects(rightSpace))
                range(n.right, rightSpace, searchRect, points);
        } else {
            RectHV bottomSpace = new RectHV(space.xmin(), space.ymin(), space.xmax(), n.val.y());
            if (searchRect.intersects(bottomSpace))
                range(n.left, bottomSpace, searchRect, points);
            RectHV topSpace = new RectHV(space.xmin(), n.val.y(), space.xmax(), space.ymax());
            if (searchRect.intersects(topSpace))
                range(n.right, topSpace, searchRect, points);
        }
    }

    public Point2D nearest(Point2D that) { // a nearest neighbor in the set to
                                           // point p; null if the set is empty
        Nearest nearest = new Nearest();
        nearest(root, that, new RectHV(0, 0, 1.0, 1.0), nearest);
        return nearest.val;
    }

    private class Nearest {
        private double d2min;
        private Point2D val;

        private Nearest() {
            d2min = Double.POSITIVE_INFINITY;
            val = null;
        }

        public String toString() {
            return "min = " + d2min + (val == null ? ", no point" : val.toString());
        }
    }

    private void nearest(Node n, final Point2D that, RectHV space, Nearest min) {
        if (n == null)
            return;

        double d2 = n.val.distanceSquaredTo(that);

        if (d2 < min.d2min) {
            min.d2min = d2;
            min.val = n.val;
            if (d2 == 0)
                return; // can't get better than exact match
        }

        if (n.vertical) {
            double valx = n.val.x();
            double dx = valx - that.x();

            RectHV leftSpace = new RectHV(space.xmin(), space.ymin(), valx, space.ymax());
            RectHV rightSpace = new RectHV(valx, space.ymin(), space.xmax(), space.ymax());

            if (dx >= 0) {
                nearest(n.left, that, leftSpace, min);
                if ((dx * dx) < min.d2min) {
                    nearest(n.right, that, rightSpace, min);
                }
            }

            if (dx < 0) {
                nearest(n.right, that, rightSpace, min);
                if ((dx * dx) < min.d2min) {
                    nearest(n.left, that, leftSpace, min);
                }
            }

        } else {
            double valy = n.val.y();
            double dy = valy - that.y();

            RectHV bottomSpace = new RectHV(space.xmin(), space.ymin(), space.xmax(), valy);
            RectHV topSpace = new RectHV(space.xmin(), valy, space.xmax(), space.ymax());

            if (dy >= 0) {
                nearest(n.left, that, bottomSpace, min);
                if ((dy * dy) < min.d2min) {
                    nearest(n.right, that, topSpace, min);
                }
            }

            if (dy < 0) {
                nearest(n.right, that, topSpace, min);
                if ((dy * dy) < min.d2min) {
                    nearest(n.left, that, bottomSpace, min);
                }
            }
        }
    }

    public static void main(String[] args) { // unit testing of the methods
                                             // (optional)
        Point2D[][] grid = new Point2D[11][11];
        for (int x = 0; x <= 10; x++) {
            for (int y = 0; y <= 10; y++) {
                grid[x][y] = new Point2D((double) x / 10, (double) y / 10);
            }
        }

        KdTree set = new KdTree();

        assert set.isEmpty();
        assert set.size() == 0;
        assert !set.contains(grid[0][0]);
        assert set.nearest(grid[1][1]) == null;

        set.insert(grid[7][2]);

        assert !set.isEmpty();
        assert set.size() == 1;
        assert set.contains(grid[7][2]);

        set.insert(grid[5][4]);
        set.insert(grid[2][3]);
        set.insert(grid[4][7]);
        set.insert(grid[9][6]);

        assert !set.isEmpty();
        assert set.size() == 5;

        set.insert(grid[5][4]);
        assert set.size() == 5;

        assert set.contains(grid[9][6]);
        assert set.contains(grid[5][4]);
        assert !set.contains(grid[10][2]);
        assert !set.contains(grid[9][9]);

        assert set.nearest(new Point2D(0.55, 0.35)).equals(grid[5][4]);
        assert set.nearest(grid[9][6]).equals(grid[9][6]);
        assert set.nearest(grid[5][6]).equals(grid[4][7]);
        assert set.nearest(grid[0][0]).equals(grid[2][3]);

        int cnt = 0;
        for (Iterator<Point2D> iterator = set.range(new RectHV(0, 0, 1.0, 1.0)).iterator(); iterator.hasNext(); iterator
                .next()) {
            cnt++;
        }
        assert cnt == 5;

        cnt = 0;
        for (Iterator<Point2D> iterator = set.range(new RectHV(0.3, 0, 0.35, 0.8)).iterator(); iterator.hasNext();) {
            cnt++;
        }
        assert cnt == 0;

        cnt = 0;
        for (Iterator<Point2D> iterator = set.range(new RectHV(0.3, 0.35, 0.6, 0.8)).iterator(); iterator.hasNext();) {
            Point2D p = iterator.next();
            assert p.equals(grid[5][4]) || p.equals(grid[4][7]);
            cnt++;
        }
        assert cnt == 2;

        set.draw();

        /*
         * set = new KdTree(); final int GRID = 100;
         * 
         * for (int i = 0; i < 1000; i++) { set.insert(new
         * Point2D(StdRandom.uniform(0, GRID) / (double) GRID, (int)
         * StdRandom.uniform(0, GRID) / (double) GRID)); }
         * 
         * for (Point2D p : set.range(new RectHV(0, 0, 1.0, 1.0))) {
         * StdOut.println(p); }
         * 
         * for (int i = 0; i < 1000; i++) { set.nearest(new
         * Point2D(StdRandom.uniform(0, GRID) / (double) GRID, (int)
         * StdRandom.uniform(0, GRID) / (double) GRID)); }
         */
    }
}
