package com.example.android.moviesapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */

public class MainActivityFragment extends Fragment {
    MovieListener movieListener;
    private ImageAdapter imageAdapter;
    GridView gridView;
    String sortType;
    FavouritesDbHelper favouritesHelper;


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        favouritesHelper = new FavouritesDbHelper(getActivity());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortType = prefs.getString("sort",
                "popular");
        if (sortType.equals("favourites")) {
            ShowFavouriteMovies showFavouriteMovies=new ShowFavouriteMovies();
            showFavouriteMovies.execute();

        }
        else {
            FetchMoviesTask fetchmoviestask = new FetchMoviesTask();
            fetchmoviestask.execute(sortType);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        gridView = (GridView) rootView.findViewById(R.id.movies_grid);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                                                    long l) {
                                                Movie selected_movie = (Movie) imageAdapter.getItem(position);
                                                movieListener.setMovieSelectedFromGridView(selected_movie);

                                            }
                                        }

        );

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private Movie[] getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String ORIGINAL_TITLE = "original_title";
            final String VOTE_AVERAGE = "vote_average";
            final String RESULTS = "results";
            final String ID = "id";


            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
            Movie[] movies = new Movie[moviesArray.length()];
            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movie = moviesArray.getJSONObject(i);
                String original_title = movie.getString(ORIGINAL_TITLE);
                Movie movieObj = new Movie();
                movieObj.setOriginal_title(original_title);

                String poster_path = movie.getString(POSTER_PATH);
                movieObj.setPoster_path(poster_path);

                String overview = movie.getString(OVERVIEW);
                movieObj.setOverview(overview);

                double vote_average = movie.getDouble(VOTE_AVERAGE);
                movieObj.setVote_average(vote_average);

                String release_date = movie.getString(RELEASE_DATE);
                movieObj.setRelease_date(release_date);
                int id = movie.getInt(ID);
                movieObj.setMovie_id(id);
                movies[i] = movieObj;
            }
            return movies;

        }

        @Override
        protected Movie[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String moviesJsonStr = null;
            try {

                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_KEY_PARAM = "api_key";
                Log.v(LOG_TAG, "PARAMS[0]= " + params[0]);
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon().appendPath(params[0])
                        .appendQueryParameter(API_KEY_PARAM, "e4fc59602c24b0c3d55e7bd41261c733")
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    moviesJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    moviesJsonStr = null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movies JSON String: " + moviesJsonStr);

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                moviesJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
                if(moviesJsonStr==null){
                    return null;
                }

            }
            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Movie[] result) {
            if (imageAdapter == null) {
                if(result!=null){
                    imageAdapter =
                            new ImageAdapter(
                                    getActivity(),

                                    R.layout.movie_image_view
                                    , result);

                    gridView.setAdapter(imageAdapter);
                  //  Movie defaultSelectedMovie = result[0];
                  //  movieListener.setMovieSelectedFromGridView(defaultSelectedMovie);

                }
                else{
                    AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
                    alert.setTitle("Alert");
                    alert.setMessage("Please Check Your Internet Connection");
                    alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int w) {
                                    dialogInterface.dismiss();
                                }
                            });
                    alert.show();
                   // Toast.makeText(getActivity(),"Please Check Your Internet Connection",Toast.LENGTH_LONG);
                }
            } else {
                if (result != null) {
                    //   imageAdapter.setMovies(result);
                    //    imageAdapter.notifyDataSetChanged();
                    imageAdapter = null;

                    // imageAdapter.clear();
                    //   for(Movie mx:result){

                    // imageAdapter.add(mx);}
                    imageAdapter =
                            new ImageAdapter(
                                    getActivity(),

                                    R.layout.movie_image_view
                                    , result);
                    gridView.setAdapter(imageAdapter);
                   // Movie defaultSelectedMovie = result[0];
                   // movieListener.setMovieSelectedFromGridView(defaultSelectedMovie,true);
                    if(sortType.equals("popular")){
                        Toast.makeText(getActivity(),"Popular Movies",Toast.LENGTH_SHORT).show();
                    }
                    else if(sortType.equals("top_rated")){
                        Toast.makeText(getActivity(),"Top Rated Movies",Toast.LENGTH_SHORT).show();

                    }


                }
                else{
                    AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
                    alert.setTitle("Alert");
                    alert.setMessage("Please Check Your Internet Connection");
                    alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int w) {
                                    dialogInterface.dismiss();
                                }
                            });
                    alert.show();
                    //Toast.makeText(getActivity(),"Please Check Your Internet Connection",Toast.LENGTH_SHORT);
                }


            }
        }


    }
    public class ShowFavouriteMovies extends AsyncTask<Void, Void, Movie[]>{
        Movie[] favouriteMoviesArray;
        protected Movie[] doInBackground(Void... params) {

            SQLiteDatabase favouritesDB = favouritesHelper.getReadableDatabase();
            String GET_FAVOURITE_MOVIES_QUERY = "SELECT * FROM favourite_movies";
            Cursor cursor = null;
            String[] colomns = {"movie_id"};
            try {
                cursor = favouritesDB.rawQuery(GET_FAVOURITE_MOVIES_QUERY, null);
                 favouriteMoviesArray = new Movie[cursor.getCount()];
                int i = 0;
                if (cursor.moveToFirst()) {
                    do {
                        Movie movieObject = new Movie();
                        movieObject.setMovie_id(cursor.getInt(cursor.getColumnIndex("movie_id")));
                        movieObject.setPoster_path(cursor.getString(cursor.getColumnIndex("poster_path")));
                        movieObject.setOriginal_title(cursor.getString(cursor.getColumnIndex("original_title")));
                        movieObject.setOverview(cursor.getString(cursor.getColumnIndex("overview")));
                        movieObject.setRelease_date(cursor.getString(cursor.getColumnIndex("release_date")));
                        movieObject.setVote_average(cursor.getDouble(cursor.getColumnIndex("vote_average")));
                        favouriteMoviesArray[i] = movieObject;
                        i++;
                    } while (cursor.moveToNext());
                    //Movie defaultSelectedMovie = favouriteMoviesArray[0];
                    //movieListener.setMovieSelectedFromGridView(defaultSelectedMovie,true);
                }
            } finally {
                cursor.close();
                favouritesDB.close();

            }
            return favouriteMoviesArray;
        }



//            for (int i = 0; i < cursor.getCount(); i++) {
//                favouriteMoviesArray[i].setMovie_id(cursor.getInt(cursor.getColumnIndex("movie_id")));
//                favouriteMoviesArray[i].setOriginal_title(cursor.getString(i + 1));
//                favouriteMoviesArray[i].setPoster_path(cursor.getString(i + 2));
//                favouriteMoviesArray[i].setOverview(cursor.getString(i + 3));
//                favouriteMoviesArray[i].setVote_average(cursor.getDouble(i + 4));
//                favouriteMoviesArray[i].setRelease_date(cursor.getString(i + 5));
//                cursor.moveToNext();
//            }
       protected void onPostExecute(Movie[] favouriteMoviesArray){
           Toast.makeText(getActivity(), "Favourite Movies", Toast.LENGTH_SHORT).show();
           if (imageAdapter == null) {
               imageAdapter =
                       new ImageAdapter(
                               getActivity(),

                               R.layout.movie_image_view
                               , favouriteMoviesArray);
               gridView.setAdapter(imageAdapter);


           } else {
               if (favouriteMoviesArray != null) {
                   //   imageAdapter.setMovies(result);
                   //    imageAdapter.notifyDataSetChanged();
                   imageAdapter = null;

                   // imageAdapter.clear();
                   //   for(Movie mx:result){

                   // imageAdapter.add(mx);}
                   imageAdapter =
                           new ImageAdapter(
                                   getActivity(),

                                   R.layout.movie_image_view
                                   , favouriteMoviesArray);
                   gridView.setAdapter(imageAdapter);


               }
           }

       }



    }

    public void setMovieListener(MovieListener movieListener) {
        Log.v("in set movie listener ", "setMovieListener ");

        this.movieListener=movieListener;
    }
}
