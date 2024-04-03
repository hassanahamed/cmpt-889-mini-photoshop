package com.photo_shop.cmpt820.ui;


import com.photo_shop.cmpt820.BMPParser.BMPParser;
import com.photo_shop.cmpt820.algorithm.*;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.photo_shop.cmpt820.util.ImageUtils;

import java.io.File;
import java.util.Arrays;

public class MainController {

    @FXML
    private MenuItem openFileMenuItem, exitMenuItem;
    @FXML
    private ImageView originalImageView; // This matches the fx:id in your FXML
    @FXML
    private ImageView staticOriginalImageView; // This matches the fx:id in your FXML
    @FXML
    private ImageView processedImageView; // This matches the fx:id in your FXML
    @FXML
    private Label statusLabel;

    @FXML
    private AnchorPane imageManipulationPane;

    @FXML
    private Label entropyLabel;
    @FXML
    private Label huffmanLabel;

    private Cursor resizingCursor = null; // Field to store the current resizing cursor


    private double initialMouseX;

    private Image initialImage;
    private double initialMouseY;

    private Rectangle[] resizeHandles = new Rectangle[4];

    private final double borderPadding = 10.0; // or any other appropriate value
    private boolean isResizing = false;

    @FXML
    public void initialize() {
        setupImageInteractions();
    }

