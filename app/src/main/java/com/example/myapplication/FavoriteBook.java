package com.example.myapplication;

public class FavoriteBook {
    private String name;
    private String img;
    private String id;
    private String username;
    public FavoriteBook(String id,String name,String img, String username){
        this.name=name;
        this.img=img;
        this.id=id;
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public String getImg() {
        return img;
    }

    public String getName() {
        return name;
    }
}
