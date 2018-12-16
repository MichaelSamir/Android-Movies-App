package com.example.android.moviesapp;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Michael on 3/29/2016.
 */

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Bundle bundle = getIntent().getExtras();
        if (savedInstanceState == null) {
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            //Pass the "extras" Bundle that contains the selected "name" to the fragment
            detailActivityFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, detailActivityFragment)
                    .commit();
        }
    }


}



