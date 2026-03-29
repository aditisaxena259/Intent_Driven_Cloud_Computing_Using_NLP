package org.intentcloudsim.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import org.intentcloudsim.ui.tabs.IntentInputTab;
import org.intentcloudsim.ui.tabs.ConfigurationTab;
import org.intentcloudsim.ui.tabs.SimulationTab;
import org.intentcloudsim.ui.models.CloudConfig;

/**
 * Main SimulationUI - 3-tab workflow for Intent-Driven Cloud Computing
 *
 * Tab 1: Intent Input - Parse natural language intent and generate suggestions
 * Tab 2: Configuration - Edit and validate cloud configuration
 * Tab 3: Simulation - Execute simulation and visualize results
 */
public class SimulationUI extends Application {

    private IntentInputTab tab1;
    private ConfigurationTab tab2;
    private SimulationTab tab3;
    private TabPane tabPane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Intent-Driven Cloud Computing Simulation");
        primaryStage.setWidth(1400);
        primaryStage.setHeight(900);

        // Create the three tabs
        tab1 = new IntentInputTab();
        tab2 = new ConfigurationTab();
        tab3 = new SimulationTab();

        // Create tab pane
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-size: 12; -fx-padding: 5;");

        // Tab 1: Intent Input
        Tab intentTab = new Tab("1. Intent Parser", tab1);
        intentTab.setStyle("-fx-padding: 5; -fx-font-size: 12; -fx-font-weight: bold;");

        // Tab 2: Configuration
        Tab configTab = new Tab("2. Configuration", tab2);
        configTab.setStyle("-fx-padding: 5; -fx-font-size: 12; -fx-font-weight: bold;");

        // Tab 3: Simulation
        Tab simTab = new Tab("3. Simulation & Results", tab3);
        simTab.setStyle("-fx-padding: 5; -fx-font-size: 12; -fx-font-weight: bold;");

        tabPane.getTabs().addAll(intentTab, configTab, simTab);

        // Listen to tab changes to sync configuration
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, old, newVal) -> {
            int newIndex = newVal.intValue();
            int oldIndex = old.intValue();

            if (oldIndex == 0 && newIndex == 1) {
                // Moving from Tab1 to Tab2: pass intent config
                CloudConfig config = tab1.getCurrentConfig();
                tab2.setCurrentConfig(config);
            } else if (oldIndex == 1 && newIndex == 2) {
                // Moving from Tab2 to Tab3: pass configuration
                CloudConfig config = tab2.getCurrentConfig();
                tab3.setCurrentConfig(config);
            } else if (oldIndex == 2 && newIndex == 1) {
                // Moving back from Tab3 to Tab2: keep current config
                // (no change needed)
            }
        });

        // Create top toolbar
        VBox topBar = createTopBar();

        // Create main layout
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabPane);

        // Create scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Show info dialog
        showWelcomeDialog();
    }

    /**
     * Create top toolbar with info and help
     */
    private VBox createTopBar() {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #d0d0d0; " +
                    "-fx-border-width: 0 0 1 0;");

        Label title = new Label("🚀 Intent-Driven Cloud Computing Simulator");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        HBox infoBox = new HBox(20);
        infoBox.setPadding(new Insets(0));
        infoBox.setStyle("-fx-font-size: 11; -fx-text-fill: #555;");

        Label step1 = new Label("Step 1: Enter your intent → Receive suggestions");
        Label step2 = new Label("Step 2: Review/edit configuration");
        Label step3 = new Label("Step 3: Run simulation & visualize results");

        Button helpButton = new Button("❓ Help");
        helpButton.setStyle("-fx-font-size: 10; -fx-padding: 5 10;");
        helpButton.setOnAction(e -> showHelpDialog());

        Button aboutButton = new Button("About");
        aboutButton.setStyle("-fx-font-size: 10; -fx-padding: 5 10;");
        aboutButton.setOnAction(e -> showAboutDialog());

        HBox.setHgrow(infoBox, Priority.ALWAYS);
        infoBox.getChildren().addAll(step1, new Separator(javafx.geometry.Orientation.VERTICAL),
                                      step2, new Separator(javafx.geometry.Orientation.VERTICAL),
                                      step3);

        box.getChildren().addAll(title, infoBox, createButtonBar(helpButton, aboutButton));
        return box;
    }

    /**
     * Create button bar
     */
    private HBox createButtonBar(Button helpButton, Button aboutButton) {
        HBox box = new HBox(10);
        box.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        box.getChildren().addAll(helpButton, aboutButton);
        return box;
    }

    /**
     * Show welcome dialog
     */
    private void showWelcomeDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome");
        alert.setHeaderText("🎉 Welcome to Intent-Driven Cloud Computing Simulator");
        alert.setContentText(
            "This application helps you:\n\n" +
            "1. Define cloud infrastructure requirements in natural language\n" +
            "2. Automatically parse your intent into configuration parameters\n" +
            "3. Review and customize the cloud configuration\n" +
            "4. Run simulation and analyze cost/performance trade-offs\n\n" +
            "Start by entering your infrastructure intent in the Intent Parser tab!"
        );
        alert.showAndWait();
    }

    /**
     * Show help dialog
     */
    private void showHelpDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("How to Use");
        alert.setContentText(
            "STEP 1 - Intent Parser:\n" +
            "• Describe your cloud needs in natural language\n" +
            "• Examples: 'Fast servers for gaming', 'Cheap batch processing'\n" +
            "• Click 'Parse Intent' to analyze\n" +
            "• Review the suggested configuration\n\n" +
            "STEP 2 - Configuration:\n" +
            "• Edit the JSON configuration or use the form fields\n" +
            "• Adjust priorities, SLA parameters, and infrastructure specs\n" +
            "• Click 'Validate' to check configuration validity\n" +
            "• Click 'Save Configuration' when ready\n\n" +
            "STEP 3 - Simulation:\n" +
            "• Click 'Start Simulation' to begin execution\n" +
            "• Monitor progress and real-time logs\n" +
            "• View cost analysis, performance metrics, and infrastructure stats\n" +
            "• Analyze trade-offs and optimization opportunities\n"
        );
        alert.showAndWait();
    }

    /**
     * Show about dialog
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Intent-Driven Cloud Computing Using NLP");
        alert.setContentText(
            "Version: 2.0\n" +
            "Built with: JavaFX 21, CloudSim Plus 8.0\n\n" +
            "This simulation engine uses natural language processing to:\n" +
            "• Parse user intents into cloud configuration parameters\n" +
            "• Negotiate SLA requirements based on priorities\n" +
            "• Evaluate cost-performance trade-offs\n" +
            "• Execute realistic cloud simulations\n\n" +
            "For more information, see documentation in the project README."
        );
        alert.showAndWait();
    }
}
