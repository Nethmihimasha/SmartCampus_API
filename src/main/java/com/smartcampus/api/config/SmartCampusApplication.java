package com.smartcampus.api.config;

import java.util.logging.Logger;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(SmartCampusApplication.class.getName());

    public SmartCampusApplication() {
        LOGGER.info("SmartCampusApplication initialized at /api/v1");
    }
}
