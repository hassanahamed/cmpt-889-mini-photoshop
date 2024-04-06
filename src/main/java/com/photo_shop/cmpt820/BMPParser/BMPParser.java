package com.photo_shop.cmpt820.BMPParser;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BMPParser {

    public static Image parseBMP(File file) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(10);
            int pixelDataOffset = Integer.reverseBytes(raf.readInt());

            raf.seek(18);
            int width = Integer.reverseBytes(raf.readInt());

            raf.seek(22);
            int height = Integer.reverseBytes(raf.readInt());

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            raf.seek(pixelDataOffset);

            byte[] pixel = new byte[3];
            for (int y = height - 1; y >= 0; y--) {
                for (int x = 0; x < width; x++) {
                    raf.readFully(pixel);
                    int value = ((pixel[2] & 0xFF) << 16) | ((pixel[1] & 0xFF) << 8) | (pixel[0] & 0xFF);
                    bufferedImage.setRGB(x, y, value);
                }
                int padding = (4 - (width * 3) % 4) % 4;
                raf.skipBytes(padding);
            }

            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
