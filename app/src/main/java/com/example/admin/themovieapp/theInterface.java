package com.example.admin.themovieapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface theInterface
{
    @GET("movie/popular")
    Call<AllMovie> getMostPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<AllMovie> getTopRatedMovies(@Query("api_key") String apiKey);

}
