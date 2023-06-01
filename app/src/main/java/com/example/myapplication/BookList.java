package com.example.myapplication;

public class BookList {
    private String name;
    private String id;
    private String img;
    private String username;
    private String status;

    public BookList(String id,String name,String img,String username,String status){
        this.id=id;
        this.name=name;
        this.img=img;
        this.username=username;
        this.status=status;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
