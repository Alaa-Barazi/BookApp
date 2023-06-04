package com.example.myapplication;

public class Book {
    private String name;
    private String img;
    private int id;
    private int price;
    public Book(int id,String name,String img,int price){
        this.id=id;
        this.name=name;
        this.img=img;
        this.price=price;
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

    public int getPrice() {
        return price;
    }
}
