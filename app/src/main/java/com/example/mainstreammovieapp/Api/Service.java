package com.example.mainstreammovieapp.Api;

import android.content.Intent;

import com.example.mainstreammovieapp.utilities.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {

    @GET("movie/popular")
    Call<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET()
    Call<MoviesResponse> getMovieByName(@Query("api_key") String apiKey);
}
