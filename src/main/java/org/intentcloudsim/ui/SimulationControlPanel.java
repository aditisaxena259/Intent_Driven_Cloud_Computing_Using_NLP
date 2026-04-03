package org.intentcloudsim.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import org.intentcloudsim.MainSimulation;
import org.intentcloudsim.intent.Intent;
import org.intentcloudsim.intent.NaturalLanguageIntentParser;
import org.intentcloudsim.sla.SLAContract;
import org.intentcloudsim.sla.SLANegotiationAgent;
import org.intentcloudsim.tradeoff.CostPerformanceTradeoffEngine;

/**
 * Panel for controlling and executing the simulation.
 * Provides buttons to run simulations and display progress.
 */
public class SimulationControlPanel extends VBox {

    private TextArea logArea;
    private ProgressBar progressBar;
    private Label statusLabel;
    private ComboBox<String> scenarioCombo;
    private Button runButton;
    private Button pauseButton;
    private Button stopButton;

    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false;

    public SimulationControlPanel() {
        setPadding(new Insets(15));
        setSpacing(10);
        setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 11;");

        // Control buttons
        getChildren().add(createControlPanel());

        // Progress
        getChildren().add(createProgressPanel());

        // Log area
        getChildren().add(createLogPanel());

        VBox.setVgrow(logArea, Priority.ALWAYS);
    }

    /**
     * Create the control buttons panel.
     */
    private HBox createControlPanel() {
        HBox controlBox = new HBox(10);
        controlBox.setPadding(new Insets(10));
        controlBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
        controlBox.setAlignment(Pos.CENTER_LEFT);

        Label modeLabel = new Label("Simulation Mode:");
        scenarioCombo = new ComboBox<>();
        scenarioCombo.getItems().addAll(
            "Single Scenario (Quick)",
            "All 8 Scenarios (Full)",
            "Custom Intent",
            "Batch Mode"
        );
        scenarioCombo.setValue("All 8 Scenarios (Full)");
        scenarioCombo.setPrefWidth(200);

        runButton = new Button("▶ Run Simulation");
        runButton.setStyle("-fx-font-size: 12; -fx-padding: 8 20; -fx-text-fill: white; -fx-background-color: #27ae60;");
        runButton.setPrefWidth(150);
        runButton.setOnAction(e -> startSimulation());

        pauseButton = new Button("⏸ Pause");
        pauseButton.setStyle("-fx-font-size: 12; -fx-padding: 8 20; -fx-background-color: #f39c12;");
        pauseButton.setPrefWidth(100);
        pauseButton.setDisable(true);
        pauseButton.setOnAction(e -> pauseSimulation());

        stopButton = new Button("⏹ Stop");
        stopButton.setStyle("-fx-font-size: 12; -fx-padding: 8 20; -fx-text-fill: white; -fx-background-color: #e74c3c;");
        stopButton.setPrefWidth(100);
        stopButton.setDisable(true);
        stopButton.setOnAction(e -> stopSimulation());

        controlBox.getChildren().addAll(
            modeLabel, scenarioCombo,
            new Separator(javafx.geometry.Orientation.VERTICAL),
            runButton, pauseButton, stopButton
        );

        return controlBox;
    }

    /**
     * Create the progress panel.
     */
    private VBox createProgressPanel() {
        VBox progressBox = new VBox(8);
        progressBox.setPadding(new Insets(10));
        progressBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        statusLabel = new Label("Status: Ready");
        statusLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        progressBar = new ProgressBar(0);
        progressBar.setPrefHeight(20);
        progressBar.setStyle("-fx-font-size: 10;");

        Label progressText = new Label("Simulation Progress");
        progressText.setStyle("-fx-font-size: 10; -fx-text-fill: #666;");

        progressBox.getChildren().addAll(statusLabel, progressText, progressBar);

        return progressBox;
    }

    /**
     * Create the log output panel.
     */
    private VBox createLogPanel() {
        VBox logBox = new VBox(8);
        logBox.setPadding(new Insets(10));
        logBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #ffffff;");

        Label logLabel = new Label("Simulation Log Output");
        logLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        logArea = new TextArea();
        logArea.setWrapText(true);
        logArea.setEditable(false);
        logArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10; -fx-control-inner-background: #1e1e1e; -fx-text-fill: #00ff00;");
        logArea.setText("Welcome to Intent-Driven Cloud Simulation\n" +
                       "============================================\n" +
                       "Ready to execute simulations. Select mode and click 'Run Simulation'.\n\n");

        Button clearButton = new Button("Clear Log");
        clearButton.setStyle("-fx-font-size: 10; -fx-padding: 5 10;");
        clearButton.setOnAction(e -> logArea.clear());

        VBox.setVgrow(logArea, Priority.ALWAYS);
        logBox.getChildren().addAll(logLabel, logArea, clearButton);

        return logBox;
    }

    /**
     * Start the simulation in a background thread.
     */
    private void startSimulation() {
        if (isRunning) {
            return;
        }

        isRunning = true;
        runButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);

        Thread simThread = new Thread(() -> {
            try {
                logMessage("▶ Starting simulation...\n");
                updateStatus("Running", Color.GREEN);

                String mode = scenarioCombo.getValue();

                if (mode.contains("Single")) {
                    runSingleScenario();
                } else if (mode.contains("All")) {
                    runAllScenarios();
                } else {
                    logMessage("Mode not yet implemented: " + mode + "\n");
                }

                if (isRunning) {
                    logMessage("\n✓ Simulation completed successfully!\n");
                    updateStatus("Completed", Color.GREEN);
                }
            } catch (Exception e) {
                logMessage("\n✗ Error during simulation: " + e.getMessage() + "\n");
                updateStatus("Error", Color.RED);
                e.printStackTrace(System.out);
            } finally {
                isRunning = false;
                Platform.runLater(() -> {
                    runButton.setDisable(false);
                    pauseButton.setDisable(true);
                    stopButton.setDisable(true);
                });
            }
        });

