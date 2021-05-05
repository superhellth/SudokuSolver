package imageAnalysis;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageReader {

    public static BufferedImage getImage(String path) {
        BufferedImage image = null;
        URL url = ImageReader.class.getClassLoader().getResource(path);
        try {
            image = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

}