    private void setupImageInteractions() {
        originalImageView.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                // Toggle between dragging and resizing mode
                isResizing = !isResizing;
                if (!isResizing) {
                    originalImageView.setCursor(Cursor.DEFAULT);
                }
                // Capture the cursor for resizing
                resizingCursor = imageManipulationPane.getScene().getCursor();
            } else {
                // Capture initial mouse coordinates for dragging
                initialMouseX = event.getSceneX();
                initialMouseY = event.getSceneY();
            }
        });

        originalImageView.setOnMouseDragged(event -> {
            if (event.getButton() != MouseButton.SECONDARY && !isResizing) {
                // Calculate delta for dragging
                double deltaX = event.getSceneX() - initialMouseX;
                double deltaY = event.getSceneY() - initialMouseY;

                // Move the image based on drag
                if (AnchorPane.getLeftAnchor(originalImageView) != null && AnchorPane.getTopAnchor(originalImageView) != null) {
                    AnchorPane.setLeftAnchor(originalImageView, AnchorPane.getLeftAnchor(originalImageView) + deltaX);
                    AnchorPane.setTopAnchor(originalImageView, AnchorPane.getTopAnchor(originalImageView) + deltaY);
                }

                initialMouseX = event.getSceneX();
                initialMouseY = event.getSceneY();
                event.consume();
            } else if (event.getButton() != MouseButton.SECONDARY && isResizing) {
                // Perform resizing based on the captured cursor type
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
                updateCursorForPosition(event); // Update cursor based on position when resizing
            }
        });
    }

    private void updateCursorForPosition(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        double width = originalImageView.getBoundsInLocal().getWidth();
        double height = originalImageView.getBoundsInLocal().getHeight();


        // Determine cursor type based on position
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


        originalImageView.setCursor(resizingCursor); // Set the cursor
    }

    private void performResizingBasedOnCursor(Cursor cursor, double deltaX, double deltaY) {
        double minWidth = 10;
        double minHeight = 10;
        originalImageView.setPreserveRatio(false);

        double initialWidth = originalImageView.getFitWidth();
        double initialHeight = originalImageView.getFitHeight();
        // Assuming initial anchors are 0 if not set
        double initialLeftAnchor = AnchorPane.getLeftAnchor(originalImageView) == null ? 0 : AnchorPane.getLeftAnchor(originalImageView);
        double initialTopAnchor = AnchorPane.getTopAnchor(originalImageView) == null ? 0 : AnchorPane.getTopAnchor(originalImageView);

        if (cursor == Cursor.E_RESIZE) {
            // Dragging right edge; only width changes
            double newWidth = initialWidth + deltaX;
            originalImageView.setFitWidth(Math.max(newWidth, minWidth));
        } else if (cursor == Cursor.W_RESIZE) {
            // Dragging left edge; adjust width and reposition
            double newWidth = initialWidth - deltaX;
            if (newWidth > minWidth) {
                originalImageView.setFitWidth(newWidth);
                AnchorPane.setLeftAnchor(originalImageView, initialLeftAnchor + deltaX);
            }
        } else if (cursor == Cursor.S_RESIZE) {
            // Dragging bottom edge; only height changes
            double newHeight = initialHeight + deltaY;
            originalImageView.setFitHeight(Math.max(newHeight, minHeight));
        } else if (cursor == Cursor.N_RESIZE) {
            // Dragging top edge; adjust height and reposition
            double newHeight = initialHeight - deltaY;
            if (newHeight > minHeight) {
                originalImageView.setFitHeight(newHeight);
                AnchorPane.setTopAnchor(originalImageView, initialTopAnchor + deltaY);
            }
        }
        if (cursor == Cursor.NE_RESIZE) {
            // Increase width, decrease height
            originalImageView.setFitWidth(Math.max(initialWidth + deltaX, minWidth));
            double newHeight = Math.max(initialHeight - deltaY, minHeight);
            if (newHeight != initialHeight) {
                originalImageView.setFitHeight(newHeight);
                AnchorPane.setTopAnchor(originalImageView, initialTopAnchor + deltaY);
            }
        } else if (cursor == Cursor.NW_RESIZE) {
            // Decrease width, decrease height, move left and up
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
            // Increase both width and height
            originalImageView.setFitWidth(Math.max(initialWidth + deltaX, minWidth));
            originalImageView.setFitHeight(Math.max(initialHeight + deltaY, minHeight));
        } else if (cursor == Cursor.SW_RESIZE) {
            // Decrease width, increase height, move left
            double newWidth = Math.max(initialWidth - deltaX, minWidth);
            if (newWidth != initialWidth) {
                originalImageView.setFitWidth(newWidth);
                AnchorPane.setLeftAnchor(originalImageView, initialLeftAnchor + deltaX);
            }
            originalImageView.setFitHeight(Math.max(initialHeight + deltaY, minHeight));
        }
    }





















    // Method to handle "Open File" action
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

    // Method to handle "Exit" action
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    // Method to apply ordered dithering
    @FXML
    private void handleOrderedDithering() {
        if (originalImageView.getImage() != null) {
            // First, convert the original image to grayscale
            ImageOperation grayscaleOperation = new GrayScale();
            Image grayscaleImage = grayscaleOperation.apply(originalImageView.getImage());

            // Apply ordered dithering to the grayscale image
            ImageOperation ditheringOperation = new OrderedDithering();
            Image ditheredImage = ditheringOperation.apply(grayscaleImage);

            // Assuming you have a way to display images side by side.
            // This might involve updating your UI to accommodate two images.
            // For simplicity, let's just update the processedImageView for now.
            originalImageView.setImage(ditheredImage);
            staticOriginalImageView.setImage(grayscaleImage);

            // Update status
            statusLabel.setText("Status: Ordered Dithering applied.");
        } else {
            statusLabel.setText("Status: No image loaded.");
        }
    }


    // Method to apply auto level
    @FXML
    private void handleAutoLevel() {
        if (originalImageView.getImage() != null) {
            ImageOperation autoLevelOperation = new AutoLevel();
            Image autoLeveledImage = autoLevelOperation.apply(originalImageView.getImage());

            // Display the original and auto-leveled images side by side
            // This might involve updating your UI layout to accommodate two images side by side
            // For simplicity, let's just update the processedImageView for now
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

            statusLabel.setText("Status: Reset successful.");
        } else {
            statusLabel.setText("Status: No image loaded.");
        }
    }


    // Method to calculate and show Huffman encoding results
    @FXML
    private void handleHuffman() {
        ImageOperation grayscaleOperation = new GrayScale();

        // Apply the operation to the current image in the originalImageView
        Image grayscaleImage = grayscaleOperation.apply(originalImageView.getImage());
        int[] histogram = ImageUtils.calculateHistogram(grayscaleImage);
        int totalPixels = (int) (grayscaleImage.getWidth() * grayscaleImage.getHeight());

        double entropy = ImageUtils.calculateEntropy(histogram, totalPixels);
        double avgHuffmanLength = HuffmanCoding.calculateAverageHuffmanCodeLength(histogram, totalPixels);

        entropyLabel.setText(String.format("Entropy: %.2f", entropy));
        huffmanLabel.setText(String.format("Average Huffman Length: %.2f", avgHuffmanLength));

        // Update your GUI or log with entropy and average Huffman code length
        // Note: For a GUI, you might set the text of a Label or TextArea
    }
    @FXML
    private void handleGrayscale() {
        if (originalImageView.getImage() != null) {
            // Create a new instance of your grayscale operation
            ImageOperation grayscaleOperation = new GrayScale();

            // Apply the operation to the current image in the originalImageView
            Image grayscaleImage = grayscaleOperation.apply(originalImageView.getImage());

            // Set the processed image to your originalImageView to display the result
            originalImageView.setImage(grayscaleImage);

            staticOriginalImageView.setImage(initialImage);

            // Update the status label to indicate the operation's completion
            statusLabel.setText("Status: Grayscale applied.");
        } else {
            // Update the status label if there's no image to process
            statusLabel.setText("Status: No image loaded.");
        }
    }


}
