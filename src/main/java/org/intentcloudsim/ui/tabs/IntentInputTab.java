package org.intentcloudsim.ui.tabs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import org.intentcloudsim.intent.Intent;
import org.intentcloudsim.intent.NaturalLanguageIntentParser;
import org.intentcloudsim.sla.SLANegotiationAgent;
import org.intentcloudsim.sla.SLAContract;
import org.intentcloudsim.tradeoff.CostPerformanceTradeoffEngine;
import org.intentcloudsim.ui.models.CloudConfig;

/**
 * Tab 1: Input natural language intent and receive parsed results with suggestions
 */
public class IntentInputTab extends VBox {

    private TextArea inputArea;
    private TextArea resultArea;
    private Label confidenceLabel;
    private Label dominantLabel;
    private TextArea suggestionsArea;
    private CloudConfig currentConfig;

    public IntentInputTab() {
        setPadding(new Insets(15));
        setSpacing(10);
        currentConfig = new CloudConfig();

        // Split into input/output
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.5);
        
        // Left side: Input and parse button
        VBox inputSection = createInputSection();
        
        // Right side: Results and suggestions
        VBox outputSection = createOutputSection();
        
        splitPane.getItems().addAll(inputSection, outputSection);
        
        getChildren().add(splitPane);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
    }

    /**
     * Create input section with text area and parse button
     */
    private VBox createInputSection() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        Label label = new Label("📝 Enter Your Cloud Infrastructure Intent");
        label.setStyle("-fx-font-size: 13; -fx-font-weight: bold;");

        inputArea = new TextArea();
        inputArea.setWrapText(true);
        inputArea.setPrefHeight(150);
        inputArea.setStyle("-fx-font-size: 11; -fx-font-family: 'Segoe UI';");
        inputArea.setText("I need fast, secure servers for banking applications at affordable cost");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button parseButton = new Button("🔍 Parse Intent");
        parseButton.setStyle("-fx-font-size: 11; -fx-padding: 10 20; -fx-text-fill: white; -fx-background-color: #3498db;");
        parseButton.setPrefWidth(140);
        parseButton.setOnAction(e -> parseAndAnalyze());

        Button exampleButton = new Button("📋 Example");
        exampleButton.setStyle("-fx-font-size: 11; -fx-padding: 10 15; -fx-background-color: #95a5a6;");
        exampleButton.setOnAction(e -> loadExample());

        Button clearButton = new Button("✕ Clear");
        clearButton.setStyle("-fx-font-size: 11; -fx-padding: 10 15; -fx-background-color: #95a5a6;");
        clearButton.setOnAction(e -> inputArea.clear());

        buttonBox.getChildren().addAll(parseButton, exampleButton, clearButton);

        box.getChildren().addAll(label, inputArea, buttonBox);
        VBox.setVgrow(inputArea, Priority.ALWAYS);
        return box;
    }

    /**
     * Create output section with parsing results and suggestions
     */
    private VBox createOutputSection() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        // Metrics row
        HBox metricsBox = new HBox(15);
        metricsBox.setPadding(new Insets(10));
        metricsBox.setStyle("-fx-border-color: #d0d0d0; -fx-background-color: #f5f5f5; -fx-border-radius: 3;");

        confidenceLabel = new Label("Confidence: 0%");
        confidenceLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #0066cc;");

        dominantLabel = new Label("Dominant: None");
        dominantLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Separator sep = new Separator(javafx.geometry.Orientation.VERTICAL);
        metricsBox.getChildren().addAll(confidenceLabel, sep, dominantLabel);

        // Parsing results
        Label resultLabel = new Label("🎯 Parsed Intent Details");
        resultLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        resultArea = new TextArea();
        resultArea.setWrapText(true);
        resultArea.setEditable(false);
        resultArea.setPrefHeight(200);
        resultArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");
        resultArea.setText("Results will appear here...");

        // Suggestions section
        Label suggestLabel = new Label("💡 Suggestions & SLA Recommendations");
        suggestLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        suggestionsArea = new TextArea();
        suggestionsArea.setWrapText(true);
        suggestionsArea.setEditable(false);
        suggestionsArea.setPrefHeight(180);
        suggestionsArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10; " +
                                  "-fx-control-inner-background: #f0f8f0;");
        suggestionsArea.setText("Suggestions will appear here...");

        VBox.setVgrow(resultArea, Priority.ALWAYS);
        VBox.setVgrow(suggestionsArea, Priority.ALWAYS);

        box.getChildren().addAll(metricsBox, resultLabel, resultArea, suggestLabel, suggestionsArea);
        return box;
    }

    /**
     * Parse the input text and analyze intent
     */
    private void parseAndAnalyze() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            resultArea.setText("Please enter an intent statement.");
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                // Parse with confidence
                NaturalLanguageIntentParser.ParseResult result =
                    NaturalLanguageIntentParser.parseWithConfidence(input);
                Intent intent = result.intent;

                // Generate SLA
                SLANegotiationAgent slaAgent = new SLANegotiationAgent();
                SLAContract sla = slaAgent.negotiate(intent);

                // Evaluate trade-offs
                CostPerformanceTradeoffEngine tradeoffEngine =
                    new CostPerformanceTradeoffEngine();
                double[] costs = {2.0, 5.0, 10.0, 15.0};
                double[] latencies = {150.0, 80.0, 40.0, 20.0};
                int bestOption = tradeoffEngine.findBestOption(costs, latencies, intent);

                // Update config with parsed intent
                currentConfig.userIntent = input;
                currentConfig.costPriority = intent.getCostPriority();
                currentConfig.latencyPriority = intent.getLatencyPriority();
                currentConfig.securityPriority = intent.getSecurityPriority();
                currentConfig.carbonPriority = intent.getCarbonPriority();
                
                currentConfig.maxLatencyMs = sla.getMaxLatencyMs();
                currentConfig.maxCostPerHour = sla.getMaxCostPerHour();
                currentConfig.minAvailabilityPercent = sla.getMinAvailability();
                currentConfig.securityLevel = sla.getMinSecurityLevel() > 5 ? "HIGH" : (sla.getMinSecurityLevel() > 2 ? "MEDIUM" : "LOW");

                // Prepare display
                StringBuilder results = new StringBuilder();
                results.append("╔════════════════════════════════════════╗\n");
                results.append("║    PARSED INTENT ANALYSIS             ║\n");
                results.append("╚════════════════════════════════════════╝\n\n");
                results.append("Input: ").append(input).append("\n\n");
                results.append("PRIORITY SCORES:\n");
                results.append(String.format("  • Cost Priority:     %.1f%%\n", intent.getCostPriority() * 100));
                results.append(String.format("  • Latency Priority:  %.1f%%\n", intent.getLatencyPriority() * 100));
                results.append(String.format("  • Security Priority: %.1f%%\n", intent.getSecurityPriority() * 100));
                results.append(String.format("  • Carbon Priority:   %.1f%%\n", intent.getCarbonPriority() * 100));

                StringBuilder suggestions = new StringBuilder();
                suggestions.append("╔════════════════════════════════════════╗\n");
                suggestions.append("║    RECOMMENDED CONFIGURATION          ║\n");
                suggestions.append("╚════════════════════════════════════════╝\n\n");
                suggestions.append("NEGOTIATED SLA:\n");
                suggestions.append(String.format("  • Max Latency:       %.0f ms\n", sla.getMaxLatencyMs()));
                suggestions.append(String.format("  • Max Cost:          $%.2f/hour\n", sla.getMaxCostPerHour()));
                suggestions.append(String.format("  • Min Availability:  %.1f%%\n", sla.getMinAvailability()));
                suggestions.append(String.format("  • Security Level:    %.1f\n\n", sla.getMinSecurityLevel()));

                suggestions.append("TRADE-OFF ANALYSIS:\n");
                for (int i = 0; i < costs.length; i++) {
                    String marker = (i == bestOption) ? " ⭐ RECOMMENDED" : "";
                    suggestions.append(String.format("  Option %d: $%.1f/hr, %.0fms latency%s\n",
                            i + 1, costs[i], latencies[i], marker));
                }

                suggestions.append("\nPLACEMENT STRATEGY:\n");
                if (intent.getCostPriority() > 0.7) {
                    suggestions.append("  • Consolidate VMs on fewer hosts\n");
                    suggestions.append("  • Use shared infrastructure\n");
                    currentConfig.vmPlacementPolicy = "CONSOLIDATED";
                } else if (intent.getLatencyPriority() > 0.7) {
                    suggestions.append("  • Spread VMs across multiple hosts\n");
                    suggestions.append("  • Use high-performance hosts\n");
                    currentConfig.vmPlacementPolicy = "SPREAD";
                } else if (intent.getSecurityPriority() > 0.7) {
                    suggestions.append("  • Use isolated/dedicated hosts\n");
                    suggestions.append("  • Enable encryption on all VMs\n");
                    currentConfig.vmPlacementPolicy = "ISOLATED";
                }

                if (intent.getCarbonPriority() > 0.5) {
                    suggestions.append("  • Use green/renewable energy datacenters\n");
                    currentConfig.greenDatacenter = true;
                }

                Platform.runLater(() -> {
                    resultArea.setText(results.toString());
                    suggestionsArea.setText(suggestions.toString());
                    confidenceLabel.setText(String.format("Confidence: %.0f%%", result.confidence * 100));
                    dominantLabel.setText("Dominant: " + result.dominantPriority);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    resultArea.setText("Error parsing intent: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Load an example intent
     */
    private void loadExample() {
        String[] examples = {
            "I need very cheap servers for batch processing",
            "Fast real-time gaming servers with low latency",
            "Secure, encrypted infrastructure for banking compliance",
            "Balanced cost-effective and responsive servers",
            "Green sustainable carbon-neutral infrastructure"
        };
        String example = examples[(int)(Math.random() * examples.length)];
        inputArea.setText(example);
        parseAndAnalyze();
    }

    /**
     * Get the current configuration
     */
    public CloudConfig getCurrentConfig() {
        return currentConfig;
    }

    /**
     * Set configuration from another tab
     */
    public void setCurrentConfig(CloudConfig config) {
        this.currentConfig = config;
    }
}
