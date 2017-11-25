import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {
    private ResizingArrayBag<LineSegment> segments;

    public FastCollinearPoints(Point[] points) { // finds all line segments
                                                 // containing 4 or more points
        if (points == null)
            throw new NullPointerException();

        points = Arrays.copyOf(points, points.length);

        Arrays.sort(points);
        for (int j = 0; j < points.length - 1; j++) {
            if (points[j] == null)
                throw new NullPointerException();
            if (0 == points[j].compareTo(points[j + 1]))
                throw new IllegalArgumentException();
        }

        segments = new ResizingArrayBag<>();
        if (points.length <= 3) {
            return;
        }

        Point[] copy = new Point[points.length];
        for (int j = 0; j < points.length; j++) {
            copy[j] = points[j];
        }

        Point[] processed = new Point[points.length];
        Point[] linePoints = new Point[points.length];

        for (int pidx = 0; pidx < points.length - 3; pidx++) {
            Point p = points[pidx];

            processed[pidx] = p;
            linePoints[0] = p;

            Arrays.sort(copy, p.slopeOrder());

            for (int i = 1; i <= copy.length - 3; i++) {
                linePoints[1] = copy[i];
                int linePointIdx = 2;

                double m1 = p.slopeTo(copy[i]);

                boolean duplicate = Arrays.binarySearch(processed, 0, pidx + 1, copy[i]) >= 0;
                int nextPointIdx = -1;
                for (int j = i + 1; j < copy.length; j++) {
                    double mj = p.slopeTo(copy[j]);
                    if (mj != m1) {
                        break;
                    } else {
                        if (!duplicate && Arrays.binarySearch(processed, 0, pidx + 1, copy[j]) >= 0) {
                            duplicate = true;
                            // keep going through all points that match this
                            // slope
                        }

                        linePoints[linePointIdx++] = copy[j];
                        nextPointIdx = j;
                    }
                }

                if (linePointIdx >= 4) {
                    if (!duplicate) {
                        Arrays.sort(linePoints, 0, linePointIdx);

                        segments.add(new LineSegment(linePoints[0], linePoints[linePointIdx - 1]));
                    }
                    i = nextPointIdx;
                }

            }
        }
    }

    public int numberOfSegments() {
        return segments.size();
    } // the number of line segments

    public LineSegment[] segments() {
        LineSegment[] ret = new LineSegment[segments.size()];
        int ri = 0;
        for (LineSegment ls : segments) {
            ret[ri++] = ls;
        }
        return ret;
    } // the line segments

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();

    }
}
