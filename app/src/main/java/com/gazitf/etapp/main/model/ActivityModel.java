package com.gazitf.etapp.main.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

/*
 * @created 22/03/2021 - 5:57 PM
 * @project EtApp
 * @author fnkaya
 */
public class ActivityModel {

    @DocumentId
    private String id;
    private String name;
    private String description;
    private Timestamp startDate;
    private Timestamp endDate;
    @ServerTimestamp
    private Timestamp createdDate;
    private GeoPoint location;
    private DocumentReference categoryRef;

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

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public DocumentReference getCategoryRef() {
        return categoryRef;
    }

    public void setCategory(DocumentReference categoryRef) {
        this.categoryRef = categoryRef;
    }
}
