package com.photo_shop.cmpt820.algorithm;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class SobelEdgeDetection implements ImageOperation {
    @Override
    public Image apply(Image input) {
        int width = (int) input.getWidth();
        int height = (int) input.getHeight();

        WritableImage output = new WritableImage(width, height);
        PixelReader pixelReader = input.getPixelReader();
        PixelWriter pixelWriter = output.getPixelWriter();

        int[][] Gx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] Gy = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                double pxGx = 0;
                double pxGy = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        Color color = pixelReader.getColor(x + j, y + i);
                        double intensity = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                        pxGx += intensity * Gx[i + 1][j + 1];
                        pxGy += intensity * Gy[i + 1][j + 1];
                    }
                }

                double gradientMagnitude = Math.sqrt(pxGx * pxGx + pxGy * pxGy);
                double normalizedMagnitude = Math.min(1, gradientMagnitude);

                pixelWriter.setColor(x, y, new Color(normalizedMagnitude, normalizedMagnitude, normalizedMagnitude, 1));
            }
        }

        return output;
    }
}

