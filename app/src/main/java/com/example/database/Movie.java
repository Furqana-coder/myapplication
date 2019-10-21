package com.example.database;

public class Movie {

    public String title;
    public String rating;
    public String year;

    public Movie() {

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Movie(String title, String rating, String year) {
        this.title = title;
        this.rating = rating;
        this.year = year;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getRating() {
        return rating;
    }

    public String getYear() {
        return year;
    }
}