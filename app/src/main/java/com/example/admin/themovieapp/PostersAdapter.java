package com.example.admin.themovieapp;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostersAdapter  extends RecyclerView.Adapter<PostersAdapter.PosterViewHolder>
{

    private List<Movie> mMovieList;
    private Context mContext;
    public static final String IMAGE_URL_BASE_PATH = " http://image.tmdb.org/t/p/w342";
    private OnItemClickListener cLickListener;


    public PostersAdapter(Context context)
    {
        this.mContext = context;
        this.mMovieList = new ArrayList<>();

    }
    public void setClickListener(OnItemClickListener itemClickListener) {
        this.cLickListener = itemClickListener;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(R.layout.activity_images,parent,shouldAttachToParentImmediately);
        PosterViewHolder posterViewHolder = new PosterViewHolder(view);
        return posterViewHolder;
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position)
    {

        Movie movie = mMovieList.get(position);
        String image_url = IMAGE_URL_BASE_PATH + movie.getPosterPath();
        String toLoad = image_url.trim();
        Log.d("The url",image_url);
        // This is how we use Picasso to load images from the internet.
//        Picasso.with(mContext)
//                .load(image_url)
//                .into(holder.posterImageView, new com.squareup.picasso.Callback()
//                {
//                    @Override
//                    public void onSuccess()
//                    {
//                        Toast.makeText(mContext, " Huge success dear one", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onError()
//                    {
//                        Toast.makeText(mContext,"Fail",Toast.LENGTH_LONG).show();
//                    }
//                });


        Picasso.Builder builder = new Picasso.Builder(mContext);
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                exception.printStackTrace();
                Log.d("Picasso Error","The error " + exception);
            }
        });
        builder.build().load(toLoad).fit().into(holder.posterImageView);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public void setMovieList(List<Movie> movieList)
    {
        this.mMovieList.clear();
        this.mMovieList.addAll(movieList);
        // The adapter needs to know that the data has changed. If we don't call this, app will crash.
        notifyDataSetChanged();
    }

    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView posterImageView;

        public PosterViewHolder(View itemView)
        {
            super(itemView);
            posterImageView = (ImageView) itemView.findViewById(R.id.posters);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            cLickListener.onClick(v, getAdapterPosition());
        }
    }
}
