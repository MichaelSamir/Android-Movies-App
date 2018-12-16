package com.example.android.moviesapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Michael on 4/27/2016.
 */
public class  DetailActivityFragment extends Fragment {
    FavouritesDbHelper dbHelper;
    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private ArrayAdapter<String> trailersAdapter;
    private ArrayAdapter<String> reviewsAdapter;
    ArrayList<String> selectedMovieTrailersLinks=new ArrayList<String>();
    View rootView;
    LinearLayout reviewsView;
    LinearLayout trailersView;
    Button favouriteButton;
    int movieID;
    int favouriteButtonFlag;

    @Override
    public void onStart() {
        super.onStart();
        dbHelper=new FavouritesDbHelper(getActivity());
        FetchData fetchMovieTrailers =new FetchData(1);
        fetchMovieTrailers.execute(movieID);
        FetchData fetchMovieReviews =new FetchData(2);
        fetchMovieReviews.execute(movieID);
        Favourites favourites=new Favourites(1);
        favourites.execute();


//            if(favouriteButtonFlag==0){
//                favouriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        if (isChecked) {
//                            // The toggle is enabled
//                            //favouriteButtonFlag=1;
//                            Favourites favouritesInsert = new Favourites(2);
//                            favouritesInsert.execute();
//
//                        } else {
//                            // The toggle is disabled
//                            Favourites favouritesDelete = new Favourites(3);
//                            favouritesDelete.execute();
//                        }
//                    }
//                });
//
//            }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        //Intent intent = getActivity().getIntent();
       // if (intent != null) {
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185" +
                    getArguments().getString("POSTER_PATH"))
            .into((ImageView) rootView.findViewById(R.id.poster_image_view));
            ((TextView) rootView.findViewById(R.id.overview_text_view))
                    .setText(getArguments().getString("OVERVIEW"));
            ((TextView) rootView.findViewById(R.id.original_title_text_view))
                    .setText(getArguments().getString("ORIGINAL_TITLE"));
            ((TextView) rootView.findViewById(R.id.release_date_text_view))
                    .setText(getArguments().getString("RELEASE_DATE"));
            ((TextView) rootView.findViewById(R.id.vote_average_text_view))
                    .setText(getArguments().getDouble("VOTE_AVERAGE", 0) + "/10");
            movieID=getArguments().getInt("ID",-1);
            trailersAdapter=new ArrayAdapter<String>(getActivity(),R.layout.trailer_item,R.id.trailer_view,new ArrayList<String>());
            reviewsAdapter=new ArrayAdapter<String>(getActivity(),R.layout.review_item,R.id.review_text_view,new ArrayList<String>());
            trailersView=(LinearLayout)rootView.findViewById(R.id.trailers_view);
            reviewsView=(LinearLayout)rootView.findViewById(R.id.reviews_view);
            favouriteButton=(Button) rootView.findViewById(R.id.favourite_button);



//
//                favouriteButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//
//                });
            // listTrailers.setAdapter(trailersAdapter);
            //listReviews.setAdapter(reviewsAdapter);
//                trailersView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int position = 0;
//                        if (v.getTag() instanceof Integer) {
//                            position = (Integer) v.getTag();
//                            Uri selectedTrailer = Uri.parse(selectedMovieTrailersLinks.get(position));
//                            Intent trailerIntent=new Intent(Intent.ACTION_VIEW,selectedTrailer);
//                            startActivity(trailerIntent);
//                        }
//
//                    }
//                });

       // }

