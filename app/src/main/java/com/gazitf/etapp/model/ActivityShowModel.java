package com.gazitf.etapp.model;

public class ActivityShowModel {

    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String start_time;
    private String finish_time;
    private String start_date;
    private String finis_date;
    private String kont;
    private String adress;



    public ActivityShowModel() {}

    public ActivityShowModel(String id, String name, String description, String imageUrl, String start_date,
                             String start_time, String finis_date, String finish_time, String kont, String adress ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.start_date = start_date;
        this.start_time = start_time;
        this.finis_date = finis_date;
        this.finish_time = finish_time;
        this.kont = kont;
        this.adress = adress;
    }
}
