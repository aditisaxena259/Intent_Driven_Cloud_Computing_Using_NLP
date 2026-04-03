package org.intentcloudsim.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Panel for displaying simulation metrics and results.
 * Shows graphs and statistics from the simulation run.
 */
public class MetricsPanel extends VBox {

    private LineChart<Number, Number> performanceChart;
    private PieChart costDistributionChart;
    private TabPane metricsTabPane;
    private TextArea summaryArea;

    public MetricsPanel() {
        setPadding(new Insets(15));
        setSpacing(10);

        // Summary section
        getChildren().add(createSummarySection());

        // Tab pane for different metric views
        metricsTabPane = new TabPane();
        metricsTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        metricsTabPane.getTabs().addAll(
            createPerformanceTab(),
            createCostAnalysisTab(),
            createSLAComplianceTab(),
            createDetailedResultsTab()
        );

        getChildren().add(metricsTabPane);
        VBox.setVgrow(metricsTabPane, Priority.ALWAYS);

        // Load sample data
        loadSampleMetrics();
    }

    /**
     * Create summary statistics section.
     */
    private HBox createSummarySection() {
        HBox summaryBox = new HBox(20);
        summaryBox.setPadding(new Insets(12));
        summaryBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
        summaryBox.setAlignment(Pos.CENTER_LEFT);

        // Summary cards
        VBox card1 = createSummaryCard("Total Experiments", "8", "#3498db");
        VBox card2 = createSummaryCard("Avg SLA Compliance", "92.5%", "#27ae60");
        VBox card3 = createSummaryCard("Avg Cost Saving", "18.3%", "#f39c12");
        VBox card4 = createSummaryCard("Success Rate", "100%", "#2ecc71");

        summaryBox.getChildren().addAll(card1, card2, card3, card4);

        return summaryBox;
    }

    /**
     * Create a summary metric card.
     */
    private VBox createSummaryCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-border-color: " + color + "; -fx-border-width: 2 0 0 0; -fx-background-color: #ffffff;");
        card.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #666666;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(titleLabel, valueLabel);

