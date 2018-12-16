package com.example.android.moviesapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Michael on 3/25/2016.
 */
public class ImageAdapter extends ArrayAdapter{

    Context context;
    int resource;
    Movie[] objects;
    //List<Movie> objects;



    public ImageAdapter(Context context, int resource, Movie[] objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }
   /*public void setMovies(Movie[] movies) {
        this.objects= movies;
    }*/




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        if(row==null){
            LayoutInflater inflater=((Activity)context).getLayoutInflater();
            row=inflater.inflate(resource,parent,false);
        }


Log.i("TAG", objects[position].getPoster_path());
       Picasso.with(context).load("http://image.tmdb.org/t/p/w185"+objects[position].getPoster_path()).into((ImageView) row);
        return row;
    }
}
