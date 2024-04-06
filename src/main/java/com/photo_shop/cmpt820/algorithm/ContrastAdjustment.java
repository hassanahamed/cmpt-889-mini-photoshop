package com.photo_shop.cmpt820.algorithm;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class ContrastAdjustment implements ImageOperation {

    private double contrastLevel;

    public ContrastAdjustment(double contrastLevel) {
        this.contrastLevel = contrastLevel;
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
                Color originalColor = pixelReader.getColor(x, y);
                Color adjustedColor = adjustContrast(originalColor, contrastLevel);
                pixelWriter.setColor(x, y, adjustedColor);
            }
        }

        return outputImage;
    }

    private Color adjustContrast(Color color, double contrastFactor) {
        double r = adjustChannel(color.getRed(), contrastFactor);
        double g = adjustChannel(color.getGreen(), contrastFactor);
        double b = adjustChannel(color.getBlue(), contrastFactor);
        return new Color(r, g, b, color.getOpacity());
    }

    private double adjustChannel(double colorValue, double contrastFactor) {
        return clamp(((colorValue - 0.5) * contrastFactor) + 0.5);
    }

    private double clamp(double value) {
        if (value < 0) return 0;
        if (value > 1) return 1;
        return value;
    }
}



