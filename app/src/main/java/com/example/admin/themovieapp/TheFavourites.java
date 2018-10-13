package com.example.admin.themovieapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.admin.themovieapp.PostersAdapter.emptying;

public class TheFavourites extends Fragment implements OnItemClickListener
{

    //    This list is the result gotten from the search
    private static List<Movie> movies = new ArrayList<>();
    private List<Movie> favouriteMovies = new ArrayList<>();
    //End of the comment
    private GridLayoutManager gridLayoutManager;
    private PostersAdapter postersAdapter;
    private RecyclerView recyclerView;
    private static final String TAG = "Error";
    private static Retrofit retrofit = null;
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    //    private Bundle mListState;
    public static final String ARG_PAGE = "ARG_PAGE";

    private Movie favourite;
    private TextView notification;
    private SparseBooleanArray positions = new SparseBooleanArray();


    public TheFavourites()
    {
        // Required empty public constructor
    }
    public static TheFavourites newInstance(int page)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TheFavourites fragment = new TheFavourites();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.activity_the_favourites, container, false);





//        The reference to the recyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_the_favourites);



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
    //    This needs to be wrapped in a background thread


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
//    Creating the menu icon
   @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       inflater.inflate(R.menu.favourites, menu);
       super.onCreateOptionsMenu(menu, inflater);




   }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        final MenuItem search_item = menu.findItem(R.id.search);

//        final SearchView searchView = (SearchView) search_item.getActionView();

//        searchView.setIconified(true);
//        searchView.setIconified(true);

        search_item.collapseActionView();
        favouriteMovies.clear();
        positions.clear();
        emptying();
        postersAdapter.notifyDataSetChanged();
        Log.d("onprepareOptionsMenu ","Method has been called ");

    }
   public static List<Movie> returnFavouriteMovies()
   {
       return movies;
   }





}
