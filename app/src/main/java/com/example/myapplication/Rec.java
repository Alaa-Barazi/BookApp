package com.example.myapplication;

public class Rec {
    //title desc rating
    private String title;
    private String desc;
    private String rating;
    private String bookID;
    public Rec(String title,String desc,String rating,String bookID){
        this.title=title;
        this.desc=desc;
        this.rating=rating;
        this.bookID=bookID;
    }

    public String getBookID() {
        return bookID;
    }

    public String getDesc() {
        return desc;
    }

    public String getTitle() {
        return title;
    }

    public String getRating() {
        return rating;
    }
}
