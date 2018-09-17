package com.example.admin.themovieapp;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Main2Activity extends AppCompatActivity {

    private ImageView mine;
    private TextView title,synopsis,voteAverage,releaseDate;
    public static final String IMAGE_URL_BASE_PATH = " http://image.tmdb.org/t/p/w154";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

//Setting the back button for the toolbar


        Movie movie = (Movie) getIntent().getParcelableExtra("parcel_data");
       String url = IMAGE_URL_BASE_PATH + movie.getPosterPath();
       String titleText = movie.getTitle();
       String sysnopsisTetx = movie.getOverview();
       Double voteAverageDouble = movie.getVoteAverage();
       String releaseDateTetx = movie.getReleaseDate();
       String image = url.trim();

//       Changing the toolbar text

        getSupportActionBar().setTitle(titleText);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        mine = (ImageView) findViewById(R.id.imageView);
        title = (TextView) findViewById(R.id.textView4);
        synopsis = (TextView) findViewById(R.id.textView6);
        voteAverage = (TextView) findViewById(R.id.textView7);
        releaseDate = (TextView) findViewById(R.id.textView9);

        title.setText(titleText);
        synopsis.setText(sysnopsisTetx);
        releaseDate.setText(releaseDateTetx);
        voteAverage.setText(String.valueOf(voteAverageDouble));

        Picasso.with(this)
                .load(image)
                .fit()
                .into(mine);


    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
