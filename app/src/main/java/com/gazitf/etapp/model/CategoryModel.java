package com.gazitf.etapp.model;

import com.google.firebase.firestore.DocumentId;

/*
 * @created 22/03/2021 - 5:57 PM
 * @project EtApp
 * @author fnkaya
 */
public class CategoryModel {

    @DocumentId
    private String id;
    private String name;
    private String description;
    private String imageUrl;

    public CategoryModel() {}

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
