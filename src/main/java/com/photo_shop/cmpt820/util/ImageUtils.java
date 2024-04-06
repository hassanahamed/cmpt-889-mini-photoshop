package com.photo_shop.cmpt820.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageUtils {


    public static int[] calculateHistogram(Image grayscaleImage) {
        int[] histogram = new int[256];
        PixelReader reader = grayscaleImage.getPixelReader();
        int width = (int) grayscaleImage.getWidth();
        int height = (int) grayscaleImage.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                int value = (int) (color.getRed() * 255);
                histogram[value]++;
            }
        }
        return histogram;
    }

    public static double calculateEntropy(int[] histogram, int totalPixels) {
        double entropy = 0;
        for (int count : histogram) {
            if (count > 0) {
                double probability = (double) count / totalPixels;
                entropy -= probability * (Math.log(probability) / Math.log(2));
            }
        }
        return entropy;
    }




}
