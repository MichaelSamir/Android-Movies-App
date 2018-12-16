package com.example.android.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements MovieListener{
    boolean twoPaneUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        FrameLayout detailPane = (FrameLayout) findViewById(R.id.detail_pane);
        if ( detailPane == null) {
            twoPaneUI = false;
        } else {
            twoPaneUI = true;
        }

            MainActivityFragment mainActivityFragment = new MainActivityFragment();
            mainActivityFragment.setMovieListener(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.grid_pane, mainActivityFragment).commit();

    }
    @Override
    public void setMovieSelectedFromGridView(Movie movie) {
        //Case Two Pane UI

        if (twoPaneUI) {
            DetailActivityFragment detailActivityFragment = new DetailActivityFragment();
            Bundle bundle = new Bundle();
            bundle.putString("POSTER_PATH", movie.getPoster_path());
            bundle.putString("ORIGINAL_TITLE", movie.getOriginal_title());
            bundle.putString("RELEASE_DATE", movie.getRelease_date());
            bundle.putString("OVERVIEW", movie.getOverview());
            bundle.putDouble("VOTE_AVERAGE", movie.getVote_average());
            bundle.putInt("ID", movie.getMovie_id());
            detailActivityFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_pane, detailActivityFragment).commit();
        } else {
                //Case Single Pane UI
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("POSTER_PATH", movie.getPoster_path());
                intent.putExtra("POSTER_PATH", movie.getPoster_path());
                intent.putExtra("ORIGINAL_TITLE", movie.getOriginal_title());
                intent.putExtra("OVERVIEW", movie.getOverview());
                intent.putExtra("RELEASE_DATE", movie.getRelease_date());
                intent.putExtra("VOTE_AVERAGE", movie.getVote_average());
                intent.putExtra("ID", movie.getMovie_id());
                startActivity(intent);
            }

        }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}