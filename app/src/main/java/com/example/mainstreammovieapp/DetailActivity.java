package com.example.mainstreammovieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mainstreammovieapp.DB.FavoriteDataBase;
import com.example.mainstreammovieapp.utilities.Movie;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.material.snackbar.Snackbar;

public class DetailActivity extends AppCompatActivity {
    private TextView nameOfMovie, plotSynopsis;
    private ImageView imageView;
    private ToggleButton toggleButton;

    private Movie movie = new Movie();
    private String thumbnail, movieName, synopsis;
    private Double rating;
    private int movie_id;
    private FavoriteDataBase favoriteDataBase = new FavoriteDataBase(this);
    private boolean myBool =false;


   @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_card);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.iv_poster_MC);
        nameOfMovie = findViewById(R.id.tv_movie_name);
        plotSynopsis = findViewById(R.id.tv_synopsis);
        Intent intent = getIntent();
        if(intent.hasExtra("movies")) {

            movie = getIntent().getParcelableExtra("movies");
            thumbnail = movie.getPosterPath();
            movieName = movie.getOriginalTitle();
            myBool = favoriteDataBase.checkFav(movie.idToString(movie.getId()));
            synopsis = movie.getOverView();
            rating = movie.getVoteAverage();
            movie_id = movie.getId();

           String poster = BuildConfig.BASE_IMAGE_URL + thumbnail;

            Glide.with(this).load(poster)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher))
                    .into(imageView);
            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
        }else {
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();
        }

        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

       MaterialFavoriteButton materialFavoriteButton =
               (MaterialFavoriteButton) findViewById(R.id.add_to_favorite);

       if(materialFavoriteButton.isFavorite() == myBool){
           materialFavoriteButton.setAnimateFavorite(true);
       }else {
           materialFavoriteButton.setAnimateFavorite(true);
       }


       materialFavoriteButton.setOnFavoriteChangeListener(
               new MaterialFavoriteButton.OnFavoriteChangeListener() {
                   @Override
                   public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                       SharedPreferences.Editor editor = preferences.edit();
                       if (favorite){
                           editor.putBoolean("Favorite Added", true);
                           editor.apply();
                           saveFavorite();
                           Snackbar.make(buttonView, "Added to Favorite",
                                   Snackbar.LENGTH_SHORT).show();
                       }else {
                           movie_id = getIntent().getExtras().getInt("id");
                           favoriteDataBase = new FavoriteDataBase(DetailActivity.this);
                           favoriteDataBase.deleteFavorite(movie_id);

                           editor.putBoolean("Favorite Added", false);
                           editor.commit();
                           Snackbar.make(buttonView, "Removed from Favorite",
                                   Snackbar.LENGTH_SHORT).show();
                       }

                   }
               });
       Intent intentButtonState = new Intent(this, MainActivity.class);
       intentButtonState.putExtra("ButtonState",myBool);
    }

    public void saveFavorite(){
       favoriteDataBase = new FavoriteDataBase(this);
       thumbnail = movie.getPosterPath();

       movie.setVoteAverage(rating);
       movie.setId(movie_id);
       movie.setOverView(synopsis);
       movie.setPosterPath(thumbnail);
       movie.setOriginalTitle(movieName);

       favoriteDataBase.addFavorite(movie);
    }

}
