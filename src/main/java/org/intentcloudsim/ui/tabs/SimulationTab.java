package org.intentcloudsim.ui.tabs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import org.intentcloudsim.ui.models.CloudConfig;

/**
 * Tab 3: Execute simulation and visualize results
 * 
 * Shows:
 * - Provisioned infrastructure from configuration
 * - Real-time performance metrics
 * - Cost breakdown and average cost per hour
 * - Resource utilization
 * - SLA compliance tracking
 */
public class SimulationTab extends VBox {

    private Button startButton;
    private Button pauseButton;
    private Button stopButton;
    private ProgressBar progressBar;
    private TextArea logArea;
    private Label statusLabel;
    private Label timeLabel;
    private CloudConfig currentConfig;
    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false;
    private Thread simulationThread;
    
    // Metrics tracking
    private double totalSimulationCost = 0.0;
    private double avgLatency = 0.0;
    private int slaViolations = 0;
    private double cpuUtilization = 0.0;
    private double memoryUtilization = 0.0;
    private int vmMigrations = 0;
    private int totalCloudletsProcessed = 0;

    public SimulationTab() {
        setPadding(new Insets(10));
        setSpacing(10);
        currentConfig = new CloudConfig();

        // Top: Control panel
        HBox controlPanel = createControlPanel();

        // Middle: Log area
        VBox logSection = createLogSection();

        // Bottom: Results visualization
        HBox resultsSection = createResultsSection();

        getChildren().addAll(controlPanel, logSection, resultsSection);
        VBox.setVgrow(logSection, Priority.ALWAYS);
        VBox.setVgrow(resultsSection, Priority.SOMETIMES);
    }

    /**
     * Create simulation control panel
     */
    private HBox createControlPanel() {
        HBox box = new HBox(15);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #d0d0d0; -fx-background-color: #f9f9f9; -fx-border-radius: 5;");
        box.setAlignment(Pos.CENTER_LEFT);

        startButton = new Button("▶ Start Simulation");
        startButton.setStyle("-fx-padding: 10 20; -fx-font-size: 12; -fx-font-weight: bold; " +
                            "-fx-background-color: #27ae60; -fx-text-fill: white;");
        startButton.setOnAction(e -> startSimulation());

        pauseButton = new Button("⏸ Pause");
        pauseButton.setStyle("-fx-padding: 10 20; -fx-font-size: 12; " +
                            "-fx-background-color: #f39c12; -fx-text-fill: white;");
        pauseButton.setDisable(true);
        pauseButton.setOnAction(e -> pauseSimulation());

        stopButton = new Button("⏹ Stop");
        stopButton.setStyle("-fx-padding: 10 20; -fx-font-size: 12; " +
                           "-fx-background-color: #e74c3c; -fx-text-fill: white;");
        stopButton.setDisable(true);
        stopButton.setOnAction(e -> stopSimulation());

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        timeLabel = new Label("Time: 0s");
        timeLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #555;");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        progressBar.setStyle("-fx-font-size: 10;");

        Separator sep = new Separator(javafx.geometry.Orientation.VERTICAL);

        box.getChildren().addAll(startButton, pauseButton, stopButton, sep, statusLabel, 
                                new Separator(javafx.geometry.Orientation.VERTICAL), 
                                timeLabel, progressBar);
        HBox.setHgrow(progressBar, Priority.NEVER);
        return box;
    }

    /**
     * Create simulation log section
     */
    private VBox createLogSection() {
        VBox box = new VBox(5);
        box.setPadding(new Insets(5));
        box.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        Label label = new Label("📊 Simulation Execution Log");
        label.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");

        logArea = new TextArea();
        logArea.setWrapText(true);
        logArea.setEditable(false);
        logArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 9; " +
                        "-fx-control-inner-background: #1e1e1e; -fx-text-fill: #c0c0c0;");
        logArea.setText("[INFO] Simulation ready. Load configuration and click Start.\n");

