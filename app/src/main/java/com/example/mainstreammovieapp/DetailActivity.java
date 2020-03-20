package com.example.mainstreammovieapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class DetailActivity extends AppCompatActivity {
    TextView nameOfMovie, plotSynopsis, releaseDate;
    ImageView imageView;

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_card);
        imageView = findViewById(R.id.iv_poster_MC);
        nameOfMovie = findViewById(R.id.tv_movie_name);
        plotSynopsis = findViewById(R.id.tv_synopsis);

        Bundle extras = getIntent().getExtras();
            String thumbnail = extras.getString("post_path");
            String movieName = extras.getString("original_title");
            String synopsis = extras.getString("overview");

            Glide.with(this).load(thumbnail)
                    .apply(new RequestOptions()
                    .placeholder(R.mipmap.ic_launcher))
                    .into(imageView);
            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);

    }
}
