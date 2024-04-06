package com.photo_shop.cmpt820.algorithm;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class BrightnessAdjustment implements ImageOperation {

    private final double brightnessFactor;

    public BrightnessAdjustment(double brightnessFactor) {
        this.brightnessFactor = brightnessFactor;
    }

    @Override
    public Image apply(Image inputImage) {
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();
        WritableImage outputImage = new WritableImage(width, height);
        PixelReader pixelReader = inputImage.getPixelReader();
        PixelWriter pixelWriter = outputImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                double red = clamp(color.getRed() * brightnessFactor);
                double green = clamp(color.getGreen() * brightnessFactor);
                double blue = clamp(color.getBlue() * brightnessFactor);
                pixelWriter.setColor(x, y, new Color(red, green, blue, color.getOpacity()));
            }
        }

        return outputImage;
    }

    private double clamp(double value) {
        if (value < 0) return 0;
        if (value > 1) return 1;
        return value;
    }
}


