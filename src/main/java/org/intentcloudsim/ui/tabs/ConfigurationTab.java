package org.intentcloudsim.ui.tabs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.intentcloudsim.ui.models.CloudConfig;

/**
 * Tab 2: Edit and manage cloud configuration (JSON editor + form fields)
 */
public class ConfigurationTab extends VBox {

    private TextArea jsonEditor;
    private TextField userIdField;
    private Spinner<Double> costPrioritySpinner;
    private Spinner<Double> latencyPrioritySpinner;
    private Spinner<Double> securityPrioritySpinner;
    private Spinner<Double> carbonPrioritySpinner;
    private Spinner<Double> maxLatencySpinner;
    private Spinner<Double> maxCostSpinner;
    private Spinner<Double> minAvailabilitySpinner;
    private ComboBox<String> securityLevelCombo;
    private Spinner<Integer> numHostsSpinner;
    private Spinner<Integer> numVMsSpinner;
    private Spinner<Integer> numCloudletsSpinner;
    private ComboBox<String> placementPolicyCombo;
    private CheckBox greenDatacenterCheck;
    private Label statusLabel;
    private CloudConfig currentConfig;

    public ConfigurationTab() {
        setPadding(new Insets(10));
        setSpacing(10);
        currentConfig = new CloudConfig();

        // Split pane: JSON on left, form on right
        SplitPane mainSplit = new SplitPane();
        mainSplit.setDividerPositions(0.5);

        VBox jsonSection = createJsonSection();
        VBox formSection = createFormSection();

        mainSplit.getItems().addAll(jsonSection, formSection);
        VBox.setVgrow(mainSplit, Priority.ALWAYS);

        // Bottom: Status and buttons
        HBox buttonBox = createButtonBox();

        getChildren().addAll(mainSplit, buttonBox);
    }

    /**
     * Create JSON editor section
     */
    private VBox createJsonSection() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        Label label = new Label("📄 Configuration JSON");
        label.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        jsonEditor = new TextArea();
        jsonEditor.setWrapText(false);
        jsonEditor.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10;");
        jsonEditor.setText(formatJson(currentConfig.toJson()));

