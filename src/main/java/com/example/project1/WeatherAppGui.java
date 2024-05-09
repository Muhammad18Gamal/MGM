package com.example.project1;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import org.json.simple.JSONObject;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends Application {
    private static final int IMAGES_PER_ROW = 4;
    private static final int NUMBER_OF_ROWS = 7;
    private static final String[] GOVERNORATES = {
            "Alexandria", "Aswan", "Asyut", "Beheira", "Beni Suef", "Cairo",
            "Dakahlia", "Damietta", "Faiyum", "Gharbia", "Giza", "Ismailia",
            "Kafr El Sheikh", "Luxor", "Matruh", "Minya", "Monufia", "New Valley",
            "Sinai", "Port Said", "Qalyubia", "Qena", "Red Sea", "Sharqia", "Sohag", "South Sinai", "suez"
    };

    @Override
    public void start(Stage primaryStage) throws IOException {
        ScrollPane SP = new ScrollPane();
        GridPane root = new GridPane();
        SP.setContent(root);
        root.setHgap(13);
        root.setVgap(10);
        root.setPadding(new Insets(10));
        primaryStage.setResizable(false);

        // Directory containing images
        File imagesDirectory = new File("src\\logos");

        // Load all images from the directory
        File[] imageFiles = imagesDirectory.listFiles();
        if (imageFiles != null) {
            int row = 0;
            int col = 0;

            for (int i = 0; i < GOVERNORATES.length; i++) {
                File file = imageFiles[i];
                Image image = new Image(file.toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(205);
                imageView.setFitHeight(150);

                Button button = new Button(GOVERNORATES[i]);
                button.setOnAction(event -> {
                    // Inside the button.setOnAction(event -> { ... });
// Create a new scene for results
                    GridPane resultsPane = new GridPane();
                    resultsPane.setAlignment(Pos.CENTER);
                    resultsPane.setHgap(10);
                    resultsPane.setVgap(10);
                    resultsPane.setPadding(new Insets(10));

// Add labels for temperature, humidity, and wind
                    Label temperatureLabel = new Label("Temperature:");
                    temperatureLabel.setFont(Font.font("Times New Roman", 30));
                    temperatureLabel.setStyle("-fx-text-fill: black;");

                    Label humidityLabel = new Label("Humidity:");
                    humidityLabel.setFont(Font.font("Times New Roman", 30));
                    humidityLabel.setStyle("-fx-text-fill: black;");

                    Label windLabel = new Label("Wind:");
                    windLabel.setFont(Font.font("Times New Roman", 30));
                    windLabel.setStyle("-fx-text-fill: black;");

                    TextField temperatureField = new TextField();
                    temperatureField.setEditable(false);
                    TextField humidityField = new TextField();
                    humidityField.setEditable(false);
                    TextField windField = new TextField();
                    windField.setEditable(false);

// Add components to results pane
                    resultsPane.add(temperatureLabel, 0, 0);
                    resultsPane.add(humidityLabel, 0, 1);
                    resultsPane.add(windLabel, 0, 2);
                    resultsPane.add(temperatureField, 1, 0);
                    resultsPane.add(humidityField, 1, 1);
                    resultsPane.add(windField, 1, 2);

                    Scene resultsScene = new Scene(resultsPane, 800, 600);

                    Stage resultsStage = new Stage();
                    resultsStage.setTitle("Results ");
                    resultsStage.setScene(resultsScene);

// Show the results stage
                    resultsStage.show();

// Close the main stage
                    primaryStage.close();

                });

                // Add the image and button to the GridPane
                root.add(imageView, col, row);
                root.add(button, col, row + 1);

                col++;
                if (col == IMAGES_PER_ROW) {
                    col = 0;
                    row += 2;

                    if (row >= NUMBER_OF_ROWS * 2) {
                        break;
                    }
                }
            }
        }

        Scene scene = new Scene(SP, 910, 500);
        primaryStage.setTitle("Welcome to our weather app!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
