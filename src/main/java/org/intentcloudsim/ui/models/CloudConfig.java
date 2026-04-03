package org.intentcloudsim.ui.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Cloud Configuration Model
 * Represents user's infrastructure configuration
 */
public class CloudConfig {

    public String userId;
    public String userIntent;
    public double costPriority;
    public double latencyPriority;
    public double securityPriority;
    public double carbonPriority;
    
    // SLA Parameters
    public double maxLatencyMs;
    public double maxCostPerHour;
    public double minAvailabilityPercent;
    public String securityLevel;  // LOW, MEDIUM, HIGH
    
    // Infrastructure Configuration
    public int numHosts;
    public int hostsPerDatacenter;
    public int numVMs;
    public int vmsPerHost;
    public int numCloudlets;
    public int cloudletsPerVM;
    
    // Resource Specifications
    public int hostCores;
    public long hostRamGb;
    public long hostStorageGb;
    public int vmCores;
    public long vmRamGb;
    
    // Placement Strategy
    public String vmPlacementPolicy;  // CONSOLIDATED, SPREAD, ISOLATED
    public String datacenterLocation;  // LOCAL, REGIONAL, GLOBAL
    public boolean greenDatacenter;
    
    // Simulation Parameters
    public long simulationTimeoutSeconds;
    public int numExperiments;

    public CloudConfig() {
        // Defaults
        this.userId = "user_default";
        this.costPriority = 0.5;
        this.latencyPriority = 0.5;
        this.securityPriority = 0.3;
        this.carbonPriority = 0.2;
        
        this.maxLatencyMs = 100.0;
        this.maxCostPerHour = 5.0;
        this.minAvailabilityPercent = 99.5;
        this.securityLevel = "MEDIUM";
        
        this.numHosts = 4;
        this.hostsPerDatacenter = 4;
        this.numVMs = 4;
        this.vmsPerHost = 1;
        this.numCloudlets = 8;
        this.cloudletsPerVM = 1;
        
        this.hostCores = 8;
        this.hostRamGb = 16;
        this.hostStorageGb = 1000;
        this.vmCores = 2;
        this.vmRamGb = 4;
        
        this.vmPlacementPolicy = "SPREAD";
        this.datacenterLocation = "LOCAL";
        this.greenDatacenter = false;
        
        this.simulationTimeoutSeconds = 300;
        this.numExperiments = 1;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public static CloudConfig fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, CloudConfig.class);
    }

    @Override
    public String toString() {
        return "CloudConfig{" +
                "userId='" + userId + '\'' +
                ", costPriority=" + costPriority +
                ", latencyPriority=" + latencyPriority +
                ", securityPriority=" + securityPriority +
                ", carbonPriority=" + carbonPriority +
                ", numHosts=" + numHosts +
                ", numVMs=" + numVMs +
                '}';
    }
}
