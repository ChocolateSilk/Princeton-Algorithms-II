import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import java.lang.Math;

public class SeamCarver {
    private Picture picture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
    }
 
    // current picture
    public Picture picture() {
        return new Picture(this.picture);
    }
 
    // width of current picture
    public int width() {
        return picture.width();
    }
 
    // height of current picture
    public int height() {
        return picture.height();
    }
 
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IllegalArgumentException("Pixel coordinates out of bounds");
        }

        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
            return 1000.0;
        }
        return calculateEnergy(x, y);
    }

    private double calculateEnergy(int x, int y) {
        Color left = picture.get(x - 1, y);
        Color right = picture.get(x + 1, y);
        Color above = picture.get(x, y - 1);
        Color below = picture.get(x, y + 1);

        double deltaXRed = Math.pow(right.getRed() - left.getRed(), 2);
        double deltaXGreen = Math.pow(right.getGreen() - left.getGreen(), 2);
        double deltaXBlue = Math.pow(right.getBlue() - left.getBlue(), 2);
        double deltaX = deltaXRed + deltaXGreen + deltaXBlue;

        double deltaYRed = Math.pow(below.getRed() - above.getRed(), 2);
        double deltaYGreen = Math.pow(below.getGreen() - above.getGreen(), 2);
        double deltaYBlue = Math.pow(below.getBlue() - above.getBlue(), 2);
        double deltaY = deltaYRed + deltaYGreen + deltaYBlue;

        return Math.sqrt(deltaX + deltaY);
    }
 
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        Picture rotate = rotateRight();
        SeamCarver result = new SeamCarver(rotate);
        int[] seam = result.findVerticalSeam();
        int origWidth = width();

        int[] horizontalSeam = new int[origWidth];

        for (int r = 0; r < origWidth; r++) {
            horizontalSeam[origWidth - 1 - r] = seam[r];
        }
        return horizontalSeam; 
    }

    private Picture rotateRight() {
        int width = width();
        int height = height();
        Picture rotate = new Picture(height, width);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                rotate.set(y, width - 1 - x, picture.get(x, y));
            }
        }
        return rotate;
    }
    private Picture rotateLeft() {
        int width = width();
        int height = height();
        Picture rotate = new Picture(height, width);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                rotate.set(height - 1 - y, x, picture.get(x, y));
            }
        }
        return rotate;
    }
 
    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int width = width();
        int height = height();
        double[][] dist = new double[width][height];
        int[][] edgeTo = new int[width][height];

        for (int x = 0; x < width; x++) {
            dist[x][0] = energy(x, 0);
        }
        for (int y = 1; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int bestPrevX = x;
                double minEnergy = dist[x][y - 1];
                if (x > 0 && dist[x - 1][y - 1] < minEnergy) {
                    minEnergy = dist[x - 1][y - 1];
                    bestPrevX = x - 1;
                }
                if (x < width - 1 && dist[x + 1][y - 1] < minEnergy) {
                    minEnergy = dist[x + 1][y - 1];
                    bestPrevX = x + 1;
                }
                dist[x][y] = energy(x, y) + minEnergy;
                edgeTo[x][y] = bestPrevX;
            }
        }
        double minPathEnergy = Double.POSITIVE_INFINITY;
        int minEndX = 0;
        for (int x = 0; x < width; x++) {
            if (dist[x][height - 1] < minPathEnergy) {
                minPathEnergy = dist[x][height - 1];
                minEndX = x;
            }
        }
        int[] seam = new int[height];
        seam[height - 1] = minEndX;
        for (int y = height - 1; y > 0; y--) {
            seam[y - 1] = edgeTo[seam[y]][y];
        }
    
        return seam;

    }
 
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width()) {
            throw new IllegalArgumentException("Seam length must match image width.");
        }
        if (height() <= 1) {
            throw new IllegalArgumentException("Image height must be greater than 1.");
        }
        int origWidth = width();
        int[] rotatedSeam = new int[origWidth];
        for (int r = 0; r < origWidth; r++) {
            rotatedSeam[r] = seam[origWidth - 1 - r];
        }
        Picture rotated = rotateRight();
        SeamCarver scRotated = new SeamCarver(rotated);
        scRotated.removeVerticalSeam(rotatedSeam);
        this.picture = scRotated.rotateLeft();
    }
 
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height()) {
            throw new IllegalArgumentException("Seam length must match image height.");
        }
        if (width() <= 1) {
            throw new IllegalArgumentException("Image width must be greater than 1.");
        }
        Picture result = new Picture(width() - 1, height());
        for (int y = 0; y < height(); y++) {
            int seamX = seam[y];
            for (int x = 0; x < seamX; x++) {
                result.set(x, y, picture.get(x, y));
            }
            for (int x0 = seamX + 1; x0 < width(); x0++) {
                result.set(x0 - 1, y, picture.get(x0, y));
            }
        }
        this.picture = result;
    }
 
    //  unit testing (optional)
    public static void main(String[] args) {
        return;
    }

 }
