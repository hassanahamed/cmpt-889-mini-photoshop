package com.photo_shop.cmpt820.algorithm;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class OrderedDithering implements ImageOperation {

    private final int[][] ditherMatrix = {
            { 0,  8,  2, 10},
            {12,  4, 14,  6},
            { 3, 11,  1,  9},
            {15,  7, 13,  5}
    };
    private final int matrixSize = 4; // Size of the dither matrix
    private final double scale = 17.0; // Scale factor for dithering (matrixSize^2 + 1)

    @Override
    public Image apply(Image input) {
        int width = (int) input.getWidth();
        int height = (int) input.getHeight();
        WritableImage output = new WritableImage(width, height);
        PixelReader reader = input.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double gray = color.getRed(); // Assuming the image is grayscale, R=G=B

                // Calculate the dithered pixel value
                int matrixValue = ditherMatrix[y % matrixSize][x % matrixSize];
                double ditheredGray = gray * scale;

                // Apply the dithering threshold
                if (ditheredGray > matrixValue) {
                    writer.setColor(x, y, Color.WHITE);
                } else {
                    writer.setColor(x, y, Color.BLACK);
                }
            }
        }

        return output;
    }
}

