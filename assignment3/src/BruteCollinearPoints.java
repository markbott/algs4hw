import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
    private LineSegment[] segments;

    public BruteCollinearPoints(Point[] points) { // finds all line segments
                                                  // containing 4 points
        if (points == null)
            throw new NullPointerException();

        computeSegments(Arrays.copyOf(points, points.length));
    }

    private void computeSegments(Point[] points) {
        int np = points.length;

        Arrays.sort(points);
        for (int j = 0; j < points.length - 1; j++) {
            if (points[j] == null)
                throw new NullPointerException();
            if (0 == points[j].compareTo(points[j + 1]))
                throw new IllegalArgumentException();
        }

        if (np < 4) {
            segments = new LineSegment[0];
            return;
        }

        segments = null;
        int segidx = 0;
        LineSegment[] ls = new LineSegment[np - 3];

        for (int i = 0; i < np; i++) {
            for (int j = i + 1; j < np; j++) {
                for (int k = j + 1; k < np; k++) {
                    final int prevIdx = segidx;
                    for (int m = k + 1; m < np; m++) {
                        double s1 = points[i].slopeTo(points[j]);
                        double s2 = points[i].slopeTo(points[k]);
                        if (s1 != s2)
                            continue;

                        double s3 = points[i].slopeTo(points[m]);
                        if (s1 != s3)
                            continue;

                        ls[segidx++] = new LineSegment(points[i], points[m]);
                        break;
                    }

                    if (prevIdx != segidx)
                        break;
                }
            }
        }

        segments = new LineSegment[segidx];
        for (int i = 0; i < segidx; i++) {
            segments[i] = ls[i];
        }
    }

    public int numberOfSegments() {
        return segments.length;
    } // the number of line segments

    public LineSegment[] segments() {
        return Arrays.copyOf(segments, segments.length);
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

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

}
