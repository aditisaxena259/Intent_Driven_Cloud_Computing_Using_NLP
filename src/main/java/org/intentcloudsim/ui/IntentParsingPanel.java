package org.intentcloudsim.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.intentcloudsim.intent.Intent;
import org.intentcloudsim.intent.NaturalLanguageIntentParser;

/**
 * Panel for Natural Language Intent Parsing.
 * Allows users to input text and see the parsed intent priorities.
 */
public class IntentParsingPanel extends VBox {

    private TextArea inputArea;
    private TextArea resultArea;
    private BarChart<String, Number> priorityChart;
    private Label confidenceLabel;
    private Label dominantLabel;

    public IntentParsingPanel() {
        setPadding(new Insets(15));
        setSpacing(10);

        // Input section
        getChildren().add(createInputSection());

        // Split pane with results and chart
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.5);
        splitPane.getItems().addAll(
            createResultsSection(),
            createChartSection()
        );
        getChildren().add(splitPane);

        VBox.setVgrow(splitPane, Priority.ALWAYS);
    }

    /**
     * Create the input section with text area and buttons.
     */
    private VBox createInputSection() {
        VBox inputBox = new VBox(8);
        inputBox.setPadding(new Insets(10));
        inputBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        Label label = new Label("Enter Natural Language Intent");
        label.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        inputArea = new TextArea();
        inputArea.setWrapText(true);
        inputArea.setPrefHeight(80);
        inputArea.setStyle("-fx-font-size: 11; -fx-font-family: 'Segoe UI';");
        inputArea.setText("I need fast and secure servers for banking at affordable cost");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button parseButton = new Button("🔍 Parse Intent");
        parseButton.setStyle("-fx-font-size: 11; -fx-padding: 8 15; -fx-text-fill: white; -fx-background-color: #3498db;");
        parseButton.setPrefWidth(120);
        parseButton.setOnAction(e -> parseIntent());

        Button exampleButton = new Button("📋 Load Example");
        exampleButton.setStyle("-fx-font-size: 11; -fx-padding: 8 15; -fx-background-color: #95a5a6;");
        exampleButton.setPrefWidth(120);
        exampleButton.setOnAction(e -> loadExample());

        Button clearButton = new Button("✕ Clear");
        clearButton.setStyle("-fx-font-size: 11; -fx-padding: 8 15; -fx-background-color: #95a5a6;");
        clearButton.setPrefWidth(80);
        clearButton.setOnAction(e -> inputArea.clear());

        buttonBox.getChildren().addAll(parseButton, exampleButton, clearButton);

        inputBox.getChildren().addAll(label, inputArea, buttonBox);

        return inputBox;
    }

    /**
     * Create the results display section.
     */
    private VBox createResultsSection() {
        VBox resultsBox = new VBox(8);
        resultsBox.setPadding(new Insets(10));
        resultsBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        Label label = new Label("Parsing Results");
        label.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        // Confidence and dominant priority
        HBox metricsBox = new HBox(20);
        metricsBox.setPadding(new Insets(8));
        metricsBox.setStyle("-fx-border-color: #d0d0d0; -fx-background-color: #ffffff;");

        confidenceLabel = new Label("Confidence: 0%");
        confidenceLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");

        dominantLabel = new Label("Dominant: None");
        dominantLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #0066cc;");

        metricsBox.getChildren().addAll(confidenceLabel, dominantLabel);

        // Results text area
        resultArea = new TextArea();
        resultArea.setWrapText(true);
        resultArea.setEditable(false);
        resultArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");
        resultArea.setText("Results will appear here...");

        VBox.setVgrow(resultArea, Priority.ALWAYS);
        resultsBox.getChildren().addAll(label, metricsBox, resultArea);

        return resultsBox;
    }

    /**
     * Create the chart visualization section.
     */
    private VBox createChartSection() {
        VBox chartBox = new VBox(8);
        chartBox.setPadding(new Insets(10));
        chartBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        Label label = new Label("Intent Priority Distribution");
        label.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        // Create bar chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Priority");
        NumberAxis yAxis = new NumberAxis(0, 1.0, 0.1);
        yAxis.setLabel("Score (0-1)");

        priorityChart = new BarChart<>(xAxis, yAxis);
        priorityChart.setTitle("User Intent Priorities");
        priorityChart.setAnimated(true);
        priorityChart.setLegendVisible(false);

        // Sample data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().addAll(
            new XYChart.Data<>("Cost", 0.5),
            new XYChart.Data<>("Latency", 0.5),
            new XYChart.Data<>("Security", 0.3),
            new XYChart.Data<>("Carbon", 0.2)
        );
        priorityChart.getData().add(series);

        VBox.setVgrow(priorityChart, Priority.ALWAYS);
        chartBox.getChildren().addAll(label, priorityChart);

        return chartBox;
    }

    /**
     * Parse the intent from the input area.
     */
    private void parseIntent() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            resultArea.setText("Please enter an intent statement.");
            return;
        }

        // Parse with confidence
        NaturalLanguageIntentParser.ParseResult result =
            NaturalLanguageIntentParser.parseWithConfidence(input);
        Intent intent = result.intent;

        // Display results
        StringBuilder output = new StringBuilder();
        output.append("═══════════════════════════════════════\n");
        output.append("INTENT PARSING RESULTS\n");
        output.append("═══════════════════════════════════════\n\n");
        output.append("Input: ").append(input).append("\n\n");
        output.append("PRIORITIES:\n");
        output.append(String.format("  • Cost Priority:     %.1f%%\n", intent.getCostPriority() * 100));
        output.append(String.format("  • Latency Priority:  %.1f%%\n", intent.getLatencyPriority() * 100));
        output.append(String.format("  • Security Priority: %.1f%%\n", intent.getSecurityPriority() * 100));
        output.append(String.format("  • Carbon Priority:   %.1f%%\n", intent.getCarbonPriority() * 100));

        output.append("\nINDICATORS:\n");
        if (intent.getCostPriority() > 0.7) {
            output.append("  ✓ Cost-sensitive workload detected\n");
        }
        if (intent.getLatencyPriority() > 0.7) {
            output.append("  ✓ Performance-critical workload detected\n");
        }
        if (intent.getSecurityPriority() > 0.7) {
            output.append("  ✓ Security-sensitive workload detected\n");
        }
        if (intent.getCarbonPriority() > 0.7) {
            output.append("  ✓ Sustainability-conscious workload detected\n");
        }

        output.append("\nRECOMMENDATIONS:\n");
        if (intent.getCostPriority() > 0.7) {
            output.append("  → Use consolidated VM placement\n");
            output.append("  → Consider spot instances or committed discounts\n");
        }
        if (intent.getLatencyPriority() > 0.7) {
            output.append("  → Use dedicated high-performance hosts\n");
            output.append("  → Place near edge locations\n");
        }
        if (intent.getSecurityPriority() > 0.7) {
            output.append("  → Use isolated/dedicated infrastructure\n");
            output.append("  → Enable encryption and compliance checks\n");
        }

        resultArea.setText(output.toString());

        // Update confidence and dominant labels
        confidenceLabel.setText(String.format("Confidence: %.0f%%", result.confidence * 100));
        dominantLabel.setText("Dominant: " + result.dominantPriority);

        // Update chart
        updateChart(intent);
    }

    /**
     * Update the bar chart with intent data.
     */
    private void updateChart(Intent intent) {
        priorityChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Intent Priorities");
        series.getData().addAll(
            new XYChart.Data<>("Cost", intent.getCostPriority()),
            new XYChart.Data<>("Latency", intent.getLatencyPriority()),
            new XYChart.Data<>("Security", intent.getSecurityPriority()),
            new XYChart.Data<>("Carbon", intent.getCarbonPriority())
        );
        priorityChart.getData().add(series);
    }

    /**
     * Load an example intent.
     */
    private void loadExample() {
        String[] examples = {
            "I want cheap and budget-friendly servers for my startup",
            "I need fast real-time low latency processing for gaming",
            "Deploy secure encrypted compliant infrastructure for banking",
            "Run my workload on green sustainable carbon neutral infrastructure",
            "I need high performance secure servers at affordable cost"
        };

        // Pick a random example
        String example = examples[(int)(Math.random() * examples.length)];
        inputArea.setText(example);
        parseIntent();
    }
}
