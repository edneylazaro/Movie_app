package com.example.mainstreammovieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mainstreammovieapp.DB.FavoriteDataBase;
import com.example.mainstreammovieapp.utilities.Movie;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;

public class DetailActivity extends AppCompatActivity {
    private TextView nameOfMovie, plotSynopsis;
    private ImageView imageView;

    private Movie movie = new Movie();
    private String thumbnail, movieName, synopsis;
    private Double rating;
    private int movie_id;
    private FavoriteDataBase favoriteDataBase;

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_card);
        imageView = findViewById(R.id.iv_poster_MC);
        nameOfMovie = findViewById(R.id.tv_movie_name);
        plotSynopsis = findViewById(R.id.tv_synopsis);

        Intent intent = getIntent();
        if(intent.hasExtra("movies")) {

            movie = getIntent().getParcelableExtra("movies");
            thumbnail = movie.getPosterPath();
            movieName = movie.getOriginalTitle();
            synopsis = movie.getOverView();
            rating = movie.getVoteAverage();


            Glide.with(this).load(thumbnail)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher))
                    .into(imageView);
            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
        }else
            Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();

       MaterialFavoriteButton materialFavoriteButton =
               (MaterialFavoriteButton) findViewById(R.id.add_to_favorite);

       materialFavoriteButton.setOnFavoriteChangeListener(
               new MaterialFavoriteButton.OnFavoriteChangeListener() {
                   @Override
                   public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                       if (favorite){
                           SharedPreferences.Editor editor = getSharedPreferences("com.example.mainstreammovieapp.DetailActivity", MODE_PRIVATE).edit();
                           editor.putBoolean("Favorite Added", true);
                           editor.commit();
                           saveFavorite();
                       }else {
                           movie_id = getIntent().getExtras().getInt("id");
                           favoriteDataBase = new FavoriteDataBase(DetailActivity.this);
                           favoriteDataBase.deleteFavorite(movie_id);

                           SharedPreferences.Editor editor = getSharedPreferences("com.example.mainstreammovieapp.DetailActivity", MODE_PRIVATE).edit();
                           editor.putBoolean("Favorite Removed", true);
                           editor.commit();
                       }
                   }
               });
    }

    public void saveFavorite(){
       favoriteDataBase = new FavoriteDataBase(this);

       movie.setVoteAverage(rating);
       movie.setId(movie_id);
       movie.setOverView(synopsis);
       movie.setPosterPath(thumbnail);
       movie.setOriginalTitle(movieName);

       favoriteDataBase.addFavorite(movie);
    }
}
