package com.smartcampus.api.resource;

import com.smartcampus.api.exception.LinkedResourceNotFoundException;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.store.InMemoryDataStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private static final Logger LOGGER = Logger.getLogger(SensorResource.class.getName());

    @GET
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensors = new ArrayList<Sensor>(InMemoryDataStore.sensors().values());
        if (type != null && !type.trim().isEmpty()) {
            LOGGER.info("Filtering sensors by type: " + type);
            String normalized = type.toLowerCase(Locale.ENGLISH);
            return sensors.stream()
                    .filter(s -> s.getType() != null && s.getType().toLowerCase(Locale.ENGLISH).equals(normalized))
                    .collect(Collectors.toList());
        }
        LOGGER.info("Listing all sensors");
        return sensors;
    }

    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            throw new WebApplicationException("Sensor id is required", Response.Status.BAD_REQUEST);
        }

        Map<String, Room> rooms = InMemoryDataStore.rooms();
        Room room = rooms.get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException("roomId " + sensor.getRoomId() + " does not exist");
        }

        if (sensor.getStatus() == null || sensor.getStatus().trim().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        InMemoryDataStore.sensors().put(sensor.getId(), sensor);
        room.getSensorIds().add(sensor.getId());
        LOGGER.info("Created sensor: " + sensor.getId() + " in room: " + sensor.getRoomId());

        URI location = uriInfo.getAbsolutePathBuilder().path(sensor.getId()).build();
        return Response.created(location).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    public Sensor getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = InMemoryDataStore.sensors().get(sensorId);
        if (sensor == null) {
            throw new WebApplicationException("Sensor not found", Response.Status.NOT_FOUND);
        }
        return sensor;
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        LOGGER.info("Delegating to SensorReadingResource for sensor: " + sensorId);
        return new SensorReadingResource(sensorId);
    }
}
