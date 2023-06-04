package com.example.myapplication;

public class CartBook {
    private String name;
    private String img;
    private String id;
    private int price;
    private String username;
    private int amount;
    public CartBook(String name,String img,String id,int price,String username,int amount){
        this.name=name;
        this.img=img;
        this.id=id;
        this.price=price;
        this.username=username;
        this.amount=amount;
    }

    public int getPrice() {
        return price;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImg() {
        return img;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
