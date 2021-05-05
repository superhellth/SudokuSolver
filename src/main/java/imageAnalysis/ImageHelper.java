package imageAnalysis;

import java.awt.image.BufferedImage;

public class ImageHelper {

    public BufferedImage cleanImage(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight() / 6; y++) {
                image.setRGB(x, y, (int) Math.pow(2, 32));
                image.setRGB(x, image.getHeight() - 1 - y, (int) Math.pow(2, 32));
            }
        }
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth() / 6; x++) {
                image.setRGB(x, y, (int) Math.pow(2, 32));
                image.setRGB(image.getWidth() - 1 - x, y, (int) Math.pow(2, 32));
            }
        }
        if (!isEmpty(image)) {
            image = resizeProperly(image);
        }
        return image;
    }

    public boolean isEmpty(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (ImageHelper.isDark(image.getRGB(x, y))) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getPixel(BufferedImage image) {
        int sum = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (ImageHelper.isDark(image.getRGB(x, y))) {
                    sum++;
                }
            }
        }
        return sum;
    }

    public static boolean isDark(int rgb) {
        return getBrightness(rgb) < ImageAnalyzer.DARKNESS_THRESHOLD;
    }

    public static double getBrightness(int color) {
        // extract each color component
        int red   = (color >>> 16) & 0xFF;
        int green = (color >>>  8) & 0xFF;
        int blue  = (color) & 0xFF;

        // calc luminance in range 0.0 to 1.0; using SRGB luminance constants
        return (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;
    }

    private BufferedImage resizeProperly(BufferedImage image) {
        int leftBuffer = getBuffer(image, 0);
        int rightBuffer = image.getWidth() - getBuffer(image, 1);
        int topBuffer = getBuffer(image, 2);
        int bottomBuffer = image.getHeight() - getBuffer(image, 3);
        image = image.getSubimage(leftBuffer, topBuffer, image.getWidth() - leftBuffer - rightBuffer, image.getHeight() - topBuffer - bottomBuffer);
        return image;
    }

    private int getBuffer(BufferedImage image, int side) {
        int x = 0;
        int y = 0;
        int xIt = 0;
        int yIt = 0;
        switch (side) {
            case 0:
                xIt = 1;
                break;
            case 1:
                x = image.getWidth() - 1;
                xIt = -1;
                break;
            case 2:
                yIt = 1;
                break;
            case 3:
                y = image.getHeight() - 1;
                yIt = -1;
                break;
        }
        if (xIt == 0) {
            while (true) {
                for (int rowPixel = 0; rowPixel < image.getWidth(); rowPixel++) {
                    if (ImageHelper.isDark(image.getRGB(rowPixel, y))) {
                        return yIt == 1 ? y - 1 : y + 2;
                    }
                }
                y += yIt;
            }
        } else {
            while (true) {
                for (int columnPixel = 0; columnPixel < image.getHeight(); columnPixel++) {
                    if (ImageHelper.isDark(image.getRGB(x, columnPixel))) {
                        return xIt == 1 ? x - 1 : x + 2;
                    }
                }
                x += xIt;
            }
        }
    }
}
