package com.example.mainstreammovieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.example.mainstreammovieapp.Api.Client;
import com.example.mainstreammovieapp.Api.Service;
import com.example.mainstreammovieapp.utilities.Movie;
import com.example.mainstreammovieapp.utilities.MovieAdapter;
import com.example.mainstreammovieapp.utilities.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity  {

    private static final String LOG_TAG = MovieAdapter.class.getName();
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> movieList;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;

    public Activity getActivity (){
        Context context = this;
        while (context instanceof ContextWrapper){
            if(context instanceof Activity){
                return(Activity) context;
            }
            context = ((ContextWrapper) ((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        swipeContainer = findViewById(R.id.action_settings);
        swipeContainer.setColorSchemeResources(android.R.color.background_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(){
        pd = new ProgressDialog(this);
        pd.setMessage("Passing Movies...");
        pd.setCancelable(false);
        pd.show();

        recyclerView = findViewById(R.id.recyclerViewMain);

        movieList = new ArrayList<>();
        adapter = new MovieAdapter(this, movieList);

        if(getActivity().getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager((new GridLayoutManager(this, 2)));
        }else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadJSON();
    }

    private void loadJSON(){
        try {
            if (BuildConfig.MOVIE_DB_API_TOKEN != null){
                Toast.makeText(getApplicationContext(), "Please obtain API Key from themoviedb.org", Toast.LENGTH_LONG).show();
                pd.dismiss();
                return;
            }

            Client client = new Client();
            Service apiService =
                    client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if(swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                    pd.dismiss();
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
