package com.smartcampus.api.config;

import com.smartcampus.api.filter.ApiLoggingFilter;
import com.smartcampus.api.mapper.GlobalExceptionMapper;
import com.smartcampus.api.mapper.LinkedResourceNotFoundExceptionMapper;
import com.smartcampus.api.mapper.RoomNotEmptyExceptionMapper;
import com.smartcampus.api.mapper.SensorUnavailableExceptionMapper;
import com.smartcampus.api.mapper.WebApplicationExceptionMapper;
import com.smartcampus.api.resource.DiscoveryResource;
import com.smartcampus.api.resource.SensorResource;
import com.smartcampus.api.resource.SensorRoomResource;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(SmartCampusApplication.class.getName());

    public SmartCampusApplication() {
        LOGGER.info("SmartCampusApplication initialized at /api/v1");
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(DiscoveryResource.class);
        classes.add(SensorRoomResource.class);
        classes.add(SensorResource.class);
        classes.add(ApiLoggingFilter.class);
        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(WebApplicationExceptionMapper.class);
        classes.add(GlobalExceptionMapper.class);
        LOGGER.info("Registered JAX-RS classes: " + classes.size());
        return classes;
    }
}
