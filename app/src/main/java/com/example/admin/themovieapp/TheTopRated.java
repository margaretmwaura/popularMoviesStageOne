package com.example.admin.themovieapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Build.VERSION_CODES.O;
import static com.example.admin.themovieapp.PostersAdapter.emptying;

public class TheTopRated extends Fragment implements OnItemClickListener,OnLongClickListener
{
    public static final String ARG_PAGE = "ARG_PAGE";
    //    This list is the result gotten from the search
    private List<Movie> movies = new ArrayList<>();
    private List<Movie> favouriteMovies = new ArrayList<>();
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
    private Movie favourite;
    private TextView notification,errorMessage;
    private Circle circle;
    private int duration = 2000;
    private Button button;
    private MediaPlayer ring;
    private SparseBooleanArray positions = new SparseBooleanArray();
    private CircleAngleAnimation animation;
    private ValueAnimator anim;
    private Animation myanim;
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
        circle = (Circle) rootView.findViewById(R.id.error);
        button = (Button) rootView.findViewById(R.id.retry);
        button.setVisibility(View.INVISIBLE);
        errorMessage = rootView.findViewById(R.id.errorMessage);
        errorMessage.setVisibility(View.INVISIBLE);

//        This method has been called first so that the movie list can be populated
        gettingTheTopRated();



//        Wiring up the recyclerView
        gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        postersAdapter = new PostersAdapter(getActivity().getApplicationContext());
        recyclerView.setAdapter(postersAdapter);

        postersAdapter.setLongCLickListener(this);
        postersAdapter.setClickListener(this);



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

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettingTheTopRated();
            }
        });



//        The animation instantiation
        anim = ValueAnimator.ofFloat(30.0f,10.0f);
        myanim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate);

        ring = MediaPlayer.create(getActivity().getApplicationContext(), R.raw.ring);
        return rootView;
    }

    public void gettingTheTopRated()
    {
        Log.d("Fetching movies " , "Currently fetching movies ");
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

                recyclerView.setVisibility(View.VISIBLE);
                movies = response.body().getResults();
                postersAdapter.setMovieList(movies);
                Log.d("Sucess","This was a success");
                button.setVisibility(View.INVISIBLE);
                errorMessage.setVisibility(View.INVISIBLE);
                circle.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<AllMovie> call, Throwable t)
            {
                recyclerView.setVisibility(View.INVISIBLE);
                Log.d(TAG, " This was a huge fail" + t.getMessage());

                circle.setVisibility(View.VISIBLE);
                 animation = new CircleAngleAnimation(circle, 320);
                animation.setDuration(duration);
                Animation.AnimationListener animationListener = new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        duration = 1;
                         if(anim!=null)
                         {
                                     anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                 @Override
                                 public void onAnimationUpdate(ValueAnimator animation) {
                                     circle.getPaint().setStrokeWidth((Float) anim.getAnimatedValue());
//                                     Log.d("Anim animation","Method has been called ");
                                     circle.invalidate();
                                     circle.requestLayout();
                                 }
                             });

                             anim.addListener(new AnimatorListenerAdapter() {
                                 @Override
                                 public void onAnimationEnd(Animator animation) {
                                     super.onAnimationEnd(animation);

                                     if(myanim!=null)
                                     {
                                         circle.startAnimation(myanim);
                                         Log.d("Myanim animation " , "Method has been called The Top Rated");
                                         button.setVisibility(View.VISIBLE);
                                         errorMessage.setVisibility(View.VISIBLE);

                                     }

//                                Code for adding the error sound
                                     if(ring!=null)
                                     {

                                         ring.start();
                                         Log.d("Media Player animation " , "Method has been called The Top Rated ");
                                     }
                                 }
                             });
                             anim.setDuration(1000);
                             anim.start();
                         }

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {

                    }
                };



                animation.setAnimationListener(animationListener);
                circle.startAnimation(animation);


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


