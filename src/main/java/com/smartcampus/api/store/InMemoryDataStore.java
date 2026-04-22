package com.smartcampus.api.store;

import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public final class InMemoryDataStore {
    private static final Logger LOGGER = Logger.getLogger(InMemoryDataStore.class.getName());

    private static final Map<String, Room> ROOMS = new ConcurrentHashMap<String, Room>();
    private static final Map<String, Sensor> SENSORS = new ConcurrentHashMap<String, Sensor>();
    private static final Map<String, List<SensorReading>> SENSOR_READINGS = new ConcurrentHashMap<String, List<SensorReading>>();

    private InMemoryDataStore() {
        LOGGER.fine("InMemoryDataStore constructor should not be called");
    }

    public static Map<String, Room> rooms() { return ROOMS; }
    public static Map<String, Sensor> sensors() { return SENSORS; }
    public static Map<String, List<SensorReading>> sensorReadings() { return SENSOR_READINGS; }

    public static List<SensorReading> getOrCreateReadings(String sensorId) {
        if (!SENSOR_READINGS.containsKey(sensorId)) {
            SENSOR_READINGS.put(sensorId, new ArrayList<SensorReading>());
            LOGGER.info("Created readings collection for sensor: " + sensorId);
        }
        return SENSOR_READINGS.get(sensorId);
    }
}
