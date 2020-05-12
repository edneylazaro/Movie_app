package com.example.mainstreammovieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mainstreammovieapp.Api.Service;
import com.example.mainstreammovieapp.DB.FavoriteDataBase;
import com.example.mainstreammovieapp.utilities.Movie;
import com.example.mainstreammovieapp.utilities.MovieAdapter;
import com.example.mainstreammovieapp.utilities.MovieAdapterListener;
import com.example.mainstreammovieapp.utilities.MoviesResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView recyclerView;
    private  RecyclerView.LayoutManager layoutManager;
    private SearchView searchView;
    private MovieAdapter mAdapter;
    private List<Movie> movieList;
    private SwipeRefreshLayout swipeContainer;
    private FavoriteDataBase favoriteDataBase;
    int cacheSize = 10 * 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        initViews();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                 = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
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

    private void initViews(){

        movieList = new ArrayList<>();
        mAdapter = new MovieAdapter(this, movieList);

        if(getActivity().getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager((new GridLayoutManager(this, 2)));
        }else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        favoriteDataBase = new FavoriteDataBase(this);



        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.content_main);
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                initViews();
                Toast.makeText(MainActivity.this, "Movies Refreshed", Toast.LENGTH_SHORT).show();
            }
        });
        checkSortOrder();
    }

    private void loadPopularMovies(){
        try {
            if (BuildConfig.MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain your API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }
            Cache cache = new Cache(getCacheDir(), cacheSize);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain)
                                throws IOException {
                            Request request = chain.request();
                            if(!isNetworkAvailable()){
                                int maxStale = 60 * 60 * 24 * 28;
                                request = request.newBuilder()
                                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                        .build();
                            }
                            return chain.proceed(request);
                        }
                    }).build();
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit = builder.build();

            Service apiService =
                    retrofit.create(Service.class);

            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    Collections.sort(movies, Movie.BY_NAME_ALPHABETICAL);
                    recyclerView.setAdapter(new MovieAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if(swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
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

    private void loadTopRatedMovies(){
        try{
            if(BuildConfig.MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please obtain your API Key from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }
            Cache cache = new Cache(getCacheDir(), cacheSize);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain)
                                throws IOException {
                            Request request = chain.request();
                            if(!isNetworkAvailable()){
                                int maxStale = 60 * 60 * 24 * 28;
                                request = request.newBuilder()
                                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                        .build();
                            }
                            return chain.proceed(request);
                        }
                    }).build();
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();

            Service apiService =
                    retrofit.create(Service.class);
            Call<MoviesResponse> call = apiService.getTopRatedMovies(BuildConfig.MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MovieAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if(swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sortedByFavorite(){
        recyclerView = findViewById(R.id.recycler);

        movieList = new ArrayList<>();
        mAdapter = new MovieAdapter(this, movieList);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        favoriteDataBase = new FavoriteDataBase(this);
        getAllFavorite();
        if(swipeContainer.isRefreshing()){
            swipeContainer.setRefreshing(false);
        }
    }

    private void getAllFavorite(){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                movieList.clear();
                movieList.addAll(favoriteDataBase.getAllFavorite());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
       if(id == R.id.action_settings){
           Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
           startActivity(startSettingsActivity);
           return true;
        }
       if(id == R.id.action_search){
           return true;
       }

       return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        checkSortOrder();
    }

    private void checkSortOrder(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );
        if(sortOrder.equals(this.getString(R.string.pref_most_popular))) {
            loadPopularMovies();
        } else if(sortOrder.equals(this.getString(R.string.favorite))) {
            sortedByFavorite();
        } else {
            loadTopRatedMovies();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(movieList.isEmpty()){
            checkSortOrder();
        } else{
            checkSortOrder();
        }
    }

}