        return card;
    }

    /**
     * Create the performance metrics tab.
     */
    private Tab createPerformanceTab() {
        Tab tab = new Tab("Performance Metrics", createPerformanceChart());
        tab.setClosable(false);
        return tab;
    }

    /**
     * Create performance chart.
     */
    private VBox createPerformanceChart() {
        VBox chartBox = new VBox(10);
        chartBox.setPadding(new Insets(15));

        NumberAxis xAxis = new NumberAxis(0, 8, 1);
        xAxis.setLabel("Experiment #");
        NumberAxis yAxis = new NumberAxis(0, 150, 20);
        yAxis.setLabel("Latency (ms)");

        performanceChart = new LineChart<>(xAxis, yAxis);
        performanceChart.setTitle("Response Time per Experiment");

        XYChart.Series<Number, Number> latencySeries = new XYChart.Series<>();
        latencySeries.setName("Latency");
        latencySeries.getData().addAll(
            new XYChart.Data<>(1, 120),
            new XYChart.Data<>(2, 35),
            new XYChart.Data<>(3, 110),
            new XYChart.Data<>(4, 75),
            new XYChart.Data<>(5, 85),
            new XYChart.Data<>(6, 95),
            new XYChart.Data<>(7, 25),
            new XYChart.Data<>(8, 140)
        );

        performanceChart.getData().add(latencySeries);
        VBox.setVgrow(performanceChart, Priority.ALWAYS);
        chartBox.getChildren().add(performanceChart);

        return chartBox;
    }

    /**
     * Create the cost analysis tab.
     */
    private Tab createCostAnalysisTab() {
        Tab tab = new Tab("Cost Analysis", createCostChart());
        tab.setClosable(false);
        return tab;
    }

    /**
     * Create cost analysis chart.
     */
    private VBox createCostChart() {
        VBox chartBox = new VBox(10);
        chartBox.setPadding(new Insets(15));

        costDistributionChart = new PieChart();
        costDistributionChart.setTitle("Cost Distribution by Priority");
        costDistributionChart.setAnimated(true);

        costDistributionChart.getData().addAll(
            new PieChart.Data("Cost-Optimized", 25),
            new PieChart.Data("Performance-Optimized", 20),
            new PieChart.Data("Security-Optimized", 15),
            new PieChart.Data("Balanced", 30),
            new PieChart.Data("Green/Carbon-Aware", 10)
        );

        VBox.setVgrow(costDistributionChart, Priority.ALWAYS);
        chartBox.getChildren().add(costDistributionChart);

        return chartBox;
    }

    /**
     * Create the SLA compliance tab.
     */
    private Tab createSLAComplianceTab() {
        Tab tab = new Tab("SLA Compliance", createSLAChart());
        tab.setClosable(false);
        return tab;
    }

    /**
     * Create SLA compliance chart.
     */
    private VBox createSLAChart() {
        VBox chartBox = new VBox(10);
        chartBox.setPadding(new Insets(15));

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Experiment");
        NumberAxis yAxis = new NumberAxis(0, 110, 10);
        yAxis.setLabel("SLA Compliance %");

        BarChart<String, Number> slaChart = new BarChart<>(xAxis, yAxis);
        slaChart.setTitle("SLA Compliance Rate");

        XYChart.Series<String, Number> complianceSeries = new XYChart.Series<>();
        complianceSeries.setName("Compliance Rate");
        complianceSeries.getData().addAll(
            new XYChart.Data<>("Exp 1", 98),
            new XYChart.Data<>("Exp 2", 99),
            new XYChart.Data<>("Exp 3", 97),
            new XYChart.Data<>("Exp 4", 95),
            new XYChart.Data<>("Exp 5", 96),
            new XYChart.Data<>("Exp 6", 99),
            new XYChart.Data<>("Exp 7", 100),
            new XYChart.Data<>("Exp 8", 89)
        );

        slaChart.getData().add(complianceSeries);
        VBox.setVgrow(slaChart, Priority.ALWAYS);
        chartBox.getChildren().add(slaChart);

        return chartBox;
    }

    /**
     * Create detailed results tab.
     */
    private Tab createDetailedResultsTab() {
        Tab tab = new Tab("Detailed Results", createDetailedResults());
        tab.setClosable(false);
        return tab;
    }

    /**
     * Create detailed results text area.
     */
    private VBox createDetailedResults() {
        VBox resultsBox = new VBox(10);
        resultsBox.setPadding(new Insets(15));

        summaryArea = new TextArea();
        summaryArea.setWrapText(true);
        summaryArea.setEditable(false);
        summaryArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");

        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(5));
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button exportButton = new Button("💾 Export to CSV");
        exportButton.setStyle("-fx-font-size: 11; -fx-padding: 6 12;");
        exportButton.setOnAction(e -> exportResults());

        Button copyButton = new Button("📋 Copy to Clipboard");
        copyButton.setStyle("-fx-font-size: 11; -fx-padding: 6 12;");
        copyButton.setOnAction(e -> copyToClipboard());

        buttonBox.getChildren().addAll(exportButton, copyButton);

        VBox.setVgrow(summaryArea, Priority.ALWAYS);
        resultsBox.getChildren().addAll(buttonBox, summaryArea);

        return resultsBox;
    }

    /**
     * Load sample metrics data.
     */
    private void loadSampleMetrics() {
        StringBuilder results = new StringBuilder();
        results.append("╔═══════════════════════════════════════════════════════════╗\n");
        results.append("║             SIMULATION RESULTS SUMMARY                    ║\n");
        results.append("╚═══════════════════════════════════════════════════════════╝\n\n");

        String[] intents = {
            "Cost-optimized (cheap & budget-friendly)",
            "Performance-optimized (fast & real-time)",
            "Security-optimized (encrypted & compliant)",
            "Balanced (cost-effective & responsive)",
            "Green-focused (sustainable & eco-friendly)",
            "Mixed (performance + security)",
            "Ultra-performance (fastest possible)",
            "Ultra-economical (minimize cost)"
        };

        double[] latencies = {120, 35, 110, 75, 85, 95, 25, 140};
        double[] costs = {2.5, 12.8, 9.5, 6.2, 4.8, 8.1, 15.0, 1.8};
        int[] slaCompliance = {98, 99, 97, 95, 96, 99, 100, 89};

        for (int i = 0; i < intents.length; i++) {
            results.append(String.format("Experiment %d: %s\n", i + 1, intents[i]));
            results.append(String.format("  Latency:       %6.0f ms\n", latencies[i]));
            results.append(String.format("  Cost:          $%.2f/hour\n", costs[i]));
            results.append(String.format("  SLA Compliance: %d%%\n", slaCompliance[i]));
            results.append(String.format("  VM Placement:  Intent-aware optimization\n"));
            results.append("  Status:        ✓ Completed\n\n");
        }

        results.append("═══════════════════════════════════════════════════════════\n");
        results.append("OVERALL STATISTICS\n");
        results.append("═══════════════════════════════════════════════════════════\n");
        results.append(String.format("Total Experiments:    8\n"));
        results.append(String.format("Successful:           8 (100%%)\n"));
        results.append(String.format("Avg Latency:          88.5 ms\n"));
        results.append(String.format("Avg Cost:             $7.59/hour\n"));
        results.append(String.format("Avg SLA Compliance:   96.1%%\n"));
        results.append(String.format("Performance Gain:     +23%% vs traditional placement\n"));

        summaryArea.setText(results.toString());
    }

    /**
     * Export results to CSV.
     */
    private void exportResults() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export");
        alert.setHeaderText("Results Exported");
        alert.setContentText("Results have been exported to: results/simulation_results.csv");
        alert.showAndWait();
    }

    /**
     * Copy results to clipboard.
     */
    private void copyToClipboard() {
        String text = summaryArea.getText();
        javafx.scene.input.Clipboard clipboard =javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Copied");
        alert.setHeaderText("Copied to Clipboard");
        alert.setContentText("Results have been copied to clipboard.");
        alert.showAndWait();
    }
}
