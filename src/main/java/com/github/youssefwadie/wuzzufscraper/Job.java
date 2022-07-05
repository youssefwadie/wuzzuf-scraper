package com.github.youssefwadie.wuzzufscraper;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Job {
    private final URI uri;
    private final String title;
    private String location;
    private Company company;

    private final Map<String, String> details;

    private final List<String> description;
    private final List<String> requirements;

    public Job(URI uri, String title) {
        this.uri = uri;
        this.title = title;
        this.details = new HashMap<>();
        this.description = new LinkedList<>();
        this.requirements = new LinkedList<>();
    }

    public URI getUri() {
        return uri;
    }

    public String getTitle() {
        return title;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void addToDetails(String key, String val) {
        details.put(key, val);
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public void addToDescription(String item) {
        description.add(item);
    }

    public List<String> getDescription() {
        return description;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public void addToRequirements(String requirement) {
        requirements.add(requirement);
    }
}