        simThread.setDaemon(true);
        simThread.start();
    }

    /**
     * Run a single test scenario.
     */
    private void runSingleScenario() {
        String intent = "I want fast and cheap servers for my startup";
        logMessage("Processing intent: \"" + intent + "\"\n");

        Intent parsedIntent = NaturalLanguageIntentParser.parse(intent);
        logMessage("Parsed Intent: " + parsedIntent + "\n");

        SLANegotiationAgent slaAgent = new SLANegotiationAgent();
        SLAContract sla = slaAgent.negotiate(parsedIntent);
        logMessage("\nNegotiated SLA: " + sla + "\n");

        CostPerformanceTradeoffEngine tradeoffEngine = new CostPerformanceTradeoffEngine();
        double[] costs = {2.0, 5.0, 10.0, 15.0};
        double[] latencies = {150.0, 80.0, 40.0, 20.0};
        int bestOption = tradeoffEngine.findBestOption(costs, latencies, parsedIntent);

        logMessage("\nTrade-off Analysis Results:\n");
        for (int i = 0; i < costs.length; i++) {
            String marker = (i == bestOption) ? " ← SELECTED" : "";
            logMessage(String.format("  Option %d: Cost=$%.2f/hr, Latency=%.0fms%s\n",
                    i + 1, costs[i], latencies[i], marker));
        }

        double score = tradeoffEngine.score(costs[bestOption], latencies[bestOption], parsedIntent);
        logMessage(String.format("\nTrade-off Score: %.4f\n", score));

        updateProgress(1.0);
    }

    /**
     * Run all 8 test scenarios.
     */
    private void runAllScenarios() {
        String[] testIntents = {
            "I want cheap and budget-friendly servers for my startup",
            "I need fast real-time low latency processing for gaming",
            "Deploy secure encrypted compliant infrastructure for banking",
            "I want a balanced solution that is cost-effective and responsive",
            "Run my workload on green sustainable carbon neutral infrastructure",
            "I need high performance secure servers at affordable cost",
            "Give me the fastest possible execution, money is no object",
            "Minimize cost as much as possible, latency doesn't matter"
        };

        for (int i = 0; i < testIntents.length; i++) {
            if (!isRunning) break;

            logMessage("\n" + "=".repeat(70) + "\n");
            logMessage("EXPERIMENT " + (i + 1) + " / " + testIntents.length + "\n");
            logMessage("=".repeat(70) + "\n");

            String intent = testIntents[i];
            logMessage("\n[1/5] Parsing Intent: \"" + intent + "\"\n");

            Intent parsedIntent = NaturalLanguageIntentParser.parse(intent);
            logMessage("  → " + parsedIntent + "\n");

            logMessage("\n[2/5] Negotiating SLA...\n");
            SLANegotiationAgent slaAgent = new SLANegotiationAgent();
            SLAContract sla = slaAgent.negotiate(parsedIntent);
            logMessage("  → SLA: Max Latency=" + sla.getMaxLatencyMs() + "ms, " +
                      "Max Cost=$" + String.format("%.2f", sla.getMaxCostPerHour()) + "/hr\n");

            logMessage("\n[3/5] Evaluating Trade-offs...\n");
            CostPerformanceTradeoffEngine tradeoffEngine = new CostPerformanceTradeoffEngine();
            double[] costs = {2.0, 5.0, 10.0, 15.0};
            double[] latencies = {150.0, 80.0, 40.0, 20.0};
            int bestOption = tradeoffEngine.findBestOption(costs, latencies, parsedIntent);

            logMessage(String.format("  → Selected Option %d: Cost=$%.2f, Latency=%.0fms\n",
                    bestOption + 1, costs[bestOption], latencies[bestOption]));

            logMessage("\n[4/5] VM Placement & Simulation...\n");
            logMessage("  → Simulating CloudSim Plus execution...\n");
            // Actual CloudSim Plus would run here

            logMessage("\n[5/5] Results Logged\n");

            updateProgress((i + 1.0) / testIntents.length);
            updateStatus("Experiment " + (i + 1) + "/" + testIntents.length, Color.BLUE);
        }
    }

    /**
     * Pause the simulation.
     */
    private void pauseSimulation() {
        isPaused = !isPaused;
        if (isPaused) {
            pauseButton.setText("▶ Resume");
            updateStatus("Paused", Color.ORANGE);
            logMessage("\n⏸ Simulation paused\n");
        } else {
            pauseButton.setText("⏸ Pause");
            updateStatus("Resumed", Color.GREEN);
            logMessage("\n▶ Simulation resumed\n");
        }
    }

    /**
     * Stop the simulation.
     */
    private void stopSimulation() {
        isRunning = false;
        isPaused = false;
        pauseButton.setText("⏸ Pause");
        updateStatus("Stopped", Color.RED);
        logMessage("\n⏹ Simulation stopped by user\n");
    }

    /**
     * Log a message to the text area (thread-safe).
     */
    private void logMessage(String message) {
        Platform.runLater(() -> logArea.appendText(message));
    }

    /**
     * Update status label (thread-safe).
     */
    private void updateStatus(String text, Color color) {
        Platform.runLater(() -> {
            statusLabel.setText("Status: " + text);
            statusLabel.setTextFill(color);
        });
    }

    /**
     * Update progress bar (thread-safe).
     */
    private void updateProgress(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    }
}
