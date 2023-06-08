package com.example.myapplication;

public class Book {
    private String name;
    private String img;
    private int id;
    private int price;
    private int total;
    public Book(int id,String name,String img,int price,int total){
        this.id=id;
        this.name=name;
        this.img=img;
        this.price=price;
        this.total=total;
    }
//    public Book(String name,String img){
//        this.name=name;
//        this.img=img;
//    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setPrice(int price) {
        this.price = price;
    }

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
