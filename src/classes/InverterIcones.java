/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author wynvern
 */
public class InverterIcones {
    public static void invertIconsInFolder(String folderPath) {
        File folder = new File(folderPath);

        // Check if the path is a directory
        if (folder.isDirectory()) {
            // List all files in the directory
            File[] files = folder.listFiles();

            if (files != null) {
                // Iterate through each file
                for (File file : files) {
                    // Check if it is a PNG file
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".png")) {
                        // Invert colors and save the updated image
                        invertAndSaveImage(file);
                    }
                }
            }
        } else {
            System.err.println("The provided path is not a directory.");
        }
    }

    private static void invertAndSaveImage(File file) {
        try {
            // Read the original image
            BufferedImage originalImage = ImageIO.read(file);

            // Create an ImageFilter to invert colors
            ImageFilter colorInverter = new RGBImageFilter() {
                @Override
                public int filterRGB(int x, int y, int rgb) {
                    // Invert the colors by XOR-ing with 0xFFFFFF (white)
                    return rgb ^ 0xFFFFFF;
                }
            };

            // Apply the ImageFilter to create the inverted image
            ImageProducer producer = new FilteredImageSource(originalImage.getSource(), colorInverter);
            Image invertedImage = Toolkit.getDefaultToolkit().createImage(producer);

            // Convert the Image back to a BufferedImage
            BufferedImage invertedBufferedImage = new BufferedImage(
                    invertedImage.getWidth(null),
                    invertedImage.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB
            );

            Graphics2D g = invertedBufferedImage.createGraphics();
            g.drawImage(invertedImage, 0, 0, null);
            g.dispose();

            // Save the inverted image back to the file
            ImageIO.write(invertedBufferedImage, "png", file);

            System.out.println("Inverted colors for: " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
