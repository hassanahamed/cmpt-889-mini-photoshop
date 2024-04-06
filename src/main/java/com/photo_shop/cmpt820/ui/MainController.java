package com.photo_shop.cmpt820.ui;


import com.photo_shop.cmpt820.BMPParser.BMPParser;
import com.photo_shop.cmpt820.algorithm.*;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.photo_shop.cmpt820.util.ImageUtils;

import java.io.File;

public class MainController {


    @FXML
    private Slider brightnessSlider;
    @FXML
    private Slider contrastSlider;

    @FXML
    private ImageView originalImageView;
    @FXML
    private ImageView staticOriginalImageView;
    @FXML
    private Label statusLabel;

    @FXML
    private AnchorPane imageManipulationPane;

    @FXML
    private Label entropyLabel;
    @FXML
    private Label huffmanLabel;

    private Cursor resizingCursor = null;


    private double initialMouseX;

    private Image initialImage;
    private double initialMouseY;

    private boolean isResizing = false;

    @FXML
    public void initialize() {
        setupImageInteractions();
        setupBrightnessAdjustmentListener();
        setupContrastAdjustmentListener();
    }

    private void setupBrightnessAdjustmentListener() {
        brightnessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double brightnessFactor = newValue.doubleValue() / 100.0 + 1;
            handleBrightnessAdjustment(brightnessFactor);
        });
    }

    private void setupContrastAdjustmentListener() {
        contrastSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double contrastFactor = 1 + (newValue.doubleValue() / 100.0);
            handleContrastAdjustment(contrastFactor);
        });
    }

    private void setupImageInteractions() {
        originalImageView.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                isResizing = !isResizing;
                if (!isResizing) {
                    originalImageView.setCursor(Cursor.DEFAULT);
                }
                resizingCursor = imageManipulationPane.getScene().getCursor();
            } else {
                initialMouseX = event.getSceneX();
                initialMouseY = event.getSceneY();
            }
        });

        originalImageView.setOnMouseDragged(event -> {
            if (event.getButton() != MouseButton.SECONDARY && !isResizing) {
                double deltaX = event.getSceneX() - initialMouseX;
                double deltaY = event.getSceneY() - initialMouseY;

                if (AnchorPane.getLeftAnchor(originalImageView) != null && AnchorPane.getTopAnchor(originalImageView) != null) {
                    AnchorPane.setLeftAnchor(originalImageView, AnchorPane.getLeftAnchor(originalImageView) + deltaX);
                    AnchorPane.setTopAnchor(originalImageView, AnchorPane.getTopAnchor(originalImageView) + deltaY);
                }

                initialMouseX = event.getSceneX();
                initialMouseY = event.getSceneY();
                event.consume();
            } else if (event.getButton() != MouseButton.SECONDARY && isResizing) {
                double deltaX = event.getSceneX() - initialMouseX;
                double deltaY = event.getSceneY() - initialMouseY;
                performResizingBasedOnCursor(resizingCursor, deltaX, deltaY);

                initialMouseX = event.getSceneX();
                initialMouseY = event.getSceneY();
                event.consume();
            }
        });

        originalImageView.setOnMouseMoved(event -> {
            if (isResizing) {
                updateCursorForPosition(event);
            }
        });
    }

    private void updateCursorForPosition(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        double width = originalImageView.getBoundsInLocal().getWidth();
        double height = originalImageView.getBoundsInLocal().getHeight();


        boolean onLeftEdge = mouseX < 10;
        boolean onRightEdge = mouseX > width - 10;
        boolean onTopEdge = mouseY < 10;
        boolean onBottomEdge = mouseY > height - 10;

        if (onTopEdge && onRightEdge) resizingCursor = Cursor.NE_RESIZE;
        else if (onTopEdge && onLeftEdge) resizingCursor = Cursor.NW_RESIZE;
        else if (onBottomEdge && onLeftEdge) resizingCursor = Cursor.SW_RESIZE;
        else if (onBottomEdge && onRightEdge) resizingCursor = Cursor.SE_RESIZE;
        else if (onRightEdge ) resizingCursor = Cursor.E_RESIZE;
        else if (onLeftEdge ) resizingCursor = Cursor.W_RESIZE;
        else if (onTopEdge) resizingCursor = Cursor.N_RESIZE;
        else if (onBottomEdge) resizingCursor = Cursor.S_RESIZE;


        originalImageView.setCursor(resizingCursor);
    }

    private void performResizingBasedOnCursor(Cursor cursor, double deltaX, double deltaY) {
        double minWidth = 10;
        double minHeight = 10;
        originalImageView.setPreserveRatio(false);

        double initialWidth = originalImageView.getFitWidth();
        double initialHeight = originalImageView.getFitHeight();
        double initialLeftAnchor = AnchorPane.getLeftAnchor(originalImageView) == null ? 0 : AnchorPane.getLeftAnchor(originalImageView);
        double initialTopAnchor = AnchorPane.getTopAnchor(originalImageView) == null ? 0 : AnchorPane.getTopAnchor(originalImageView);

        if (cursor == Cursor.E_RESIZE) {
            double newWidth = initialWidth + deltaX;
            originalImageView.setFitWidth(Math.max(newWidth, minWidth));
        } else if (cursor == Cursor.W_RESIZE) {
            double newWidth = initialWidth - deltaX;
            if (newWidth > minWidth) {
                originalImageView.setFitWidth(newWidth);
                AnchorPane.setLeftAnchor(originalImageView, initialLeftAnchor + deltaX);
            }
        } else if (cursor == Cursor.S_RESIZE) {
            double newHeight = initialHeight + deltaY;
            originalImageView.setFitHeight(Math.max(newHeight, minHeight));
        } else if (cursor == Cursor.N_RESIZE) {
            double newHeight = initialHeight - deltaY;
            if (newHeight > minHeight) {
                originalImageView.setFitHeight(newHeight);
                AnchorPane.setTopAnchor(originalImageView, initialTopAnchor + deltaY);
            }
        }
        if (cursor == Cursor.NE_RESIZE) {
            originalImageView.setFitWidth(Math.max(initialWidth + deltaX, minWidth));
            double newHeight = Math.max(initialHeight - deltaY, minHeight);
            if (newHeight != initialHeight) {
                originalImageView.setFitHeight(newHeight);
                AnchorPane.setTopAnchor(originalImageView, initialTopAnchor + deltaY);
            }
        } else if (cursor == Cursor.NW_RESIZE) {
            double newWidth = Math.max(initialWidth - deltaX, minWidth);
            double newHeight = Math.max(initialHeight - deltaY, minHeight);
            if (newWidth != initialWidth) {
                originalImageView.setFitWidth(newWidth);
                AnchorPane.setLeftAnchor(originalImageView, initialLeftAnchor + deltaX);
            }
            if (newHeight != initialHeight) {
                originalImageView.setFitHeight(newHeight);
                AnchorPane.setTopAnchor(originalImageView, initialTopAnchor + deltaY);
            }
        } else if (cursor == Cursor.SE_RESIZE) {
            originalImageView.setFitWidth(Math.max(initialWidth + deltaX, minWidth));
            originalImageView.setFitHeight(Math.max(initialHeight + deltaY, minHeight));
        } else if (cursor == Cursor.SW_RESIZE) {
            double newWidth = Math.max(initialWidth - deltaX, minWidth);
            if (newWidth != initialWidth) {
                originalImageView.setFitWidth(newWidth);
                AnchorPane.setLeftAnchor(originalImageView, initialLeftAnchor + deltaX);
            }
            originalImageView.setFitHeight(Math.max(initialHeight + deltaY, minHeight));
        }
    }


    @FXML
    private void handleOpenFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open BMP File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Files", "*.bmp"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            Image image = BMPParser.parseBMP(file);
            if (image != null) {
                initialImage = image;
                originalImageView.setImage(image);
                staticOriginalImageView.setImage(image);
                statusLabel.setText("Status: Image loaded successfully.");
            } else {
                statusLabel.setText("Status: Failed to load image.");
            }
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleOrderedDithering() {
        if (originalImageView.getImage() != null) {
            ImageOperation grayscaleOperation = new GrayScale();
            Image grayscaleImage = grayscaleOperation.apply(initialImage);

            ImageOperation ditheringOperation = new OrderedDithering();
            Image ditheredImage = ditheringOperation.apply(grayscaleImage);

            originalImageView.setImage(ditheredImage);
            staticOriginalImageView.setImage(grayscaleImage);

            statusLabel.setText("Status: Ordered Dithering applied.");
        } else {
            statusLabel.setText("Status: No image loaded.");
        }
    }

    @FXML
    private void handleAutoLevel() {
        if (originalImageView.getImage() != null) {
            ImageOperation autoLevelOperation = new AutoLevel();
            Image autoLeveledImage = autoLevelOperation.apply(initialImage);

            originalImageView.setImage(autoLeveledImage);

            staticOriginalImageView.setImage(initialImage);

            statusLabel.setText("Status: Auto Level applied.");
        } else {
            statusLabel.setText("Status: No image loaded.");
        }
    }

    @FXML
    private void handleReset() {
        if (originalImageView.getImage() != null) {
            originalImageView.setImage(initialImage);

            staticOriginalImageView.setImage(initialImage);
            brightnessSlider.setValue(0);
            contrastSlider.setValue(0);

            statusLabel.setText("Status: Reset successful.");
        } else {
            statusLabel.setText("Status: No image loaded.");
        }
    }


    @FXML
    private void handleHuffman() {
        ImageOperation grayscaleOperation = new GrayScale();

        Image grayscaleImage = grayscaleOperation.apply(originalImageView.getImage());
        int[] histogram = ImageUtils.calculateHistogram(grayscaleImage);
        int totalPixels = (int) (grayscaleImage.getWidth() * grayscaleImage.getHeight());

        double entropy = ImageUtils.calculateEntropy(histogram, totalPixels);
        double avgHuffmanLength = HuffmanCoding.calculateAverageHuffmanCodeLength(histogram, totalPixels);

        entropyLabel.setText(String.format("Entropy: %.4f", entropy));
        huffmanLabel.setText(String.format("Average Huffman Length: %.4f", avgHuffmanLength));

    }
    @FXML
    private void handleGrayscale() {
        if (originalImageView.getImage() != null) {
            ImageOperation grayscaleOperation = new GrayScale();

            Image grayscaleImage = grayscaleOperation.apply(initialImage);

            originalImageView.setImage(grayscaleImage);

            staticOriginalImageView.setImage(initialImage);

            statusLabel.setText("Status: Grayscale applied.");
        } else {
            statusLabel.setText("Status: No image loaded.");
        }
    }

    @FXML
    private void handleSobelEdgeDetection() {
        if (originalImageView.getImage() != null) {
            ImageOperation edgeDetectionOperation = new SobelEdgeDetection();

            Image sobelImage = edgeDetectionOperation.apply(initialImage);

            originalImageView.setImage(sobelImage);

            staticOriginalImageView.setImage(initialImage);

            statusLabel.setText("Status: sobelEdge Dection applied.");
        } else {
            statusLabel.setText("Status: No image loaded.");
        }
    }


    @FXML
    private void handleBrightnessAdjustment(double brightnessFactor) {
        if (originalImageView.getImage() != null && initialImage != null) {
            ImageOperation brightnessOperation = new BrightnessAdjustment(brightnessFactor);

            Image adjustedImage = brightnessOperation.apply(initialImage);

            originalImageView.setImage(adjustedImage);
            statusLabel.setText("Status: Brightness adjusted.");
        } else {
            statusLabel.setText("Status: No image loaded.");
        }
    }


    public void handleContrastAdjustment(double contrastLevel) {
        if (originalImageView.getImage() != null && initialImage != null) {
            ImageOperation contrastOperation = new ContrastAdjustment(contrastLevel);

            Image adjustedImage = contrastOperation.apply(initialImage);

            originalImageView.setImage(adjustedImage);
            statusLabel.setText("Status: Contrast adjusted.");
        } else {
            statusLabel.setText("Status: No image loaded.");
        }
    }
}