        return rootView;
    }


    public class FetchData extends AsyncTask<Integer, Void, ArrayList<String>> {
        int objType;
        private final String LOG_TAG = FetchData.class.getSimpleName();

        public FetchData(int type) {
            objType = type;
        }

        private ArrayList<String> getTrailersKeysFromJson(String movieJsonStr)
                throws JSONException {
            final String TRAILER_KEY = "key";
            final String RESULTS = "results";
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray trailersArray = movieJson.getJSONArray(RESULTS);
            ArrayList<String> trailersLinks = new ArrayList<String>();
            for (int i = 0; i < trailersArray.length(); i++) {
                JSONObject trailer = trailersArray.getJSONObject(i);
                String trailerKey = trailer.getString(TRAILER_KEY);
                trailersLinks.add("https://www.youtube.com/watch?v=" + trailerKey);
            }
            Log.v(LOG_TAG, "TRAILERS LINKS " + trailersLinks);

            return trailersLinks;

        }
        private ArrayList<String> getReviewsFromJson(String movieJsonStr)
                throws JSONException {
            final String AUTHOR = "author";
            final String CONTENT = "content";
            final String RESULTS = "results";
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray reviewsArray = movieJson.getJSONArray(RESULTS);
            ArrayList<String> reviews = new ArrayList<String>();
            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject review = reviewsArray.getJSONObject(i);
                String author = review.getString(AUTHOR);
                String content = review.getString(CONTENT);
                reviews.add(author+":\n---------"+"\n"+content);
            }
            Log.v(LOG_TAG, "REVIEWS " + reviews);

            return reviews;

        }

        @Override
        protected ArrayList<String> doInBackground(Integer... params) {
            if (objType == 1) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String trailersJsonStr = null;
                try {

                    final String TRAILERS_BASE_URL = "http://api.themoviedb.org/3/movie/";
                    final String VIDEOS_PARAM = "videos";
                    final String API_KEY_PARAM = "api_key";
                    Log.v(LOG_TAG, "MOVIE ID= " + params[0]);
                    Uri builtUri = Uri.parse(TRAILERS_BASE_URL).buildUpon()
                            .appendPath(String.valueOf(params[0]))
                            .appendPath(VIDEOS_PARAM)
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
                        trailersJsonStr = null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        trailersJsonStr = null;
                    }
                    trailersJsonStr = buffer.toString();
                    Log.v(LOG_TAG, "TRAILERS JSON String: " + trailersJsonStr);

                } catch (IOException e) {
                    Log.e("PlaceholderFragment", "Error ", e);
                    trailersJsonStr = null;
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
                    if(trailersJsonStr==null){
                        return null;
                    }

                }
                try {
                    return getTrailersKeysFromJson(trailersJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            } else if(objType == 2)  {
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String reviewsJsonStr = null;
                try {

                    final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                    final String REVIEWS_PARAM = "reviews";
                    final String API_KEY_PARAM = "api_key";
                    Log.v(LOG_TAG, "MOVIE ID= " + params[0]);
                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(String.valueOf(params[0]))
                            .appendPath(REVIEWS_PARAM)
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
                        reviewsJsonStr = null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        reviewsJsonStr = null;
                    }
                    reviewsJsonStr = buffer.toString();
                    Log.v(LOG_TAG, "REVIEWS JSON String: " + reviewsJsonStr);

                } catch (IOException e) {
                    Log.e("PlaceholderFragment", "Error ", e);
                    reviewsJsonStr = null;
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
                    if(reviewsJsonStr==null){
                        return null;
                    }

                }
                try {
                    return getReviewsFromJson(reviewsJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }
            return null;
        }

        protected void onPostExecute(ArrayList<String> result){
            if(objType==1){
                if(result!=null&&result.size()!=0){
                    trailersAdapter.clear();
                    selectedMovieTrailersLinks=result;
                    TextView trailersLabel=(TextView)rootView.findViewById(R.id.trailers_label);
                    trailersLabel.setText("Trailers:");
                    View item;
                    trailersView.removeAllViews();
                    for(int i = 1; i <= result.size(); i++){
                        trailersAdapter.add("Trailer "+i);
                        item = trailersAdapter.getView(i-1, null, null);

                        trailersView.addView(item);
                        final int position=i-1;
                        item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Uri selectedTrailer = Uri.parse(selectedMovieTrailersLinks.get(position));
                                Intent trailerIntent = new Intent(Intent.ACTION_VIEW, selectedTrailer);
                                startActivity(trailerIntent);
                            }

                        });


                    }
                }
                else if(result==null){
                    AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
                    alert.setTitle("Alert");
                    alert.setMessage("Cannot Load Trailers\nPlease Check Your Internet Connection");
                    alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int w) {
                                    dialogInterface.dismiss();
                                }
                            });
                    alert.show();
                }
            }
            else if(objType==2){
                if(result!=null&&result.size()!=0){
                    reviewsAdapter.clear();
                    TextView reviewsLabel=(TextView)rootView.findViewById(R.id.reviews_label);
                    reviewsLabel.setText("Reviews:");
                    int i=0;
                    View item;
                    reviewsView.removeAllViews();
                    for(String reviewStr:result){
                        reviewsAdapter.add(reviewStr);
                        item = reviewsAdapter.getView(i, null, null);
                        reviewsView.addView(item);
                        i++;
                    }
                }
                else if(result==null){
                    AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
                    alert.setTitle("Alert");
                    alert.setMessage("Unable To Load Reviews\nPlease Check Your Internet Connection");
                    alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int w) {
                                    dialogInterface.dismiss();
                                }
                            });
                    alert.show();
                }

            }

        }
    }
    public class Favourites extends AsyncTask<Void,Void,Integer>{
        int flag;
        public Favourites(int flag){
            this.flag=flag;
        }
        @Override
        protected Integer doInBackground(Void... params) {
            if(flag==1){

                SQLiteDatabase db=dbHelper.getReadableDatabase();

                String QUERY ="SELECT movie_id FROM favourite_movies WHERE movie_id="+movieID;
                try{
                    Cursor c=db.rawQuery(QUERY,null);
                    if(c.moveToFirst()){
                        c.close();
                        return 1;
                    }
                    else{
                        c.close();
                        return 0;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
            else if(flag==2){
                FavouritesDbHelper favouritesHelper = new FavouritesDbHelper(getActivity());
                SQLiteDatabase favouritesDB = favouritesHelper.getReadableDatabase();
                String posterPath = getArguments().getString("POSTER_PATH");
                String overview = getArguments().getString("OVERVIEW");
                String originalTitle = getArguments().getString("ORIGINAL_TITLE");
                String releaseDate = getArguments().getString("RELEASE_DATE");
                double voteAverage = getArguments().getDouble("VOTE_AVERAGE", 0);
                ContentValues contentValues = new ContentValues();
                contentValues.put("movie_id", movieID);
                contentValues.put("poster_path", posterPath);
                contentValues.put("overview", overview);
                contentValues.put("original_title", originalTitle);
                contentValues.put("release_date", releaseDate);
                contentValues.put("vote_average", voteAverage);
                try{
                    favouritesDB.insert("favourite_movies", null, contentValues);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                    favouritesDB.close();

                }
                return 1;

            }
            else if(flag==3){
                FavouritesDbHelper favouritesHelper = new FavouritesDbHelper(getActivity());
                SQLiteDatabase favouritesDB = favouritesHelper.getReadableDatabase();
                try{
                    favouritesDB.delete("favourite_movies","movie_id="+movieID,null);
                    favouritesDB.close();
                    return 0;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }
        public void onPostExecute(Integer flag){
            if(flag==1){
                favouriteButton.setBackgroundResource(R.drawable.star_enabled);
                favouriteButtonFlag=3;
//                    favouriteButton.setChecked(true);
//                    favouriteButtonFlag=1;
            }
            else if(flag==0){
                favouriteButton.setBackgroundResource(R.drawable.star_disabled);
                favouriteButtonFlag=2;
//                    favouriteButton.setChecked(false);
//                    favouriteButtonFlag=0;
            }
            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favouriteButtonFlag == 2) {
                        Favourites favouritesDelete = new Favourites(3);
                        favouritesDelete.execute();
                        Favourites favouritesInsert = new Favourites(2);
                        favouritesInsert.execute();
                        Toast.makeText(getActivity(), "Added To Favorites", Toast.LENGTH_SHORT).show();
                    } else if (favouriteButtonFlag == 3) {
                        Favourites favouritesDelete = new Favourites(3);
                        favouritesDelete.execute();
                        Toast.makeText(getActivity(),"Deleted From Favorites",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}