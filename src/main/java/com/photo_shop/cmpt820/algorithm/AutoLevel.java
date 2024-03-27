package com.photo_shop.cmpt820.algorithm;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.function.Function;

public class AutoLevel implements ImageOperation{

    @Override
    public Image apply(Image input) {
        int width = (int) input.getWidth();
        int height = (int) input.getHeight();
        WritableImage output = new WritableImage(width, height);
        PixelReader reader = input.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        // Calculate histograms
        int[] histogramR = calculateHistogram(reader, width, height, Color::getRed);
        int[] histogramG = calculateHistogram(reader, width, height, Color::getGreen);
        int[] histogramB = calculateHistogram(reader, width, height, Color::getBlue);

        // Calculate CDFs
        double[] cdfR = calculateCDF(histogramR);
        double[] cdfG = calculateCDF(histogramG);
        double[] cdfB = calculateCDF(histogramB);

        // Normalize CDFs
        normalizeCDF(cdfR, width * height);
        normalizeCDF(cdfG, width * height);
        normalizeCDF(cdfB, width * height);

        // Map original colors to stretched ones
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color originalColor = reader.getColor(x, y);
                Color newColor = mapColor(originalColor, cdfR, cdfG, cdfB);
                writer.setColor(x, y, newColor);
            }
        }

        return output;
    }

    int[] calculateHistogram(PixelReader reader, int width, int height, Function<Color, Double> channelSelector) {
        int[] histogram = new int[256];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                int value = (int) (channelSelector.apply(color) * 255);
                histogram[value]++;
            }
        }
        return histogram;
    }

    double[] calculateCDF(int[] histogram) {
        int sum = 0;
        double[] cdf = new double[256];
        for (int i = 0; i < histogram.length; i++) {
            sum += histogram[i];
            cdf[i] = sum;
        }
        return cdf;
    }

    void normalizeCDF(double[] cdf, int totalPixels) {
        double minCdf = Arrays.stream(cdf).min().orElse(0);
        for (int i = 0; i < cdf.length; i++) {
            cdf[i] = (cdf[i] - minCdf) / (totalPixels - minCdf);
        }
    }

    Color mapColor(Color originalColor, double[] cdfR, double[] cdfG, double[] cdfB) {
        double newR = cdfR[(int) (originalColor.getRed() * 255)];
        double newG = cdfG[(int) (originalColor.getGreen() * 255)];
        double newB = cdfB[(int) (originalColor.getBlue() * 255)];
        return new Color(newR, newG, newB, originalColor.getOpacity());
    }



}
