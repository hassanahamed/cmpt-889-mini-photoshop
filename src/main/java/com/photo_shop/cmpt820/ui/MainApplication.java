package com.photo_shop.cmpt820.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file from the resources package
        Parent root = FXMLLoader.load(getClass().getResource("/com/photo_shop/cmpt820/photo_shop.fxml"));

        // Set the application title
        primaryStage.setTitle("CMPT820 Photoshop App");

        // Create the Scene with the loaded FXML root node, set the size as needed
        Scene scene = new Scene(root, 800, 600); // You can adjust the size as necessary

        // Set the Scene on the primary stage
        primaryStage.setScene(scene);

        // Show the primary stage
        primaryStage.show();
    }


    public static void main(String[] args) {
        // Launch the application
        launch(args);
    }
}
