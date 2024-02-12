package com.photo_shop.cmpt820.ui;


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
    private ImageView processedImageView; // This matches the fx:id in your FXML
    @FXML
    private Label statusLabel;

    @FXML
    private AnchorPane imageManipulationPane;

    private Rectangle resizeBorder;

    private Cursor resizingCursor = null; // Field to store the current resizing cursor


    private double initialMouseX;
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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("BMP Files", "*.*"));
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            originalImageView.setImage(image);
            // Reset processedImageView and statusLabel when a new image is loaded
            processedImageView.setImage(null);
            statusLabel.setText("Status: Image loaded successfully.");
        }
    }

    // Method to handle "Exit" action
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    // Method to convert and display the image in grayscale
    @FXML
    private void handleGrayscale() {
        if (originalImageView.getImage() != null) {
            Image grayImage = ImageUtils.convertToGrayscale(originalImageView.getImage());
            processedImageView.setImage(grayImage);
            statusLabel.setText("Status: Grayscale conversion applied.");
        }
    }

    // Method to apply ordered dithering
    @FXML
    private void handleOrderedDithering() {
        // Implement ordered dithering conversion in ImageUtils
        // This is a placeholder for where you would call the method
        statusLabel.setText("Status: Ordered dithering feature is not implemented yet.");
    }

    // Method to apply auto level
    @FXML
    private void handleAutoLevel() {
        // Implement auto level conversion in ImageUtils
        // This is a placeholder for where you would call the method
        statusLabel.setText("Status: Auto level feature is not implemented yet.");
    }

    // Method to calculate and show Huffman encoding results
    @FXML
    private void handleHuffman() {
        // Implement Huffman encoding in a suitable utility or algorithm class
        // This is a placeholder for where you would display the Huffman encoding results
        statusLabel.setText("Status: Huffman coding feature is not implemented yet.");
    }
}
