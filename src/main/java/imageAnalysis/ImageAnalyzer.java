package imageAnalysis;

import digitRecognition.DigitRecognizer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageAnalyzer {

    // vars
    private final static double CONTRAST_BLACK = 0.7;
    public final static double DARKNESS_THRESHOLD = 0.5;
    private int topBuffer;
    private int bottomBuffer;
    private int leftBuffer;
    private int rightBuffer;
    private final int sudokuWidth;
    private final int sudokuHeight;
    private final int squareWidth;
    private final int squareHeight;

    // data
    private final BufferedImage image;
    private final BufferedImage[][] squares = new BufferedImage[9][9];
    private final int[][] values = new int[9][9];
    ImageHelper helper = new ImageHelper();
    DigitRecognizer recognizer;

    public ImageAnalyzer(BufferedImage image) {
        this.image = image;
        calcBuffer();
        increaseContrast();
        sudokuWidth = image.getWidth() - leftBuffer - rightBuffer;
        sudokuHeight = image.getHeight() - topBuffer - bottomBuffer;
        squareWidth = sudokuWidth / 9;
        squareHeight = sudokuHeight / 9;
        readSquares();
        cleanImages();
        setUpRecognizer();
        analyse();
//        show();
    }

    public int[][] getValues() {
        return values;
    }

    private void cleanImages() {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                squares[row][column] = helper.cleanImage(squares[row][column]);
            }
        }
    }

    private void analyse() {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                BufferedImage image = squares[row][column];
                recognizer.setImage(image);
                values[row][column] = recognizer.analiseDigit();
            }
        }
    }

    private void setUpRecognizer() {
        int nonEmpty = 0;
        int pixelSum = 0;
        int widthSum = 0;
        int heightSum = 0;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                BufferedImage image = squares[row][column];
                if (!helper.isEmpty(image)) {
                    nonEmpty++;
                    pixelSum += helper.getPixel(image);
                    widthSum += image.getWidth();
                    heightSum += image.getHeight();
                }
            }
        }
        recognizer = new DigitRecognizer(pixelSum / nonEmpty, widthSum / nonEmpty, heightSum / nonEmpty);
    }

    private void increaseContrast() {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double brightness = ImageHelper.getBrightness(image.getRGB(x, y));
                if (brightness < CONTRAST_BLACK) {
                    image.setRGB(x, y, 0);
                } else {
                    image.setRGB(x, y, (int) Math.pow(2, 32));
                }
            }
        }
    }

    private void readSquares() {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                squares[row][column] = image.getSubimage(leftBuffer + column * squareWidth, topBuffer + row * squareHeight, squareWidth, squareHeight);
            }
        }
    }

    private void calcBuffer() {
        leftBuffer = bufferIterator(0).x;
        rightBuffer = image.getWidth() - bufferIterator(1).x;
        topBuffer = bufferIterator(2).y;
        bottomBuffer = image.getHeight() - bufferIterator(3).y;
    }

    /**
     * Finds where the sudoku starts.
     * Sides:
     * left = 0
     * right = 1
     * top = 2
     * bottom = 3
     * @param side
     * @return
     */
    private Point bufferIterator(int side) {
        int x = 0;
        int y = 0;
        int xIt = 0;
        int yIt = 0;
        switch (side) {
            case 0:
                x = 0;
                y = image.getHeight() / 2;
                xIt = 1;
                break;
            case 1:
                x = image.getWidth() - 1;
                y = image.getHeight() / 2;
                xIt = -1;
                break;
            case 2:
                x = image.getWidth() / 2;
                y = 0;
                yIt = 1;
                break;
            case 3:
                x = image.getWidth() / 2;
                y = image.getHeight() - 1;
                yIt = -1;
                break;
        }

        double brightness = ImageHelper.getBrightness(image.getRGB(x, y));
        while (brightness > DARKNESS_THRESHOLD) {
            x += xIt;
            y += yIt;
            brightness = ImageHelper.getBrightness(image.getRGB(x, y));
        }
        return new Point(x, y);
    }

    private void show() {
        JFrame frame = new JFrame();
        frame.setLayout(null);
        frame.setSize(image.getWidth() + 50, image.getHeight() + 50);
        JLabel label = new JLabel(new ImageIcon(image));
        label.setSize(frame.getWidth(), frame.getHeight());
        label.setLocation(0, 0);
        frame.add(label);
        frame.setVisible(true);
    }
}