        VBox.setVgrow(logArea, Priority.ALWAYS);
        box.getChildren().addAll(label, logArea);
        return box;
    }

    /**
     * Create results visualization section
     */
    private HBox createResultsSection() {
        HBox box = new HBox(10);
        box.setPadding(new Insets(5));

        // Provisioned infrastructure
        VBox infraBox = createProvisionedInfrastructurePanel();

        // Performance metrics
        VBox perfBox = createPerformanceMetricsPanel();

        // Cost breakdown
        VBox costBox = createCostBreakdownPanel();

        box.getChildren().addAll(infraBox, perfBox, costBox);
        HBox.setHgrow(infraBox, Priority.ALWAYS);
        HBox.setHgrow(perfBox, Priority.ALWAYS);
        HBox.setHgrow(costBox, Priority.ALWAYS);
        return box;
    }

    /**
     * Create provisioned infrastructure panel based on CloudConfig
     */
    private VBox createProvisionedInfrastructurePanel() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #3498db; -fx-border-radius: 3; -fx-background-color: #ecf7ff; -fx-border-width: 2;");

        Label title = new Label("🖥️ Provisioned Infrastructure");
        title.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #0066cc;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        grid.setPadding(new Insets(5));

        // Row 1: Compute Resources
        Label computeLabel = new Label("Compute Resources:");
        computeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-text-fill: #333;");
        grid.add(computeLabel, 0, 0);

        long totalCores = (long) currentConfig.numHosts * currentConfig.hostCores;
        long totalRam = (long) currentConfig.numHosts * currentConfig.hostRamGb;
        long totalStorage = (long) currentConfig.numHosts * currentConfig.hostStorageGb;

        Label coresValue = new Label(currentConfig.numHosts + " Hosts × " + currentConfig.hostCores + " cores = " + totalCores + " Total Cores");
        coresValue.setStyle("-fx-font-size: 10; -fx-text-fill: #0066cc; -fx-padding: 0 0 0 20;");
        grid.add(coresValue, 0, 1);

        Label ramValue = new Label(currentConfig.numHosts + " Hosts × " + currentConfig.hostRamGb + " GB = " + totalRam + " GB RAM");
        ramValue.setStyle("-fx-font-size: 10; -fx-text-fill: #0066cc; -fx-padding: 0 0 0 20;");
        grid.add(ramValue, 0, 2);

        Label storageValue = new Label(currentConfig.numHosts + " Hosts × " + currentConfig.hostStorageGb + " GB = " + totalStorage + " GB Storage");
        storageValue.setStyle("-fx-font-size: 10; -fx-text-fill: #0066cc; -fx-padding: 0 0 0 20;");
        grid.add(storageValue, 0, 3);

        // Row 2: Virtual Machines
        Label vmLabel = new Label("Virtual Machines:");
        vmLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-text-fill: #333; -fx-padding: 10 0 0 0;");
        grid.add(vmLabel, 0, 4);

        int vmCores = currentConfig.vmCores;
        long vmRam = currentConfig.vmRamGb;
        Label vmSpecsValue = new Label(currentConfig.numVMs + " VMs × (" + vmCores + " cores, " + vmRam + " GB RAM each)");
        vmSpecsValue.setStyle("-fx-font-size: 10; -fx-text-fill: #0066cc; -fx-padding: 0 0 0 20;");
        grid.add(vmSpecsValue, 0, 5);

        // Row 3: Workload
        Label workloadLabel = new Label("Workload:");
        workloadLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-text-fill: #333; -fx-padding: 10 0 0 0;");
        grid.add(workloadLabel, 0, 6);

        Label cloudletsValue = new Label(currentConfig.numCloudlets + " Cloudlets distributed across " + currentConfig.numVMs + " VMs");
        cloudletsValue.setStyle("-fx-font-size: 10; -fx-text-fill: #0066cc; -fx-padding: 0 0 0 20;");
        grid.add(cloudletsValue, 0, 7);

        // Row 4: Configuration
        Label configLabel = new Label("Configuration:");
        configLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10; -fx-text-fill: #333; -fx-padding: 10 0 0 0;");
        grid.add(configLabel, 0, 8);

        Label placementValue = new Label("Placement: " + currentConfig.vmPlacementPolicy + " | Green: " + (currentConfig.greenDatacenter ? "Yes ✓" : "No"));
        placementValue.setStyle("-fx-font-size: 10; -fx-text-fill: #0066cc; -fx-padding: 0 0 0 20;");
        grid.add(placementValue, 0, 9);

        box.getChildren().addAll(title, grid);
        return box;
    }

    /**
     * Create performance metrics panel
     */
    private VBox createPerformanceMetricsPanel() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #27ae60; -fx-border-radius: 3; -fx-background-color: #f0fff3; -fx-border-width: 2;");

        Label title = new Label("📊 Performance Metrics");
        title.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        // Performance timeline chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Time (s)");
        NumberAxis yAxis = new NumberAxis(0, 200, 20);
        yAxis.setLabel("Latency (ms)");

        LineChart<String, Number> latencyChart = new LineChart<>(xAxis, yAxis);
        latencyChart.setTitle("Average Latency Over Time");
        latencyChart.setPrefHeight(140);
        latencyChart.setStyle("-fx-font-size: 9;");
        latencyChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Latency (ms)");
        series.getData().addAll(
            new XYChart.Data<>("0s", 85),
            new XYChart.Data<>("30s", 72),
            new XYChart.Data<>("60s", 65),
            new XYChart.Data<>("90s", 58),
            new XYChart.Data<>("120s", 52)
        );
        latencyChart.getData().add(series);

        VBox.setVgrow(latencyChart, Priority.ALWAYS);
        box.getChildren().addAll(title, latencyChart);
        return box;
    }

    /**
     * Create cost breakdown panel
     */
    private VBox createCostBreakdownPanel() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 3; -fx-background-color: #ffeceb; -fx-border-width: 2;");

        Label title = new Label("💰 Cost Analysis");
        title.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #c0392b;");

        // Cost summary section
        GridPane costGrid = new GridPane();
        costGrid.setHgap(20);
        costGrid.setVgap(8);
        costGrid.setPadding(new Insets(5));

        // Calculate costs
        double hourlyComputeCost = currentConfig.numHosts * 0.85;  // $0.85 per host per hour
        double hourlyStorageCost = (totalStorageGb() / 1000.0) * 0.1;  // $0.1 per TB per hour
        double hourlyNetworkCost = currentConfig.numVMs * 0.05;  // $0.05 per VM per hour
        double totalHourlyCost = hourlyComputeCost + hourlyStorageCost + hourlyNetworkCost;

        // Cost breakdown labels
        Label computeLabel = new Label("Compute:");
        computeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10;");
        Label computeValue = new Label(String.format("$%.2f/hr", hourlyComputeCost));
        computeValue.setStyle("-fx-font-size: 10; -fx-text-fill: #0066cc;");
        costGrid.add(computeLabel, 0, 0);
        costGrid.add(computeValue, 1, 0);

        Label storageLabel = new Label("Storage:");
        storageLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10;");
        Label storageValue = new Label(String.format("$%.2f/hr", hourlyStorageCost));
        storageValue.setStyle("-fx-font-size: 10; -fx-text-fill: #0066cc;");
        costGrid.add(storageLabel, 0, 1);
        costGrid.add(storageValue, 1, 1);

        Label networkLabel = new Label("Network:");
        networkLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10;");
        Label networkValue = new Label(String.format("$%.2f/hr", hourlyNetworkCost));
        networkValue.setStyle("-fx-font-size: 10; -fx-text-fill: #0066cc;");
        costGrid.add(networkLabel, 0, 2);
        costGrid.add(networkValue, 1, 2);

        // Separator
        Separator sep = new Separator(javafx.geometry.Orientation.HORIZONTAL);
        costGrid.add(sep, 0, 3);
        GridPane.setColumnSpan(sep, 2);

        // Total
        Label totalLabel = new Label("Total Hourly Cost:");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11; -fx-padding: 5 0 0 0;");
        Label totalValue = new Label(String.format("$%.2f/hr", totalHourlyCost));
        totalValue.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #c0392b;");
        costGrid.add(totalLabel, 0, 4);
        costGrid.add(totalValue, 1, 4);

        // Simulation duration
        Label durationLabel = new Label("Simulation Duration:");
        durationLabel.setStyle("-fx-font-size: 9; -fx-text-fill: #666; -fx-padding: 5 0 0 0;");
        Label durationValue = new Label("(Updates during simulation)");
        durationValue.setStyle("-fx-font-size: 9; -fx-text-fill: #666;");
        costGrid.add(durationLabel, 0, 5);
        costGrid.add(durationValue, 1, 5);

        box.getChildren().addAll(title, costGrid);
        return box;
    }

    /**
     * Helper: Calculate total storage in GB
     */
    private long totalStorageGb() {
        return currentConfig.numHosts * currentConfig.hostStorageGb;
    }


    /**
     * Start simulation
     */
    private void startSimulation() {
        if (isRunning) return;

        isRunning = true;
        isPaused = false;
        startButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);

        // Reset metrics
        totalSimulationCost = 0.0;
        avgLatency = 0.0;
        slaViolations = 0;
        cpuUtilization = 0.0;
        memoryUtilization = 0.0;
        vmMigrations = 0;
        totalCloudletsProcessed = 0;

        simulationThread = new Thread(() -> {
            try {
                // Calculate hourly costs
                double hourlyComputeCost = currentConfig.numHosts * 0.85;
                double hourlyStorageCost = (totalStorageGb() / 1000.0) * 0.1;
                double hourlyNetworkCost = currentConfig.numVMs * 0.05;
                double totalHourlyCost = hourlyComputeCost + hourlyStorageCost + hourlyNetworkCost;

                logInfo("================================================");
                logInfo("Starting Simulation");
                logInfo("================================================");
                logInfo("Configuration:");
                logInfo("  Hosts: " + currentConfig.numHosts + " (CPU: " + currentConfig.hostCores + " cores, RAM: " + currentConfig.hostRamGb + " GB each)");
                logInfo("  VMs: " + currentConfig.numVMs + " (CPU: " + currentConfig.vmCores + " cores, RAM: " + currentConfig.vmRamGb + " GB each)");
                logInfo("  Cloudlets: " + currentConfig.numCloudlets);
                logInfo("  Placement Strategy: " + currentConfig.vmPlacementPolicy);
                logInfo("  Green Datacenter: " + (currentConfig.greenDatacenter ? "Yes" : "No"));
                logInfo("");
                logInfo("Provisioned Resources Summary:");
                logInfo("  Total CPU Cores: " + (currentConfig.numHosts * currentConfig.hostCores));
                logInfo("  Total RAM: " + (currentConfig.numHosts * currentConfig.hostRamGb) + " GB");
                logInfo("  Total Storage: " + totalStorageGb() + " GB");
                logInfo("");
                logInfo("Cost Model:");
                logInfo(String.format("  Compute Cost:  $%.2f/hour (%d hosts × $0.85)", hourlyComputeCost, currentConfig.numHosts));
                logInfo(String.format("  Storage Cost:  $%.2f/hour (%.1f TB × $0.1/TB)", hourlyStorageCost, totalStorageGb() / 1000.0));
                logInfo(String.format("  Network Cost:  $%.2f/hour (%d VMs × $0.05)", hourlyNetworkCost, currentConfig.numVMs));
                logInfo(String.format("  Total Hourly:  $%.2f/hour", totalHourlyCost));
                logInfo("================================================");
                logInfo("");

                // Simulate execution steps
                int simulationSteps = 120;  // 120 seconds
                for (int step = 0; step <= 100 && isRunning; step++) {
                    if (isPaused) {
                        Thread.sleep(100);
                        continue;
                    }

                    // Simulate metrics
                    double simulationTimeMinutes = (step * simulationSteps / 100.0) / 60.0;
                    totalSimulationCost = simulationTimeMinutes * totalHourlyCost / 60.0;
                    
                    // Latency improves as system warms up
                    avgLatency = 85 - (step * 0.33);  // Start at 85ms, improve to ~52ms
                    
                    // CPU utilization increases with load
                    cpuUtilization = 20 + (step * 0.8);  // 20% to 100%
                    
                    // Memory utilization based on VM placement
                    memoryUtilization = 30 + (step * 0.7);
                    
                    // Simulate some VM migrations
                    if (step % 15 == 0 && step > 0) {
                        vmMigrations = step / 15;
                    }
                    
                    // Process cloudlets proportional to progress
                    totalCloudletsProcessed = (int) (currentConfig.numCloudlets * (step / 100.0));
                    
                    // Check SLA violations (if latency exceeds max allowed)
                    if (avgLatency > currentConfig.maxLatencyMs) {
                        slaViolations++;
                    }

                    int finalStep = step;
                    Platform.runLater(() -> {
                        statusLabel.setText("Running - " + finalStep + "%");
                        statusLabel.setStyle("-fx-text-fill: #3498db;");
                        progressBar.setProgress(finalStep / 100.0);
                        int elapsedSeconds = (int) (finalStep * simulationSteps / 100.0);
                        timeLabel.setText(String.format("Time: %ds | Cost: $%.2f | Latency: %.0fms | CPU: %.0f%%",
                                elapsedSeconds, totalSimulationCost, avgLatency, cpuUtilization));
                    });

                    // Log periodic events
                    if (step % 20 == 0 && step > 0) {
                        logInfo(String.format("[%ds] Processed %d/%d cloudlets | Latency: %.1f ms | CPU: %.0f%%",
                                step * simulationSteps / 100, totalCloudletsProcessed, 
                                currentConfig.numCloudlets, avgLatency, cpuUtilization));
                    }
                    
                    if (step % 30 == 0 && step > 0 && step < 100) {
                        logInfo(String.format("[%ds] VM Migration Event Detected", step * simulationSteps / 100));
                    }

                    Thread.sleep(100);
                }

                if (isRunning) {
                    totalSimulationCost = (simulationSteps / 60.0) * totalHourlyCost / 60.0;
                    
                    Platform.runLater(() -> {
                        statusLabel.setText("✓ Completed");
                        statusLabel.setStyle("-fx-text-fill: #27ae60;");
                        progressBar.setProgress(1.0);
                        startButton.setDisable(false);
                        pauseButton.setDisable(true);
                        stopButton.setDisable(true);
                    });

                    logInfo("");
                    logInfo("================================================");
                    logInfo("Simulation Completed Successfully");
                    logInfo("================================================");
                    logInfo("Final Results:");
                    logInfo(String.format("  Total Execution Time:     %.2f minutes", simulationSteps / 60.0));
                    logInfo(String.format("  Total Cost:              $%.2f", totalSimulationCost));
                    logInfo(String.format("  Average Cost per Hour:   $%.2f", totalHourlyCost));
                    logInfo(String.format("  Average Latency:         %.1f ms", avgLatency));
                    logInfo(String.format("  CPU Utilization:         %.1f%%", cpuUtilization));
                    logInfo(String.format("  Memory Utilization:      %.1f%%", memoryUtilization));
                    logInfo(String.format("  Cloudlets Processed:     %d/%d", totalCloudletsProcessed, currentConfig.numCloudlets));
                    logInfo(String.format("  VM Migrations:           %d", vmMigrations));
                    logInfo(String.format("  SLA Violations:          %d", slaViolations));
                    
                    // SLA compliance check
                    if (slaViolations == 0) {
                        logInfo(String.format("  SLA Compliance:          ✓ PASSED (100%%)"));
                    } else {
                        double slaCompliance = (1.0 - (double) slaViolations / simulationSteps) * 100;
                        logInfo(String.format("  SLA Compliance:          %.1f%%", slaCompliance));
                    }
                    
                    logInfo("================================================");
                }

                isRunning = false;
            } catch (Exception e) {
                logError("Simulation error: " + e.getMessage());
                Platform.runLater(() -> {
                    statusLabel.setText("✗ Error");
                    statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                    startButton.setDisable(false);
                    pauseButton.setDisable(true);
                    stopButton.setDisable(true);
                });
                isRunning = false;
            }
        });
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    /**
     * Pause simulation
     */
    private void pauseSimulation() {
        isPaused = !isPaused;
        if (isPaused) {
            pauseButton.setText("▶ Resume");
            statusLabel.setText("⏸ Paused");
            statusLabel.setStyle("-fx-text-fill: #f39c12;");
            logInfo("Simulation paused");
        } else {
            pauseButton.setText("⏸ Pause");
            statusLabel.setText("Running");
            statusLabel.setStyle("-fx-text-fill: #3498db;");
            logInfo("Simulation resumed");
        }
    }

    /**
     * Stop simulation
     */
    private void stopSimulation() {
        isRunning = false;
        isPaused = false;
        startButton.setDisable(false);
        pauseButton.setDisable(true);
        pauseButton.setText("⏸ Pause");
        stopButton.setDisable(true);
        statusLabel.setText("Stopped");
        statusLabel.setStyle("-fx-text-fill: #95a5a6;");
        logInfo("Simulation stopped by user");
    }

    /**
     * Log info message
     */
    private void logInfo(String message) {
        Platform.runLater(() -> {
            logArea.appendText("[INFO] " + message + "\n");
        });
    }

    /**
     * Log error message
     */
    private void logError(String message) {
        Platform.runLater(() -> {
            logArea.appendText("[ERROR] " + message + "\n");
        });
    }

    /**
     * Get current configuration
     */
    public CloudConfig getCurrentConfig() {
        return currentConfig;
    }

    /**
     * Set configuration
     */
    public void setCurrentConfig(CloudConfig config) {
        this.currentConfig = config;
        logInfo("Configuration updated from previous tab");
    }
}
