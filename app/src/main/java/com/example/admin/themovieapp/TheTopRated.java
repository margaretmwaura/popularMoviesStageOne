package com.example.admin.themovieapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TheTopRated extends Fragment implements OnItemClickListener
{
    public static final String ARG_PAGE = "ARG_PAGE";
    //    This list is the result gotten from the search
    private List<Movie> movies = new ArrayList<>();

    //End of the comment
    private Menu menu;
    private GridLayoutManager gridLayoutManager;
    private PostersAdapter postersAdapter;
    private RecyclerView  recyclerView;
    private static final String TAG = "Error";
    private static Retrofit retrofit = null;
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    //    private Bundle mListState;
    private MovieDatabase mDb;
    public TheTopRated()
    {
        // Required empty public constructor
    }
    public static TheTopRated newInstance(int page)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TheTopRated fragment = new TheTopRated();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);

        Log.d("Method has been called","Creating the view");
        View rootView = inflater.inflate(R.layout.activity_the_top_rated, container, false);

//        getActivity().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        mDb = MovieDatabase.getInstance(getActivity().getApplicationContext());

//        The reference to the recyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_top_rated);

//        This method has been called first so that the movie list can be populated
        gettingTheTopRated();



//        Wiring up the recyclerView
        gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        postersAdapter = new PostersAdapter(getActivity().getApplicationContext());
        recyclerView.setAdapter(postersAdapter);
        postersAdapter.setClickListener( this);


        if(savedInstanceState != null) {
            Log.v("---------------->", "restored!");
//            int scrollPosition = savedInstanceState.getInt("BUNDLE_RECYCLER_LAYOUT");
//            String log = String.valueOf(scrollPosition);
//            recyclerView.scrollToPosition(scrollPosition);
//            Log.d("The gotten position " , log);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("BUNDLE_RECYCLER_LAYOUT");
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            movies = savedInstanceState.getParcelableArrayList("INSTANCE_STATE_ENTRIES");
            postersAdapter.setMovieList(movies);
        }


        return rootView;
    }

    public void gettingTheTopRated()
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
//        Running in the background
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
        Intent i = new Intent(getActivity().getApplicationContext(),Main2Activity.class);
        i.putExtra("parcel_data", mine);
        startActivity(i);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v("---------------->", "saved!");
//        int position = gridLayoutManager.findFirstVisibleItemPosition();
//        outState.putInt("BUNDLE_RECYCLER_LAYOUT", gridLayoutManager.findFirstVisibleItemPosition());
//        String log = String.valueOf(position);
//        Log.d("The position is " ,log);
        outState.putParcelable("BUNDLE_RECYCLER_LAYOUT", recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList("INSTANCE_STATE_ENTRIES", (ArrayList<? extends Parcelable>) movies);
        super.onSaveInstanceState(outState);

    }


    //    Creating the menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        this.menu = menu;
        inflater.inflate(R.menu.search, menu);
        super.onCreateOptionsMenu(menu, inflater);
        Log.d("onCreateOptionsMenu","Method called");



        final List<Movie> movieSearch = new ArrayList<>();
        MenuItem search_item = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) search_item.getActionView();
        searchView.setQueryHint("Enter a movie title");


        searchView.setQuery("", false);
//        searchView.setIconified(true);
//        searchView.setIconified(true);

        searchView.setIconifiedByDefault(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
//                Clearing the search array
                String keyword = query.toLowerCase();
                movieSearch.clear();
                for(int i=0 ; i<movies.size() ; i++)
                {
                    Log.d("Search","Search method called ");
                    Movie movie = movies.get(i);
                    String title = movie.getTitle();
                    String search = title.toLowerCase();
                    if(search.contains(keyword))
                    {
                        movieSearch.add(movie);
                        Log.d("Search","Search found one");
                    }

                }
                postersAdapter.setMovieList(movieSearch);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused)
                {
                    postersAdapter.setMovieList(movies);


                }
            }
        });

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        final MenuItem search_item = menu.findItem(R.id.action_search);

//        final SearchView searchView = (SearchView) search_item.getActionView();

//        searchView.setIconified(true);
//        searchView.setIconified(true);

        search_item.collapseActionView();
        Log.d("onprepareOptionsMenu ","Method has been called ");

    }
}
