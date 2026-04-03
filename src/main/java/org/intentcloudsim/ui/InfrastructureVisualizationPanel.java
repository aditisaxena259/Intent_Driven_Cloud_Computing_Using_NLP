package org.intentcloudsim.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Panel for visualizing cloud infrastructure.
 * Shows datacenters, hosts, VMs, and cloudlets in a visual representation.
 */
public class InfrastructureVisualizationPanel extends VBox {

    private Canvas canvas;
    private Spinner<Integer> hostCountSpinner;
    private Spinner<Integer> vmCountSpinner;
    private Spinner<Integer> cloudletCountSpinner;
    private Label statsLabel;

    public InfrastructureVisualizationPanel() {
        setPadding(new Insets(15));
        setSpacing(10);

        // Control panel
        getChildren().add(createControlPanel());

        // Canvas for visualization
        HBox canvasBox = new HBox();
        canvasBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #ffffff;");
        canvasBox.setPadding(new Insets(10));

        canvas = new Canvas(800, 500);
        canvas.setStyle("-fx-border-color: #cccccc;");
        canvasBox.getChildren().add(canvas);

        getChildren().add(canvasBox);

        // Stats panel
        getChildren().add(createStatsPanel());

        VBox.setVgrow(canvasBox, Priority.ALWAYS);

        // Draw initial infrastructure
        drawInfrastructure();
    }

    /**
     * Create the control panel for infrastructure settings.
     */
    private HBox createControlPanel() {
        HBox controlBox = new HBox(15);
        controlBox.setPadding(new Insets(10));
        controlBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
        controlBox.setAlignment(Pos.CENTER_LEFT);

        Label hostLabel = new Label("Hosts:");
        hostCountSpinner = new Spinner<>(1, 16, 4);
        hostCountSpinner.setPrefWidth(80);
        hostCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> drawInfrastructure());

