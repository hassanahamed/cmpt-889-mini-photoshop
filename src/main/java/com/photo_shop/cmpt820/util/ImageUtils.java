package com.photo_shop.cmpt820.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageUtils {

    /**
     * Converts the given image to grayscale.
     * @param image The original image to convert.
     * @return A new Image object in grayscale.
     */
    public static Image convertToGrayscale(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        WritableImage grayImage = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();
        PixelWriter pixelWriter = grayImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                double grayLevel = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                Color grayColor = new Color(grayLevel, grayLevel, grayLevel, color.getOpacity());
                pixelWriter.setColor(x, y, grayColor);
            }
        }

        return grayImage;
    }
}
