package com.photo_shop.cmpt820.algorithm;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class GrayScale implements ImageOperation {
    @Override
    public Image apply(Image input) {
        int width = (int) input.getWidth();
        int height = (int) input.getHeight();

        // Create a new writable image
        WritableImage output = new WritableImage(width, height);

        // Get the pixel reader from the input image
        PixelReader pixelReader = input.getPixelReader();

        // Get the pixel writer for the output image
        PixelWriter pixelWriter = output.getPixelWriter();

        // Iterate over all pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Read the color of the current pixel
                Color color = pixelReader.getColor(x, y);

                // Calculate the average value of RGB components
                double gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

                // Create a new Color object with the gray value
                Color grayColor = new Color(gray, gray, gray, color.getOpacity());

                // Write the new color to the output image
                pixelWriter.setColor(x, y, grayColor);
            }
        }

        // Return the grayscale image
        return output;
    }
}

