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
        WritableImage output = new WritableImage(width, height);

        PixelReader pixelReader = input.getPixelReader();

        PixelWriter pixelWriter = output.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                double gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3;

                Color grayColor = new Color(gray, gray, gray, color.getOpacity());

                pixelWriter.setColor(x, y, grayColor);
            }
        }

        return output;
    }
}

