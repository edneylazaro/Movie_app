package com.example.mainstreammovieapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity {
    TextView nameOfMovie, plotSynopsis, releaseDate;
    ImageView imageView;

   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imageView = findViewById(R.id.iv_poster);
        nameOfMovie = findViewById(R.id.movie_title);
        plotSynopsis = findViewById(R.id.txt_plot);

        Intent intent = getIntent();
        if(intent.hasExtra("original_path")){
            String thumbnail = getIntent().getExtras().getString("post_path");
            String movieName = getIntent().getExtras().getString("original_title");
            String synopsis = getIntent().getExtras().getString("overview");

            Glide.with(this).load(thumbnail)
                    .into(imageView);

            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);

        }

    }*/
}