        Label vmLabel = new Label("VMs:");
        vmCountSpinner = new Spinner<>(1, 32, 4);
        vmCountSpinner.setPrefWidth(80);
        vmCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> drawInfrastructure());

        Label cloudletLabel = new Label("Cloudlets:");
        cloudletCountSpinner = new Spinner<>(1, 64, 8);
        cloudletCountSpinner.setPrefWidth(80);
        cloudletCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> drawInfrastructure());

        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setStyle("-fx-font-size: 11; -fx-padding: 6 12;");
        refreshButton.setOnAction(e -> drawInfrastructure());

        controlBox.getChildren().addAll(
            hostLabel, hostCountSpinner,
            vmLabel, vmCountSpinner,
            cloudletLabel, cloudletCountSpinner,
            new Separator(javafx.geometry.Orientation.VERTICAL),
            refreshButton
        );

        return controlBox;
    }

    /**
     * Create the statistics panel.
     */
    private VBox createStatsPanel() {
        VBox statsBox = new VBox(8);
        statsBox.setPadding(new Insets(10));
        statsBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        Label titleLabel = new Label("Infrastructure Statistics");
        titleLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");

        statsLabel = new Label();
        updateStats();

        statsBox.getChildren().addAll(titleLabel, statsLabel);

        return statsBox;
    }

    /**
     * Draw the infrastructure visualization on canvas.
     */
    private void drawInfrastructure() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        // Clear canvas
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        // Draw border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(0, 0, width - 1, height - 1);

        int numHosts = hostCountSpinner.getValue();
        int numVMs = vmCountSpinner.getValue();
        int numCloudlets = cloudletCountSpinner.getValue();

        // Draw datacenters
        drawDatacenter(gc, width, height, numHosts, numVMs, numCloudlets);

        updateStats();
    }

    /**
     * Draw a datacentre with hosts, VMs, and cloudlets.
     */
    private void drawDatacenter(GraphicsContext gc, double width, double height,
                                int numHosts, int numVMs, int numCloudlets) {

        // Title
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font("Arial", 14));
        gc.fillText("Datacenter Architecture", 20, 30);

        double hostsPerRow = Math.ceil(Math.sqrt(numHosts));
        double hostWidth = (width - 60) / hostsPerRow;
        double hostHeight = 120;
        double startX = 30;
        double startY = 60;

        // Draw hosts
        for (int h = 0; h < numHosts; h++) {
            int row = (int)(h / hostsPerRow);
            int col = h % (int)hostsPerRow;

            double hostX = startX + col * hostWidth;
            double hostY = startY + row * hostHeight;

            drawHost(gc, hostX, hostY, hostWidth - 10, hostHeight - 10, h + 1, numVMs / numHosts, numCloudlets / numHosts);
        }

        // Legend
        drawLegend(gc, width, height);
    }

    /**
     * Draw a single host with its VMs.
     */
    private void drawHost(GraphicsContext gc, double x, double y, double width, double height,
                         int hostId, int vmsPerHost, int cloudletsPerHost) {

        // Host box
        gc.setStroke(Color.web("#2c3e50"));
        gc.setLineWidth(2);
        gc.setFill(Color.web("#ecf0f1"));
        gc.fillRect(x, y, width, height);
        gc.strokeRect(x, y, width, height);

        // Host label
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font("Arial", 10));
        gc.fillText("Host #" + hostId, x + 5, y + 18);

        // CPU/Resources indicator
        gc.setFont(javafx.scene.text.Font.font("Arial", 8));
        gc.setFill(Color.web("#555555"));
        gc.fillText("CPU: 8 cores | RAM: 16GB | Disk: 1TB", x + 5, y + 32);

        double vmWidth = (width - 10) / Math.max(1, vmsPerHost);
        double vmHeight = 35;
        double vmStartX = x + 5;
        double vmStartY = y + 40;

        // Draw VMs
        for (int v = 0; v < vmsPerHost; v++) {
            double vmX = vmStartX + v * vmWidth;
            drawVM(gc, vmX, vmStartY, vmWidth - 2, vmHeight, v + 1, cloudletsPerHost);
        }

        // CPU usage bar
        gc.setFill(Color.web("#3498db"));
        double cpuUsage = 0.65; // Example usage
        gc.fillRect(x + 5, y + height - 15, (width - 10) * cpuUsage, 10);
        gc.setStroke(Color.web("#2c3e50"));
        gc.strokeRect(x + 5, y + height - 15, width - 10, 10);

        gc.setFont(javafx.scene.text.Font.font("Arial", 7));
        gc.setFill(Color.BLACK);
        gc.fillText("CPU: " + String.format("%.0f%%", cpuUsage * 100), x + width - 35, y + height - 5);
    }

    /**
     * Draw a virtual machine box.
     */
    private void drawVM(GraphicsContext gc, double x, double y, double width, double height,
                       int vmId, int cloudletsInVM) {

        gc.setStroke(Color.web("#27ae60"));
        gc.setLineWidth(1);
        gc.setFill(Color.web("#d5f4e6"));
        gc.fillRect(x, y, width, height);
        gc.strokeRect(x, y, width, height);

        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font("Arial", 7));
        gc.fillText("VM" + vmId, x + 2, y + 12);

        // Cloudlet indicators
        double cloudletSize = 4;
        for (int c = 0; c < Math.min(cloudletsInVM, 3); c++) {
            double dotX = x + 2 + c * 6;
            double dotY = y + 20;
            gc.setFill(Color.web("#e74c3c"));
            gc.fillOval(dotX, dotY, cloudletSize, cloudletSize);
        }

        if (cloudletsInVM > 3) {
            gc.setFill(Color.BLACK);
            gc.fillText("+", x + 20, y + 24);
        }
    }

    /**
     * Draw legend.
     */
    private void drawLegend(GraphicsContext gc, double width, double height) {
        double legendX = width - 180;
        double legendY = height - 80;

        gc.setFont(javafx.scene.text.Font.font("Arial", 9));
        gc.setFill(Color.BLACK);
        gc.fillText("Legend:", legendX, legendY);

        // Host
        gc.setFill(Color.web("#ecf0f1"));
        gc.fillRect(legendX, legendY + 10, 20, 15);
        gc.setStroke(Color.web("#2c3e50"));
        gc.strokeRect(legendX, legendY + 10, 20, 15);
        gc.setFont(javafx.scene.text.Font.font("Arial", 8));
        gc.setFill(Color.BLACK);
        gc.fillText("Host", legendX + 25, legendY + 22);

        // VM
        gc.setFill(Color.web("#d5f4e6"));
        gc.fillRect(legendX, legendY + 30, 20, 15);
        gc.setStroke(Color.web("#27ae60"));
        gc.strokeRect(legendX, legendY + 30, 20, 15);
        gc.fillText("VM", legendX + 25, legendY + 42);

        // Cloudlet
        gc.setFill(Color.web("#e74c3c"));
        gc.fillOval(legendX, legendY + 50, 6, 6);
        gc.setFill(Color.BLACK);
        gc.fillText("Cloudlet", legendX + 25, legendY + 55);
    }

    /**
     * Update the statistics label.
     */
    private void updateStats() {
        int hosts = hostCountSpinner.getValue();
        int vms = vmCountSpinner.getValue();
        int cloudlets = cloudletCountSpinner.getValue();

        long totalCores = hosts * 8L;
        long totalRam = hosts * 16L;
        long totalDisk = hosts * 1000L;

        String stats = String.format(
            "Hosts: %d | VMs: %d | Cloudlets: %d | Total Cores: %d | Total RAM: %dGB | Total Disk: %dGB | " +
            "Avg CPU/Host: %.1f%% | Avg Mem/VM: %.1fGB",
            hosts, vms, cloudlets, totalCores, totalRam, totalDisk,
            65.0, 4.0
        );

        statsLabel.setText(stats);
    }
}
