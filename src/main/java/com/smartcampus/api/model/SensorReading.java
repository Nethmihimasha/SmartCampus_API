package com.smartcampus.api.model;

import java.util.UUID;
import java.util.logging.Logger;

public class SensorReading {
    private static final Logger LOGGER = Logger.getLogger(SensorReading.class.getName());

    private String id;
    private long timestamp;
    private double value;

    public SensorReading() {
        LOGGER.fine("SensorReading instance created");
    }

    public SensorReading(long timestamp, double value) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = timestamp;
        this.value = value;
        LOGGER.fine("SensorReading created with id: " + this.id);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
}
