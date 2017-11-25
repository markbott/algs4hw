import java.util.Comparator;

import edu.princeton.cs.algs4.StdDraw;

public class Point implements Comparable<Point> {
    private final int x; // x-coordinate of this point
    private final int y; // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param x
     *            the <em>x</em>-coordinate of the point
     * @param y
     *            the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point to
     * standard draw.
     *
     * @param that
     *            the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point. Formally,
     * if the two points are (x0, y0) and (x1, y1), then the slope is (y1 - y0)
     * / (x1 - x0). For completeness, the slope is defined to be +0.0 if the
     * line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical; and
     * Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param that
     *            the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that) {
        int dx = that.x - this.x;
        int dy = that.y - this.y;

        if (dx == 0 && dy == 0)
            return Double.NEGATIVE_INFINITY;
        if (dy == 0)
            return 0;
        if (dx == 0)
            return Double.POSITIVE_INFINITY;

        return (double) dy / dx;
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param that
     *            the other point
     * @return the value <tt>0</tt> if this point is equal to the argument point
     *         (x0 = x1 and y0 = y1); a negative integer if this point is less
     *         than the argument point; and a positive integer if this point is
     *         greater than the argument point
     */
    public int compareTo(Point that) {
        int dy = this.y - that.y;
        if (dy < 0 || dy > 0)
            return dy;

        return this.x - that.x;
    }

    /**
     * Compares two points by the slope they make with this point. The slope is
     * defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder() {
        return (arg0, arg1) -> {
            double m0 = this.slopeTo(arg0);
            double m1 = this.slopeTo(arg1);
            double dm = m0 - m1;
            if (dm < 0)
                return -1;
            if (dm > 0)
                return +1;
            return 0;
        };
    }

    /**
     * Returns a string representation of this point. This method is provide for
     * debugging; your program should not rely on the format of the string
     * representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    /**
     * Unit tests the Point data type.
     */
    public static void main(String[] args) {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 1);
        Point p3 = new Point(1, 0);
        Point p4 = new Point(1, 1);
        Point p5 = new Point(-1, -1);
        Point p6 = new Point(-1, 2);

        assert p2.compareTo(p1) > 0;
        assert p2.compareTo(p2) == 0;
        assert p2.compareTo(p6) < 0;
        assert p2.compareTo(p4) < 0;
        assert p5.compareTo(p1) < 0;
        assert p3.compareTo(p1) > 0;
        assert p1.compareTo(p5) > 0;

        assert p1.slopeTo(p2) == Double.POSITIVE_INFINITY;
        assert p1.slopeTo(p4) == 1.0;
        assert p5.slopeTo(p4) == 1.0;
        assert p4.slopeTo(p5) == 1.0;
        assert p4.slopeTo(p4) == Double.NEGATIVE_INFINITY;
        assert p3.slopeTo(p1) == 0.0;

        Comparator<Point> cmp = p4.slopeOrder();
        assert cmp.compare(p1, new Point(2, 2)) == 0;
        assert cmp.compare(new Point(2, 0), new Point(2, 1)) < 0;
        assert cmp.compare(new Point(2, 1), new Point(3, 0)) > 0;
    }

}
