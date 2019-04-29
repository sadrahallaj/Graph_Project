package com.graphAlgorithm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/com/graphAlgorithm/view/main/mainPage.fxml"));
        primaryStage.setTitle("Breadth First Search or Depth First Search for first Graph");
        Image image = new Image(getClass().getResourceAsStream("/source/graph.png"));
        primaryStage.getIcons().add(image);
        primaryStage.setScene(new Scene(root, 1000, 960));
        primaryStage.setResizable(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
