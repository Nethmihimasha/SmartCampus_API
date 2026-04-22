package com.smartcampus.api.resource;

import com.smartcampus.api.exception.SensorUnavailableException;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;
import com.smartcampus.api.store.InMemoryDataStore;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
    private static final Logger LOGGER = Logger.getLogger(SensorReadingResource.class.getName());

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
        LOGGER.info("SensorReadingResource initialized for sensor: " + sensorId);
    }

    @GET
    public List<SensorReading> getSensorReadings() {
        if (!InMemoryDataStore.sensors().containsKey(sensorId)) {
            throw new WebApplicationException("Sensor not found", Response.Status.NOT_FOUND);
        }
        LOGGER.info("Fetching readings for sensor: " + sensorId);
        return InMemoryDataStore.getOrCreateReadings(sensorId);
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = InMemoryDataStore.sensors().get(sensorId);
        if (sensor == null) {
            throw new WebApplicationException("Sensor not found", Response.Status.NOT_FOUND);
        }

        if (sensor.getStatus() != null && "MAINTENANCE".equals(sensor.getStatus().toUpperCase(Locale.ENGLISH))) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is in MAINTENANCE and cannot accept readings");
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(java.util.UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() <= 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        InMemoryDataStore.getOrCreateReadings(sensorId).add(reading);
        sensor.setCurrentValue(reading.getValue());
        LOGGER.info("Added reading for sensor " + sensorId + " and updated currentValue to " + reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
