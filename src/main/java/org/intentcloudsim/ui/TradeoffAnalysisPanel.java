package org.intentcloudsim.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Panel for displaying cost-performance trade-off analysis.
 * Shows Pareto frontier and optimization results.
 */
public class TradeoffAnalysisPanel extends VBox {

    private ScatterChart<Number, Number> paretoChart;
    private BarChart<String, Number> tradeoffScoreChart;
    private ComboBox<String> priorityCombo;
    private Label recommendationLabel;

    public TradeoffAnalysisPanel() {
        setPadding(new Insets(15));
        setSpacing(10);

        // Control panel
        getChildren().add(createControlPanel());

        // Main content with split pane
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.5);
        splitPane.getItems().addAll(
            createParetoFrontierSection(),
            createTradeoffScoresSection()
        );

        getChildren().add(splitPane);

        // Recommendation panel
        getChildren().add(createRecommendationPanel());

        VBox.setVgrow(splitPane, Priority.ALWAYS);

        // Load sample data
        loadSampleTradeoffData();
    }

    /**
     * Create the control panel for trade-off analysis settings.
     */
    private HBox createControlPanel() {
        HBox controlBox = new HBox(15);
        controlBox.setPadding(new Insets(10));
        controlBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
        controlBox.setAlignment(Pos.CENTER_LEFT);

        Label priorityLabel = new Label("User Priority:");
        priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll(
            "Cost-Optimized",
            "Performance-Optimized",
            "Security-Optimized",
            "Balanced",
            "Green/Sustainable"
        );
        priorityCombo.setValue("Balanced");
        priorityCombo.setPrefWidth(180);
        priorityCombo.setOnAction(e -> updateTradeoffAnalysis());

        Button analyzeButton = new Button("📊 Analyze Trade-offs");
        analyzeButton.setStyle("-fx-font-size: 11; -fx-padding: 8 15; -fx-text-fill: white; -fx-background-color: #3498db;");
        analyzeButton.setOnAction(e -> loadSampleTradeoffData());

        controlBox.getChildren().addAll(priorityLabel, priorityCombo, analyzeButton);

        return controlBox;
    }

    /**
     * Create the Pareto frontier visualization section.
     */
    private VBox createParetoFrontierSection() {
        VBox sectionBox = new VBox(10);
        sectionBox.setPadding(new Insets(10));
        sectionBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        Label titleLabel = new Label("Pareto Frontier Analysis");
        titleLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        NumberAxis xAxis = new NumberAxis(0, 20, 2);
        xAxis.setLabel("Cost ($/hour)");
        NumberAxis yAxis = new NumberAxis(0, 160, 20);
        yAxis.setLabel("Latency (ms)");

        paretoChart = new ScatterChart<>(xAxis, yAxis);
        paretoChart.setTitle("Cost vs. Performance Trade-off");

        XYChart.Series<Number, Number> optionsSeries = new XYChart.Series<>();
        optionsSeries.setName("Available Options");
        optionsSeries.getData().addAll(
            new XYChart.Data<>(2.0, 150),
            new XYChart.Data<>(5.0, 80),
            new XYChart.Data<>(10.0, 40),
            new XYChart.Data<>(15.0, 20),
            new XYChart.Data<>(3.0, 120),
            new XYChart.Data<>(8.0, 60),
            new XYChart.Data<>(12.0, 35),
            new XYChart.Data<>(6.0, 90)
        );

        XYChart.Series<Number, Number> paretoSeries = new XYChart.Series<>();
        paretoSeries.setName("Pareto Optimal");
        paretoSeries.getData().addAll(
            new XYChart.Data<>(2.0, 150),
            new XYChart.Data<>(5.0, 80),
            new XYChart.Data<>(10.0, 40),
            new XYChart.Data<>(15.0, 20)
        );

        paretoChart.getData().addAll(optionsSeries, paretoSeries);

        VBox.setVgrow(paretoChart, Priority.ALWAYS);
        sectionBox.getChildren().addAll(titleLabel, paretoChart);

        return sectionBox;
    }

    /**
     * Create the trade-off scores section.
     */
    private VBox createTradeoffScoresSection() {
        VBox sectionBox = new VBox(10);
        sectionBox.setPadding(new Insets(10));
        sectionBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        Label titleLabel = new Label("Trade-off Scores");
        titleLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Configuration");
        NumberAxis yAxis = new NumberAxis(0, 1.0, 0.1);
        yAxis.setLabel("Score (0-1)");

        tradeoffScoreChart = new BarChart<>(xAxis, yAxis);
        tradeoffScoreChart.setTitle("Configuration Scores");

        XYChart.Series<String, Number> scoreSeries = new XYChart.Series<>();
        scoreSeries.setName("Trade-off Score");
        scoreSeries.getData().addAll(
            new XYChart.Data<>("Option 1\n(Cost $2)", 0.65),
            new XYChart.Data<>("Option 2\n(Cost $5)", 0.82),
            new XYChart.Data<>("Option 3\n(Cost $10)", 0.78),
            new XYChart.Data<>("Option 4\n(Cost $15)", 0.55)
        );

        tradeoffScoreChart.getData().add(scoreSeries);

        VBox.setVgrow(tradeoffScoreChart, Priority.ALWAYS);
        sectionBox.getChildren().addAll(titleLabel, tradeoffScoreChart);

        return sectionBox;
    }

    /**
     * Create the recommendation panel.
     */
    private VBox createRecommendationPanel() {
        VBox recBox = new VBox(8);
        recBox.setPadding(new Insets(10));
        recBox.setStyle("-fx-border-color: #27ae60; -fx-border-radius: 5; -fx-background-color: #d5f4e6;");

        Label titleLabel = new Label("Recommendation");
        titleLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        recommendationLabel = new Label();
        recommendationLabel.setWrapText(true);
        recommendationLabel.setStyle("-fx-font-size: 10;");

        recBox.getChildren().addAll(titleLabel, recommendationLabel);

        return recBox;
    }

    /**
     * Load sample trade-off analysis data.
     */
    private void loadSampleTradeoffData() {
        String priority = priorityCombo.getValue();

        String recommendation = "";
        switch (priority) {
            case "Cost-Optimized":
                recommendation = "✓ Recommended: Option 1 ($2/hr, 150ms latency) provides the lowest cost. " +
                       "Suitable for batch processing and non-critical workloads.";
                break;
            case "Performance-Optimized":
                recommendation = "✓ Recommended: Option 4 ($15/hr, 20ms latency) offers the best performance. " +
                       "Best for real-time systems, gaming, and latency-sensitive applications.";
                break;
            case "Security-Optimized":
                recommendation = "✓ Recommended: Option 3 ($10/hr, 40ms latency) balances security and performance. " +
                       "Good for healthcare, banking, and regulated industries.";
                break;
            case "Balanced":
                recommendation = "✓ Recommended: Option 2 ($5/hr, 80ms latency) offers the best trade-off score (0.82). " +
                       "Provides good balance between cost, performance, and resource efficiency.";
                break;
            case "Green/Sustainable":
                recommendation = "✓ Recommended: Option 2 ($5/hr, 80ms latency) with green datacenter placement. " +
                       "Uses renewable energy sources while maintaining reasonable performance.";
                break;
        }

        recommendationLabel.setText(recommendation);
    }

    /**
     * Update trade-off analysis based on priority.
     */
    private void updateTradeoffAnalysis() {
        loadSampleTradeoffData();
    }
}
