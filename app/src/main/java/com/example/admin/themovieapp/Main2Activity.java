package com.example.admin.themovieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.admin.themovieapp.TheFavourites.returnFavouriteMovies;

public class Main2Activity extends AppCompatActivity
{

    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    private ImageView mine;
    private TextView title,synopsis,voteAverage,releaseDate;
    private RecyclerView recyclerView,recyclerView1;
    private int id;
    private Movie movie;
    private Boolean savedState;
    public static final String IMAGE_URL_BASE_PATH = " http://image.tmdb.org/t/p/w154";
    private List<Movie> favouriteMovies = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

//This gets us the passed object
        movie = (Movie) getIntent().getParcelableExtra("parcel_data");

//        End of it


//        Details of the searched movie

//        This is for the poster path
       String url = IMAGE_URL_BASE_PATH + movie.getPosterPath();
        String image = url.trim();



//        This is for the trailer path

//       trailerUrl = BASE_URL + id + SECOND_BASE_URL + BuildConfig.THE_API_KEY;
//       uri = trailerUrl.trim();

        id = movie.getId();
        String titleText = movie.getTitle();
       String sysnopsisTetx = movie.getOverview();
       Double voteAverageDouble = movie.getVoteAverage();
       String releaseDateTetx = movie.getReleaseDate();

//        Log.d("The video url",uri);
//       Changing the toolbar text

        getSupportActionBar().setTitle(titleText);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        mine = (ImageView) findViewById(R.id.imageView);
        title = (TextView) findViewById(R.id.textView6);
        synopsis = (TextView) findViewById(R.id.textView2);
        voteAverage = (TextView) findViewById(R.id.textView5);
        releaseDate = (TextView) findViewById(R.id.textView8);


        title.setText(titleText);
        synopsis.setText(sysnopsisTetx);
        releaseDate.setText(releaseDateTetx);
        voteAverage.setText(String.valueOf(voteAverageDouble));

        Picasso.get()
                .load(image)
                .fit()
                .into(mine);


//        This is the code for starting the trailer in you tube and reviews
//        Initializing the recyclerView This is for the trailers
        //        This is important so that our recyclerView can be horizontally scrolling
        recyclerView = findViewById(R.id.recylerview2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);


//        Recycler View for the trailers
        recyclerView1 = findViewById(R.id.recylerview3);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView1.setLayoutManager(linearLayoutManager1);


//        Maintaining the state of the checkbox
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        savedState = preferences.getBoolean("State",false);
        int movieId = preferences.getInt("MovieID",0);


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }




}
