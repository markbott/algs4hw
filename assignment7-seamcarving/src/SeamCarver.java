import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    //private Picture picture;
    private int [][] newPicture;
    private int w, h;
    
    public SeamCarver(Picture picture) {               // create a seam carver object based on the given picture
        if(picture == null) throw new IllegalArgumentException();
        //this.picture = new Picture(picture);
        this.w = picture.width();
        this.h = picture.height();
        this.newPicture = new int[w][h];
        
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                newPicture[x][y] = picture.getRGB(x, y);
            }
        }
        
    }

    public Picture picture() {              // current picture
        Picture p = new Picture(w,h);
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                p.setRGB(x, y, newPicture[x][y]);
            }
        }
        return p;
    }

    public     int width()  {               // width of current picture
        return w;
    }

    public     int height()             {             // height of current picture
        return h;
    }

    public  double energy(int x, int y) {             // energy of pixel at column x and row y
        
        if(x < 0 || x >= w || y < 0 || y >= h) { 
            throw new IllegalArgumentException();
        }
        
        // border is 1000
        if(x == 0 || y == 0 || x == w-1 || y == h-1) {
            return 1000;
        }
        
        // rgb & 0xFF = blue, rgb & 0xFF00 = green, rgb & 0xFF0000 = red
        int xGradSq = gradientSquared(newPicture[x - 1][y], newPicture[x + 1][y]);
        int yGradSq = gradientSquared(newPicture[x][y - 1], newPicture[x][y + 1]);
        
        return Math.sqrt(xGradSq + yGradSq);
        
    }

    static private int gradientSquared(int rgbInt1, int rgbInt2) {
        int [] rgb1 = { (rgbInt1 >> 16) & 0xFF, (rgbInt1 >> 8) & 0xFF, rgbInt1 & 0xFF };
        int [] rgb2 = { (rgbInt2 >> 16) & 0xFF, (rgbInt2 >> 8) & 0xFF, rgbInt2 & 0xFF };
        
        int result = 0;
        for(int i = 0; i < 3; i++) {
            int d = rgb1[i] - rgb2[i];
            result += d * d;
        }
        
        return result;
    }
    
    public   int[] findHorizontalSeam()   {             // sequence of indices for horizontal seam
        Picture trans = new Picture(h, w);
        
        for(int y = 0; y < h; y++) {
            for(int x = 0; x < w; x++) {
                trans.setRGB(y, x, newPicture[x][y]);
            }
        }
        
        return new SeamCarver(trans).findVerticalSeam();
    }

    private int edge(int x, int y, int w) {
        return y*w + x;
    }

    private void relax(DirectedEdge e, double [] distTo, DirectedEdge [] edgeTo) {
        final int v = e.from(), w = e.to();
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
        }
    }
    
    public   int[] findVerticalSeam()      {           // sequence of indices for vertical seam
        final int V = w * h;
        
        DirectedEdge [] edgeTo = new DirectedEdge[V];
        
        double[] distTo = new double[V];
        for (int v = 0; v < V; v++) {
            distTo[v] = Double.POSITIVE_INFINITY;
        }
        
        // TODO possible optimization - cache energyMatrix in the object and clear out
        // the impact pixels when removing seams
        double [][] energyMatrix = new double[w][h];
        for (int y = 0; y < h - 1; y++) {
            for(int x = 0; x < w; x++) {
                energyMatrix[x][y] = energy(x,y);
            }
        }
            
        for(int x = 0; x < w; x++) {
            distTo[x] = energyMatrix[x][0];
        }
        
        for (int y = 0; y < h - 1; y++) {
            for(int x = 0; x < w; x++) {
                for(int curX = x-1; curX <= x+1; curX++) {
                    if(curX < 0 || curX >= w) continue;

                    DirectedEdge e = new DirectedEdge(edge(x, y, w), edge(curX, y+1, w), energyMatrix[curX][y+1]);
                    relax(e, distTo, edgeTo);
                }
            }
            
            debug(distTo, edgeTo);
        }
        
        double minDist = Double.POSITIVE_INFINITY;
        int minX = -1;
        for(int x = 0; x < w; x++) {
            if(distTo[(h-1)*w + x] < minDist) {
                minDist = distTo[(h-1)*w + x];
                minX = x;
            }
        }
        
        int [] seam = new int[h];
        for(int y = h-1; y >= 0; y--) {
            seam[y] = minX;
            if(y > 0) minX = edgeTo[edge(minX, y, w)].from() % w;
        }
        
        return seam;
    }
    
    private void debug(double[] distTo, DirectedEdge[] edgeTo) {
        /*
        StdOut.println();
        StdOut.println();
        for(int y = 0; y < picture.height(); y++) {
            for(int x = 0; x < picture.width(); x++) {
                int xy = edge(x, y, picture.width());
                StdOut.printf("%7.2f (%d) ", distTo[xy], (edgeTo[xy] == null ? -1 : (edgeTo[xy].from() % picture.width())));
            }
            StdOut.println();
        }
        */
    }

    public    void removeHorizontalSeam(int[] seam) {  // remove horizontal seam from current picture
        removeHorizontalSeam(newPicture, w, h, seam);
        h -= 1;
    }
    
    static private void removeHorizontalSeam(int [][] pict, int width, int height, int [] seam) {
        if(seam == null || seam.length != width) throw new IllegalArgumentException();
        
        int prevSeam = -1;
        
        for(int x = 0; x < width; x++) {
            if(seam[x] < 0 || seam[x] >= height) throw new IllegalArgumentException();
            
            if(prevSeam != -1 && Math.abs(seam[x] - prevSeam) > 1) throw new IllegalArgumentException();
            
            System.arraycopy(pict[x], seam[x] + 1, pict[x], seam[x], height - seam[x] - 1);
            
            prevSeam = seam[x];
        }
    }
    
    public    void removeVerticalSeam(int[] seam)   {  // remove vertical seam from current picture
        
        int [][] trans = new int[h][w];
        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                trans[y][x] = newPicture[x][y];
            }
        }
    
        removeHorizontalSeam(trans, h, w, seam);
        
        w -= 1;

        for(int x = 0; x < w; x++) {
            for(int y = 0; y < h; y++) {
                newPicture[x][y] = trans[y][x];
            }
        }
    }
}