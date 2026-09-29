package com.example.farmers.data.model;

public class PersonaItem {
    private final String id;
    private final String title;
    private final String icon;

    public PersonaItem(String id, String title, String icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }
}
