package com.example.admin.themovieapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface theInterface
{
    @GET("movie/popular")
    Call<AllMovie> getTopRatedMovies(@Query("api_key") String apiKey);
}
