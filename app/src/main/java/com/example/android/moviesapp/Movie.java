package com.example.android.moviesapp;

/**
 * Created by Michael on 3/25/2016.
 */
public class Movie {
    String original_title;
    String poster_path;
    String overview;
    double vote_average;
    String release_date;
    int movie_id;

    public void setOriginal_title(String s){
        original_title=s;
    }
    public void setPoster_path(String s){
        poster_path=s;
    }
    public void setOverview(String s){
        overview=s;
    }
    public void setVote_average(double v){
        vote_average=v;
    }
    public void setRelease_date(String s){
        release_date=s;
    }
    public void setMovie_id(int id){movie_id=id;}
    public String getOriginal_title(){
        return original_title;
    }
    public String getPoster_path(){
        return poster_path;
    }
    public String getOverview(){
        return overview;
    }
    public String getRelease_date(){
        return release_date;
    }
    public double getVote_average(){
        return vote_average;
    }
    public int getMovie_id(){return movie_id;}



}
