package com.smartcampus.api.resource;

import com.smartcampus.api.model.DiscoveryResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {
    private static final Logger LOGGER = Logger.getLogger(DiscoveryResource.class.getName());

    @GET
    public DiscoveryResponse getApiDiscovery() {
        LOGGER.info("Serving API discovery endpoint");
        Map<String, String> resources = new LinkedHashMap<String, String>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        resources.put("sensorReadings", "/api/v1/sensors/{sensorId}/readings");
        return new DiscoveryResponse("v1", "smartcampus-admin@westminster.ac.uk", resources);
    }
}
