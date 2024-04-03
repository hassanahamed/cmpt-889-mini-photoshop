package com.photo_shop.cmpt820.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageUtils {


    public static int[] calculateHistogram(Image grayscaleImage) {
        int[] histogram = new int[256]; // For 256 grayscale levels
        PixelReader reader = grayscaleImage.getPixelReader();
        int width = (int) grayscaleImage.getWidth();
        int height = (int) grayscaleImage.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                int value = (int) (color.getRed() * 255); // Assuming grayscale so R=G=B
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
                entropy -= probability * (Math.log(probability) / Math.log(2)); // Using log base 2
            }
        }
        return entropy;
    }

    public static double calculateAverageHuffmanCodeLength(int[] histogram, int totalPixels) {
        // This method would involve:
        // 1. Building the Huffman tree based on the frequencies in the histogram.
        // 2. Traversing the tree to calculate the weighted average length of Huffman codes.
        // For the sake of this example, let's assume we return a placeholder value.
        return 0; // Placeholder for demonstration purposes.
    }



}