        // Add listener to sync form when JSON changes
        jsonEditor.textProperty().addListener((obs, old, newVal) -> {
            // Validate JSON while typing
            try {
                CloudConfig.fromJson(newVal);
                jsonEditor.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10; " +
                                    "-fx-control-inner-background: #f0f8f0;");
                statusLabel.setText("✓ JSON valid");
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            } catch (JsonSyntaxException e) {
                jsonEditor.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 10; " +
                                    "-fx-control-inner-background: #fff5f5;");
                statusLabel.setText("✗ Invalid JSON: " + e.getMessage());
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        });

        VBox.setVgrow(jsonEditor, Priority.ALWAYS);
        box.getChildren().addAll(label, jsonEditor);
        return box;
    }

    /**
     * Create form fields section
     */
    private VBox createFormSection() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        Label label = new Label("⚙️ Configuration Form");
        label.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(5));

        int row = 0;

        // User ID
        form.add(new Label("User ID:"), 0, row);
        userIdField = new TextField();
        userIdField.setText(currentConfig.userId != null ? currentConfig.userId : "user-001");
        form.add(userIdField, 1, row++);

        // Priorities
        form.add(new Label("Cost Priority:"), 0, row);
        costPrioritySpinner = new Spinner<>(0.0, 1.0, currentConfig.costPriority, 0.1);
        costPrioritySpinner.setEditable(true);
        form.add(costPrioritySpinner, 1, row++);

        form.add(new Label("Latency Priority:"), 0, row);
        latencyPrioritySpinner = new Spinner<>(0.0, 1.0, currentConfig.latencyPriority, 0.1);
        latencyPrioritySpinner.setEditable(true);
        form.add(latencyPrioritySpinner, 1, row++);

        form.add(new Label("Security Priority:"), 0, row);
        securityPrioritySpinner = new Spinner<>(0.0, 1.0, currentConfig.securityPriority, 0.1);
        securityPrioritySpinner.setEditable(true);
        form.add(securityPrioritySpinner, 1, row++);

        form.add(new Label("Carbon Priority:"), 0, row);
        carbonPrioritySpinner = new Spinner<>(0.0, 1.0, currentConfig.carbonPriority, 0.1);
        carbonPrioritySpinner.setEditable(true);
        form.add(carbonPrioritySpinner, 1, row++);

        // SLA
        form.add(new Label("Max Latency (ms):"), 0, row);
        maxLatencySpinner = new Spinner<>(10.0, 10000.0, currentConfig.maxLatencyMs, 10.0);
        maxLatencySpinner.setEditable(true);
        form.add(maxLatencySpinner, 1, row++);

        form.add(new Label("Max Cost ($/hr):"), 0, row);
        maxCostSpinner = new Spinner<>(0.1, 1000.0, currentConfig.maxCostPerHour, 0.5);
        maxCostSpinner.setEditable(true);
        form.add(maxCostSpinner, 1, row++);

        form.add(new Label("Min Availability (%):"), 0, row);
        minAvailabilitySpinner = new Spinner<>(50.0, 100.0, currentConfig.minAvailabilityPercent, 1.0);
        minAvailabilitySpinner.setEditable(true);
        form.add(minAvailabilitySpinner, 1, row++);

        form.add(new Label("Security Level:"), 0, row);
        securityLevelCombo = new ComboBox<>();
        securityLevelCombo.getItems().addAll("LOW", "MEDIUM", "HIGH");
        securityLevelCombo.setValue(currentConfig.securityLevel != null ? currentConfig.securityLevel : "MEDIUM");
        form.add(securityLevelCombo, 1, row++);

        // Infrastructure
        form.add(new Label("Number of Hosts:"), 0, row);
        numHostsSpinner = new Spinner<>(1, 1000, currentConfig.numHosts, 1);
        numHostsSpinner.setEditable(true);
        form.add(numHostsSpinner, 1, row++);

        form.add(new Label("Number of VMs:"), 0, row);
        numVMsSpinner = new Spinner<>(1, 10000, currentConfig.numVMs, 1);
        numVMsSpinner.setEditable(true);
        form.add(numVMsSpinner, 1, row++);

        form.add(new Label("Number of Cloudlets:"), 0, row);
        numCloudletsSpinner = new Spinner<>(1, 100000, currentConfig.numCloudlets, 1);
        numCloudletsSpinner.setEditable(true);
        form.add(numCloudletsSpinner, 1, row++);

        // Placement & Green
        form.add(new Label("Placement Policy:"), 0, row);
        placementPolicyCombo = new ComboBox<>();
        placementPolicyCombo.getItems().addAll("CONSOLIDATED", "SPREAD", "ISOLATED");
        placementPolicyCombo.setValue(currentConfig.vmPlacementPolicy != null ? currentConfig.vmPlacementPolicy : "SPREAD");
        form.add(placementPolicyCombo, 1, row++);

        form.add(new Label("Green Datacenter:"), 0, row);
        greenDatacenterCheck = new CheckBox();
        greenDatacenterCheck.setSelected(currentConfig.greenDatacenter);
        form.add(greenDatacenterCheck, 1, row++);

        scroll.setContent(form);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        box.getChildren().addAll(label, scroll);
        return box;
    }

    /**
     * Create button bar
     */
    private HBox createButtonBox() {
        HBox box = new HBox(10);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle("-fx-border-color: #d0d0d0; -fx-background-color: #f9f9f9;");

        Button syncFromJsonButton = new Button("📥 JSON → Form");
        syncFromJsonButton.setStyle("-fx-padding: 8 15; -fx-font-size: 11;");
        syncFromJsonButton.setOnAction(e -> syncFromJson());

        Button syncToJsonButton = new Button("📤 Form → JSON");
        syncToJsonButton.setStyle("-fx-padding: 8 15; -fx-font-size: 11;");
        syncToJsonButton.setOnAction(e -> syncToJson());

        Button validateButton = new Button("✓ Validate");
        validateButton.setStyle("-fx-padding: 8 15; -fx-font-size: 11; -fx-background-color: #27ae60; -fx-text-fill: white;");
        validateButton.setOnAction(e -> validateConfiguration());

        Button resetButton = new Button("⟲ Reset");
        resetButton.setStyle("-fx-padding: 8 15; -fx-font-size: 11; -fx-background-color: #95a5a6; -fx-text-fill: white;");
        resetButton.setOnAction(e -> resetConfiguration());

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-font-size: 11;");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        box.getChildren().addAll(syncFromJsonButton, syncToJsonButton, validateButton, resetButton, statusLabel);
        return box;
    }

    /**
     * Sync from JSON editor to form fields
     */
    private void syncFromJson() {
        try {
            CloudConfig config = CloudConfig.fromJson(jsonEditor.getText());
            currentConfig = config;
            updateFormFromConfig();
            statusLabel.setText("✓ Synced from JSON");
            statusLabel.setStyle("-fx-text-fill: #27ae60;");
        } catch (Exception e) {
            statusLabel.setText("✗ Error syncing JSON: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    /**
     * Sync from form fields to JSON
     */
    private void syncToJson() {
        updateConfigFromForm();
        jsonEditor.setText(formatJson(currentConfig.toJson()));
        statusLabel.setText("✓ Synced to JSON");
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
    }

    /**
     * Validate configuration
     */
    private void validateConfiguration() {
        updateConfigFromForm();
        StringBuilder issues = new StringBuilder();

        if (currentConfig.costPriority + currentConfig.latencyPriority +
            currentConfig.securityPriority + currentConfig.carbonPriority < 0.95) {
            issues.append("Priorities should sum to ~1.0\n");
        }

        if (currentConfig.maxLatencyMs < 10) {
            issues.append("Max latency should be >= 10 ms\n");
        }

        if (currentConfig.maxCostPerHour < 0.1) {
            issues.append("Max cost should be >= 0.1 $/hr\n");
        }

        if (currentConfig.minAvailabilityPercent < 50) {
            issues.append("Min availability should be >= 50%\n");
        }

        if (currentConfig.numHosts < 1 || currentConfig.numVMs < 1) {
            issues.append("Must have at least 1 host and 1 VM\n");
        }

        if (issues.length() == 0) {
            statusLabel.setText("✓ Configuration is valid");
            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            showAlert("Validation Passed", "Configuration is valid and ready for simulation");
        } else {
            statusLabel.setText("✗ Validation failed");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            showAlert("Validation Issues", issues.toString());
        }
    }

    /**
     * Reset to default configuration
     */
    private void resetConfiguration() {
        currentConfig = new CloudConfig();
        jsonEditor.setText(formatJson(currentConfig.toJson()));
        updateFormFromConfig();
        statusLabel.setText("Reset to defaults");
        statusLabel.setStyle("-fx-text-fill: #3498db;");
    }

    /**
     * Update form fields from config object
     */
    private void updateFormFromConfig() {
        userIdField.setText(currentConfig.userId);
        costPrioritySpinner.getValueFactory().setValue(currentConfig.costPriority);
        latencyPrioritySpinner.getValueFactory().setValue(currentConfig.latencyPriority);
        securityPrioritySpinner.getValueFactory().setValue(currentConfig.securityPriority);
        carbonPrioritySpinner.getValueFactory().setValue(currentConfig.carbonPriority);
        maxLatencySpinner.getValueFactory().setValue(currentConfig.maxLatencyMs);
        maxCostSpinner.getValueFactory().setValue(currentConfig.maxCostPerHour);
        minAvailabilitySpinner.getValueFactory().setValue(currentConfig.minAvailabilityPercent);
        securityLevelCombo.setValue(currentConfig.securityLevel);
        numHostsSpinner.getValueFactory().setValue(currentConfig.numHosts);
        numVMsSpinner.getValueFactory().setValue(currentConfig.numVMs);
        numCloudletsSpinner.getValueFactory().setValue(currentConfig.numCloudlets);
        placementPolicyCombo.setValue(currentConfig.vmPlacementPolicy);
        greenDatacenterCheck.setSelected(currentConfig.greenDatacenter);
    }

    /**
     * Update config object from form fields
     */
    private void updateConfigFromForm() {
        currentConfig.userId = userIdField.getText();
        currentConfig.costPriority = costPrioritySpinner.getValue();
        currentConfig.latencyPriority = latencyPrioritySpinner.getValue();
        currentConfig.securityPriority = securityPrioritySpinner.getValue();
        currentConfig.carbonPriority = carbonPrioritySpinner.getValue();
        currentConfig.maxLatencyMs = maxLatencySpinner.getValue();
        currentConfig.maxCostPerHour = maxCostSpinner.getValue();
        currentConfig.minAvailabilityPercent = minAvailabilitySpinner.getValue();
        currentConfig.securityLevel = securityLevelCombo.getValue();
        currentConfig.numHosts = numHostsSpinner.getValue();
        currentConfig.numVMs = numVMsSpinner.getValue();
        currentConfig.numCloudlets = numCloudletsSpinner.getValue();
        currentConfig.vmPlacementPolicy = placementPolicyCombo.getValue();
        currentConfig.greenDatacenter = greenDatacenterCheck.isSelected();
    }

    /**
     * Format JSON for display
     */
    private String formatJson(String json) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Object o = gson.fromJson(json, Object.class);
            return gson.toJson(o);
        } catch (Exception e) {
            return json;
        }
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Get current configuration
     */
    public CloudConfig getCurrentConfig() {
        updateConfigFromForm();
        return currentConfig;
    }

    /**
     * Set configuration
     */
    public void setCurrentConfig(CloudConfig config) {
        this.currentConfig = config;
        jsonEditor.setText(formatJson(config.toJson()));
        updateFormFromConfig();
    }
}
