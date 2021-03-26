package com.gazitf.etapp.main.model;

import com.google.firebase.firestore.DocumentId;

/*
 * @created 22/03/2021 - 5:57 PM
 * @project EtApp
 * @author fnkaya
 */
public class Category {

    @DocumentId
    private String id;
    private String name;
    private String description;
    private String imageUrl;

    public Category() {}

    public Category(String id, String name, String description, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
