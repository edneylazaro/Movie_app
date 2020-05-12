package com.example.mainstreammovieapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mainstreammovieapp.DB.FavoriteContract;
import com.example.mainstreammovieapp.DB.FavoriteDataBase;
import com.example.mainstreammovieapp.utilities.Movie;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.android.material.snackbar.Snackbar;

public class DetailActivity extends AppCompatActivity {
    private TextView nameOfMovie, plotSynopsis;
    private ImageView imageView;

    private Movie movie = new Movie();
    private String thumbnail, movieName, synopsis;
    private Double rating;
    private int movie_id;
    private FavoriteDataBase favoriteDataBase;
    private MaterialFavoriteButton materialFavoriteButton;
    private SQLiteDatabase mDb;

   @Override
   public void onCreate(final Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.movie_card);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       final FavoriteDataBase fData = new FavoriteDataBase(this);
       mDb = fData.getWritableDatabase();

       imageView = findViewById(R.id.iv_poster_MC);
       nameOfMovie = findViewById(R.id.tv_movie_name);
       plotSynopsis = findViewById(R.id.tv_synopsis);

       Intent intent = getIntent();
       if (intent.hasExtra("movies")) {

           movie = getIntent().getParcelableExtra("movies");
           thumbnail = movie.getPosterPath();
           movieName = movie.getOriginalTitle();
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
       } else {
           Toast.makeText(this, "No API Data", Toast.LENGTH_SHORT).show();
       }

       materialFavoriteButton = (MaterialFavoriteButton) findViewById(R.id.add_to_favorite);

       if (search(movieName)) {
           materialFavoriteButton.setFavorite(true);
           materialFavoriteButton.setOnFavoriteChangeListener(
                   new MaterialFavoriteButton.OnFavoriteChangeListener() {
                       @Override
                       public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                           if (favorite == true) {
                               saveFavorite();
                               Snackbar.make(buttonView, "Added to Favorite",
                                       Snackbar.LENGTH_SHORT).show();
                           } else {
                               favoriteDataBase = new FavoriteDataBase(DetailActivity.this);
                               favoriteDataBase.deleteFavorite(movie_id);
                               Snackbar.make(buttonView, "Removed from Favorite",
                                       Snackbar.LENGTH_SHORT).show();
                           }
                       }
                   });
       } else {
           materialFavoriteButton.setOnFavoriteChangeListener(
                   new MaterialFavoriteButton.OnFavoriteChangeListener() {
                       @Override
                       public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                           if (favorite == true) {
                               saveFavorite();
                               Snackbar.make(buttonView, "Added to Favorite",
                                       Snackbar.LENGTH_SHORT).show();
                           } else {
                               int movie_id = getIntent().getExtras().getInt("id");
                               favoriteDataBase = new FavoriteDataBase(DetailActivity.this);
                               favoriteDataBase.deleteFavorite(movie_id);
                               Snackbar.make(buttonView, "Removed from Favorite",
                                       Snackbar.LENGTH_SHORT).show();
                           }
                       }
                   });


       }
   }

   public boolean search(String searchItem){
           String[] projection = {
                   FavoriteContract.FavoriteEntry._ID,
                   FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID,
                   FavoriteContract.FavoriteEntry.COLUMN_TITLE,
                   FavoriteContract.FavoriteEntry.COLUMN_USER_RATING,
                   FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH,
                   FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS
            };
           String selection = FavoriteContract.FavoriteEntry.COLUMN_TITLE + " =?";
           String[] selectionArgs = { searchItem };
           String limit = "1";

           Cursor cursor = mDb.query(FavoriteContract.FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null, limit);
           boolean exists = (cursor.getCount() > 0);
           cursor.close();
           return exists;
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