//       This piece of code is for finding the textView within the favourites icon layout
//        final View actionView = menu.findItem(R.id.favorite_movies).getActionView();
//        notification = (TextView) actionView.findViewById(R.id.notification_message);



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



        MenuItem menuItem = menu.findItem(R.id.favorite_movies);
        View actionView = menuItem.getActionView();
        notification = (TextView) actionView.findViewById(R.id.notification_message);

        displaySelectedCount();

        actionView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addToFavourites();
                notification.setText(" ");
                notification.setVisibility(View.INVISIBLE);
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
        favouriteMovies.clear();
        positions.clear();
        emptying();
        postersAdapter.notifyDataSetChanged();
        Log.d("onprepareOptionsMenu ","Method has been called ");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.refresh)
        {
            gettingTheTopRated();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLongClick(View view, int position)
    {
        Log.d("LongClickMethod","The method has beeen called");
        Log.d("LongClickMethodPosition","This is the position of the clicked item " + position);
//Will populate the array at this point that will be added to the favourites section

        favourite = movies.get(position);
        if(!positions.get(position,false))
        {
            favouriteMovies.add(favourite);
            positions.put(position,true);
        }
        else
        {
           favouriteMovies.remove(favourite);
            positions.put(position,false);
//            This is where one will keep track of what is to be removed from the notification bar
        }

        displaySelectedCount();


    }

    public void displaySelectedCount()
    {
        //  this is where one will keep track of the count to be shown in the notification bar by adding

        if (notification != null)
        {


            if (favouriteMovies.size() == 0)
            {
                    notification.setVisibility(View.GONE);

            } else
                {
                    int count = favouriteMovies.size();
                    notification.setVisibility(View.VISIBLE);
                    notification.setText(String.valueOf(count));
                }
            }
        }

     public void addToFavourites()
     {
         if(favouriteMovies.size() == 0)
         {
             Toast.makeText(getActivity().getApplicationContext(),"No movies have been selected to add to favourites",Toast.LENGTH_LONG).show();
         }
         else
             {
             Thread thread = new Thread(new Runnable() {
                 @Override
                 public void run()
                 {
                     mDb.movieDao().insertMovies(favouriteMovies);
                     favouriteMovies.clear();
                 }
             });
             thread.start();
             Toast.makeText(getActivity().getApplicationContext(), "Movies have been added to favourites ", Toast.LENGTH_LONG).show();
         }

         emptying();
         positions.clear();
         postersAdapter.notifyDataSetChanged();

     }

     public void clearingAnimations()
     {
       Log.d("PageUnselected","The page The Top Rated has been unselected so the animation will stop");
//       Toast.makeText(getActivity().getApplicationContext(),"The page has been unselected",Toast.LENGTH_LONG).show();
         if(anim!=null)
         {
             if (anim.isRunning())
             {
                 circle.clearAnimation();
                 Log.d("Animation cleared","The animation to thinening the stroke width has been called ");
             }
             anim = null;
             Log.d("Animation data cleared","The animation to thinening the stroke width has been called ");
         }
         if(anim == null)
         {
             anim = null;
             Log.d("Animation thickening","Data is already equal to null ");
         }
          if(myanim!=null)
         {
             if(myanim.hasStarted())
             {
                 circle.clearAnimation();
                 Log.d("Animation cleared","The animation to rotate the circle has been called");
             }
             myanim = null;
             Log.d("Animation data cleared","The animation to thinening the stroke width has been called ");
         }
         if(myanim == null)
          {
              myanim = null;
              Log.d("Animation rotation","Data is already equal to null ");
          }
         if(ring!=null)
         {
             ring.release();
             ring=null;
             Log.d("Clearing MediaPlayer", "Media player has been cleared ");
         }
         if(ring == null)
         {
             ring=null;
             Log.d("Clearing MediaPlayer", "Media player is already equal to null ");
         }


     }

}


