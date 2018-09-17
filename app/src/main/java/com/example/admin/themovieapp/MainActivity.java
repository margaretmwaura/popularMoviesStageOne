package com.example.admin.themovieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements OnItemClickListener{


//    This list is the result gotten from the search
    private  List<Movie> movies = new ArrayList<>();

//End of the comment
    private PostersAdapter postersAdapter;
    private RecyclerView  recyclerView;
    private static final String TAG = "Error";
    private static Retrofit retrofit = null;
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        The reference to the recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

//        This method has been called first so that the movie list can be populated
        gettingThePopularMovies();

//        Wiring up the recyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        postersAdapter = new PostersAdapter(this);
        recyclerView.setAdapter(postersAdapter);
          postersAdapter.setClickListener( this);

    }

    public void gettingThePopularMovies()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        theInterface toUse = retrofit.create(theInterface.class);
        Call<AllMovie> call = toUse.getTopRatedMovies(BuildConfig.THE_API_KEY);
        call.enqueue(new Callback<AllMovie>() {
            @Override
            public void onResponse(Call<AllMovie> call, Response<AllMovie> response)
            {

                movies = response.body().getResults();
                postersAdapter.setMovieList(movies);
                Log.d("Sucess","This was a success");
            }

            @Override
            public void onFailure(Call<AllMovie> call, Throwable t)
            {
              Log.d(TAG, " This was a huge fail" + t.getMessage());
            }
        });


}

    @Override
    public void onClick(View view, int position)
    {
       Movie mine = movies.get(position);
       String title = mine.getOriginalTitle();
       Log.d("Tile of Clicked Movie " , title);
       Intent i = new Intent(this,Main2Activity.class);
        i.putExtra("parcel_data", mine);
        startActivity(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
//            The method for sorting the movies
//            Sort the movies
//            Then set the adapter
             Collections.sort(movies);
            postersAdapter.setMovieList(movies);
        }


        return super.onOptionsItemSelected(item);
    }
}


