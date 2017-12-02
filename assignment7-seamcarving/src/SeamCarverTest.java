import edu.princeton.cs.algs4.Picture;

public class SeamCarverTest {
    public static void main(String [] args) {
        Picture pict = new Picture("6x5.png");
        SeamCarver sc = new SeamCarver(pict);
        
        for(int y = 0; y < pict.height(); y++) {
            for(int x = 0; x < pict.width(); x++) {
                System.out.print(String.format("%.3f ", sc.energy(x, y)));
            }
            System.out.println();
        }
    }
}
