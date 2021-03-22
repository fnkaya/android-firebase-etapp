package com.gazitf.etapp.main.model;

/*
 * @created 22/03/2021 - 5:57 PM
 * @project EtApp
 * @author fnkaya
 */
public class Category {

    private String name;
    private String description;
    private String imageUrl;

    public Category(String name, String description, String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
