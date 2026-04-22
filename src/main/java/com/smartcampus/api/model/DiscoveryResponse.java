package com.smartcampus.api.model;

import java.util.Map;
import java.util.logging.Logger;

public class DiscoveryResponse {
    private static final Logger LOGGER = Logger.getLogger(DiscoveryResponse.class.getName());

    private String version;
    private String contact;
    private Map<String, String> resources;

    public DiscoveryResponse() {
        LOGGER.fine("DiscoveryResponse created");
    }

    public DiscoveryResponse(String version, String contact, Map<String, String> resources) {
        this.version = version;
        this.contact = contact;
        this.resources = resources;
        LOGGER.fine("DiscoveryResponse built for version: " + version);
    }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public Map<String, String> getResources() { return resources; }
    public void setResources(Map<String, String> resources) { this.resources = resources; }
}
