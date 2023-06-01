package com.example.myapplication;

public class Book {
    private String name;
    private String img;
    private int id;
    public Book(int id,String name,String img){
        this.id=id;
        this.name=name;
        this.img=img;
    }
//    public Book(String name,String img){
//        this.name=name;
//        this.img=img;
//    }
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getImg() {
        return img;
    }
}
