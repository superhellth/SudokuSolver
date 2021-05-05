package digitRecognition;

import imageAnalysis.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class DigitRecognizer {

    // image and subimages
    private BufferedImage image;
    private BufferedImage leftImage;
    private BufferedImage rightImage;
    private BufferedImage topImage;
    private BufferedImage bottomImage;

    // vars
    private final int avgPixel;
    private final int avgWidth;
    private final int avgHeight;

    // help
    private final ImageHelper helper = new ImageHelper();

    // number weights
    private final double[] weights = new double[9];

    public DigitRecognizer(int avgPixel, int avgWidth, int avgHeight) {
        this.avgPixel = avgPixel;
        this.avgWidth = avgWidth;
        this.avgHeight = avgHeight;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int analiseDigit() {
        Arrays.fill(weights, 0);
        if (helper.isEmpty(image)) {
            return 0;
        }
        extractSubImages();
//        showSubImages();

        // total weight: 1
        if (hasUpperBar() && !getsBiggerTop()) {
            changeWeights(-10, 0, 0, -10, 0.5, 0, 0.5, 0, 0);
        }
        // total weight: 1
        if (hasLowerBar() && !getsBiggerBottom()) {
            changeWeights(0.2, 0.8, 0, -10, 0, 0, -10, 0, 0);
        }
        // total weight: 1
        if (hasMoreHigh()) {
            changeWeights(0, 0, 0, 0.33, 0, -0.5, 0.33, 0, 0.33);
        }
        if (hasMoreLeft()) {
            changeWeights(0, 0, -0.4, 0, 0, 0.5, 0, 0, -0.5);
        }
        // total weight: 1
        if (isVerSymmetrical()) {
            changeWeights(-0.2, -10, 0.2, -10, -10, 0.2, -10, 0.6, 0.2);
        } else {
            changeWeights(0, 0, 0, 0, 0, 0, 0, -0.5, 0);
        }
        // total weight: 1
        if (hasLessPixel()) {
            changeWeights(1, 0, 0, 0, 0, 0, 0.3, -10, 0);
        }
        // total weight: 1
        if (isTighter()) {
            changeWeights(1, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        // total weight: 1
        if (splitsDown()) {
            changeWeights(-0.1, 0, 0.25, -0.1, 0.25, 0.25, -0.1, 0.25, 0);
        }
        // total weight: 1
        if (splitsUp()) {
            changeWeights(0.16, 0.16, 0.16, 0.16, 0, 0.16, 0, 0.16, 0.16);
        }
        // total weight: 1
        if (splitsLeft()) {
            changeWeights(0, 0.12, 0.13, 0.12, 0.12, 0.12, 0, 0.12, 0.12);
        }
        if (hasTrappedWhite()) {
            changeWeights(-10, -0.5, -0.5, 0.2, -0.5, 0.2, -10, 0.2, 0.2);
        }
        if (!hasDoubleEnds()) {
            changeWeights(0.33, -0.33, -0.33, 0.33, -0.33, -0.1, 0.33, -0.33, -0.1);
        }
        if (getsBiggerTop()) {
            changeWeights(0, 0.2, 0.2, 0.2, -0.4, 0.2, 0, 0.2, 0.2);
        }
        if (getsBiggerBottom()) {
            changeWeights(-0.33, 0, 0, -0.33, 0, 0, 0, 0, 0);
        }
        if (getMostProbable() == 6 || getMostProbable() == 9) {
            if (canDrawToMiddle(topImage)) {
                changeWeights(0, 0, 0, 0, 0, 0.3, 0, 0, -0.3);
            }
            if (canDrawToMiddle(bottomImage)) {
                changeWeights(0, 0, 0, 0, 0, -0.3, 0, 0, 0.3);
            }
        }

        return getMostProbable();
    }

    private void changeWeights(double one, double two, double three, double four, double five, double six, double seven, double eight, double nine) {
        int i = 0;
        weights[i++] += one;
        weights[i++] += two;
        weights[i++] += three;
        weights[i++] += four;
        weights[i++] += five;
        weights[i++] += six;
        weights[i++] += seven;
        weights[i++] += eight;
        weights[i] += nine;
    }

    private int getMostProbable() {
        int maxI = 0;
        for (int j = 1; j < weights.length; j++) {
            if (weights[maxI] < weights[j]) {
                maxI = j;
            }
        }
        return maxI + 1;
    }

    public boolean canDrawToMiddle(BufferedImage anImage) {
        for (int y = 2; y < anImage.getHeight() - 2; y++) {
            int x = 0;
            while (!ImageHelper.isDark(anImage.getRGB(x, y)) && x < anImage.getWidth()) {
                x++;
            }
            if (x > anImage.getWidth() / 2) {
                return true;
            }
            int x2 = anImage.getWidth() - 1;
            while (!ImageHelper.isDark(anImage.getRGB(x2, y)) && x2 > 0) {
                x2--;
            }
            if (x2 < anImage.getWidth() / 2) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDoubleEnds() {
        boolean endFound = false;
        int pixel = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            int rgb = image.getRGB(4, y);
            if (ImageHelper.isDark(rgb)) {
                pixel++;
            } else if (pixel > 2 && !endFound) {
                endFound = true;
                pixel = 0;
            } else if (pixel > 2) {
                return true;
            }
        }
        endFound = false;
        pixel = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            int rgb = image.getRGB(image.getWidth() - 5, y);
            if (ImageHelper.isDark(rgb)) {
                pixel++;
            } else if (pixel > 2 && !endFound) {
                endFound = true;
                pixel = 0;
            } else if (pixel > 2) {
                return true;
            }
        }
        return false;
    }

    private boolean getsBiggerTop() {
        int iterateTo = image.getHeight() / 6 + 2;
        int[] arr = new int[iterateTo];
        for (int y = 0; y < iterateTo; y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (ImageHelper.isDark(image.getRGB(x, y))) {
                    arr[y]++;
                }
            }
        }
        int gotBigger = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] - arr[i + 1] < -1) {
                gotBigger++;
            }
        }
        return gotBigger > image.getHeight() / 10   ;
    }

    private boolean getsBiggerBottom() {
        int iterateTo = image.getHeight() / 6 + 2;
        int[] arr = new int[iterateTo];
        for (int y = 0; y < iterateTo; y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (ImageHelper.isDark(image.getRGB(x, image.getHeight() - 1 - y))) {
                    arr[y]++;
                }
            }
        }
        int gotBigger = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] - arr[i + 1] < -1) {
                gotBigger++;
            }
        }
        return gotBigger > image.getHeight() / 10;
    }

    private boolean hasTrappedWhite() {
        // paint from the outside
        Color color = new Color(184, 10, 10);
        int toColor = color.getRGB();
        for (int y = 0; y < image.getHeight(); y++) {
            int currentX = 0;
            while (!ImageHelper.isDark(image.getRGB(currentX, y)) && currentX < image.getWidth() - 1) {
                image.setRGB(currentX, y, toColor);
                currentX++;
            }
        }
        for (int y = 0; y < image.getHeight(); y++) {
            int currentX = image.getWidth() - 1;
            while (!ImageHelper.isDark(image.getRGB(currentX, y)) && currentX > 0) {
                image.setRGB(currentX, y, toColor);
                currentX--;
            }
        }
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                boolean upperReached = false;
                boolean lowerReached = false;
                if (image.getRGB(x, y) == toColor) {
                    for (int yb = 1; yb < image.getHeight(); yb++) {
                        for (int i = -1; i < 2; i += 2) {
                            int currentY = y + i * yb;
                            if (currentY >= 0 && currentY < image.getHeight()) {
                                if (!ImageHelper.isDark(image.getRGB(x, currentY))) {
                                    if ((i < 0 && !lowerReached) || (i > 0 && !upperReached)) {
                                        image.setRGB(x, currentY, toColor);
                                    }
                                } else {
                                    if (i < 0) {
                                        lowerReached = true;
                                    } else {
                                        upperReached = true;
                                    }
                                }
                            }
                        }
                        if (upperReached && lowerReached) {
                            break;
                        }
                    }
                }
            }
        }
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                boolean rightReached = false;
                boolean leftReached = false;
                if (image.getRGB(x, y) == toColor) {
                    for (int xb = 1; xb < image.getWidth(); xb++) {
                        for (int i = -1; i < 2; i += 2) {
                            int currentX = x + i * xb;
                            if (currentX >= 0 && currentX < image.getWidth()) {
                                if (!ImageHelper.isDark(image.getRGB(currentX, y))) {
                                    if ((i < 0 && !leftReached) || (i > 0 && !rightReached)) {
                                        image.setRGB(currentX, y, toColor);
                                    }
                                } else {
                                    if (i < 0) {
                                        leftReached = true;
                                    } else {
                                        rightReached = true;
                                    }
                                }
                            }
                        }
                        if (rightReached && leftReached) {
                            break;
                        }
                    }
                }
            }
        }

        // count white pixels
        int totalPixels = image.getHeight() * image.getWidth();
        int white = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (image.getRGB(x, y) != toColor && !ImageHelper.isDark(image.getRGB(x, y))) {
                    white++;
                }
            }
        }

        // undo painting
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (image.getRGB(x, y) == toColor) {
                    image.setRGB(x, y, (int) Math.pow(2, 32));
                }
            }
        }
        return white > totalPixels * 0.02;
    }

    private boolean splitsLeft() {
        int columns = 0;
        int upTo = image.getWidth() / 2;
        for (int x = 0; x < upTo; x++) {
            columns = getColumns(columns, x);
        }
        return columns > image.getWidth() / 5;
    }

    private boolean splitsRight() {
        int columns = 0;
        int upTo = image.getWidth() - image.getWidth() / 2;
        for (int x = image.getWidth() - 1; x > upTo; x--) {
            columns = getColumns(columns, x);
        }
        return columns > image.getWidth() / 5;
    }

    private boolean splitsUp() {
        int rows = 0;
        int upTo = image.getHeight() / 2;
        for (int y = 0; y < upTo; y++) {
            rows = getRows(rows, y);
        }
        return rows > image.getHeight() / 5;
    }

    private boolean splitsDown() {
        int rows = 0;
        int upTo = image.getHeight() - image.getHeight() / 2;
        for (int y = image.getHeight() - 1; y > upTo; y--) {
            rows = getRows(rows, y);
        }
        return rows > image.getHeight() / 5;
    }

    private boolean hasLessPixel() {
        return helper.getPixel(image) < avgPixel * 0.9;
    }

    private boolean isTighter() {
        return image.getWidth() < avgWidth * 0.8;
    }

    private boolean isSmaller() {
        return image.getHeight() < avgHeight;
    }

    private boolean hasMoreHigh() {
        ImageHelper helper = new ImageHelper();
        int topPixel = helper.getPixel(topImage);
        int bottomPixel = helper.getPixel(bottomImage);
        return topPixel > 0.52 * (bottomPixel + topPixel);
    }

    private boolean hasMoreLeft() {
        int leftPixel = helper.getPixel(leftImage);
        int rightPixel = helper.getPixel(rightImage);
        return leftPixel > 0.52 * (rightPixel + leftPixel);
    }

    private boolean hasLowerBar() {
        double checkRatio = 0.67;
        int missed = 0;
        int buffer = (int) (image.getWidth() * ((1 - checkRatio) / 2));
        int rows = 0;
        for (int y = image.getHeight() - 1; y > image.getHeight() - image.getHeight() / 10 - 2; y--) {
            for (int x = buffer; x < image.getWidth() - buffer; x++) {
                if (!ImageHelper.isDark(image.getRGB(x, y))) {
                    missed++;
                }
            }
            if (missed < image.getWidth() / 20 + 1) {
                rows++;
            }
            missed = 0;
        }
        return rows > image.getHeight() / 33;
    }

    private boolean hasUpperBar() {
        double checkRatio = 0.67;
        int missed = 0;
        int buffer = (int) (image.getWidth() * ((1 - checkRatio) / 2));
        int rows = 0;
        for (int y = 1; y < image.getHeight() / 10 + 2; y++) {
            for (int x = buffer; x < image.getWidth() - buffer; x++) {
                if (!ImageHelper.isDark(image.getRGB(x, y))) {
                    missed++;
                }
            }
            if (missed < image.getWidth() / 20 + 1) {
                rows++;
            }
            missed = 0;
        }
        return rows > image.getHeight() / 33;
    }

    private boolean isVerSymmetrical() {
        for (int x = 0; x < leftImage.getWidth(); x++) {
            for (int y = 0; y < leftImage.getHeight(); y++) {
                int xBuffer = image.getWidth() / 14;
                boolean correspondingFound = false;
                for (int bx = -xBuffer; bx <= xBuffer; bx++) {
                    int yBuffer = image.getHeight() / 11;
                    for (int by = -yBuffer; by < yBuffer; by++) {
                        int invertedX = leftImage.getWidth() - 1 - x;
                        if (invertedX + bx >= 0 && invertedX + bx < rightImage.getWidth() && y + by >= 0 && y + by < rightImage.getHeight()) {
                            if (ImageHelper.isDark(leftImage.getRGB(x, y)) == ImageHelper.isDark(rightImage.getRGB(invertedX + bx, y + by))) {
                                correspondingFound = true;
                            }
                        }
                    }
                }
                if (!correspondingFound) {
                    return false;
                }
            }
        }
        return true;
    }

    private int getRows(int rows, int y) {
        boolean oneBlack = false;
        boolean oneWhite = false;
        for (int x = 0; x < image.getWidth(); x++) {
            if (ImageHelper.isDark(image.getRGB(x, y))) {
                if (oneBlack) {
                    if (oneWhite) {
                        rows++;
                    }
                } else {
                    oneBlack = true;
                }
            } else {
                if (oneBlack) {
                    oneWhite = true;
                }
            }
        }
        return rows;
    }

    private int getColumns(int columns, int x) {
        boolean oneBlack = false;
        boolean oneWhite = false;
        for (int y = 0; y < image.getHeight(); y++) {
            if (ImageHelper.isDark(image.getRGB(x, y))) {
                if (oneBlack) {
                    if (oneWhite) {
                        columns++;
                    }
                } else {
                    oneBlack = true;
                }
            } else {
                if (oneBlack) {
                    oneWhite = true;
                }
            }
        }
        return columns;
    }

    private void extractSubImages() {
        // left / right
        boolean hasEvenWidth = image.getWidth() % 2 == 0;
        int subImageWidth = image.getWidth() / 2;
        leftImage = image.getSubimage(0, 0, subImageWidth, image.getHeight());
        int rightImageStart = hasEvenWidth ? subImageWidth : subImageWidth + 1;
        rightImage = image.getSubimage(rightImageStart, 0, subImageWidth, image.getHeight());

        // top / bottom
        boolean hasEvenHeight = image.getHeight() % 2 == 0;
        int subImageHeight = image.getHeight() / 2;
        topImage = image.getSubimage(0, 0, image.getWidth(), subImageHeight);
        int bottomImageStart = hasEvenHeight ? subImageHeight : subImageHeight + 1;
        bottomImage = image.getSubimage(0, bottomImageStart, image.getWidth(), subImageHeight);
    }

    public void show() {
        JFrame frame = new JFrame();
        frame.setSize(200, 700);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JLabel label = new JLabel(new ImageIcon(image));
        label.setSize(150, 150);
        label.setOpaque(true);
        label.setBackground(Color.RED);
        label.setLocation(0, 0);
        frame.add(label);
        frame.setVisible(true);
        JLabel buffer = new JLabel("<html>"
                + "Guess: " + getMostProbable() + "<br>"
                + "Total Pixels: " + helper.getPixel(image) + "<br>"
                + "Pixels top: " + helper.getPixel(topImage) + "<br>"
                + "Pixels bottom: " + helper.getPixel(bottomImage) + "<br>"
                + "Has less pixels: " + hasLessPixel() + "<br>"
                + "Is vertically symmetrical: " + isVerSymmetrical() + "<br>"
                + "Has upper bar: " + hasUpperBar() + "<br>"
                + "Has lower bar: " + hasLowerBar() + "<br>"
                + "Is tight: " + isTighter() + "<br>"
                + "Trapped white: " + hasTrappedWhite() + "<br>"
                + "Has more high: " + hasMoreHigh() + "<br>"
                + "Has more left: " + hasMoreLeft() + "<br>"
                + "Has double ends: " + hasDoubleEnds() + "<br>"
                + "Splits up: " + splitsUp() + "<br>"
                + "Splits down: " + splitsDown() + "<br>"
                + "Splits left: " + splitsLeft() + "<br>"
                + "Splits right: " + splitsRight() + "<br>"
                + "Gets bigger top: " + getsBiggerTop() + "<br>"
                + "Gets bigger bottom: " + getsBiggerBottom() + "<br>"
                + "Probabilities: " + "<br>"
                + "one: " + weights[0] + "<br>"
                + "two: " + weights[1] + "<br>"
                + "three: " + weights[2] + "<br>"
                + "four: " + weights[3] + "<br>"
                + "five: " + weights[4] + "<br>"
                + "six: " + weights[5] + "<br>"
                + "seven: " + weights[6] + "<br>"
                + "eight: " + weights[7] + "<br>"
                + "nine: " + weights[8] + "<br>"
                + "</html>");
        buffer.setSize(200, 475);
        buffer.setLocation(0, label.getY() + label.getHeight());
        frame.add(buffer);
    }

    private void showSubImages() {
        DigitRecognizer recognizer = new DigitRecognizer(avgPixel, avgWidth, avgHeight);
        recognizer.setImage(leftImage);
        recognizer.show();
        recognizer.setImage(rightImage);
        recognizer.show();
        recognizer.setImage(topImage);
        recognizer.show();
        recognizer.setImage(bottomImage);
        recognizer.show();
    }

}
