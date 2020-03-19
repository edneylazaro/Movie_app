package com.example.mainstreammovieapp.utilities;

import android.os.Parcelable;

import com.example.mainstreammovieapp.BuildConfig;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("adult")
    private boolean adult;
    @SerializedName("overview")
    private String overView;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("genre_ids")
    private List<Integer> genreIds = new ArrayList<>();
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("backdrop_path")
    private String backdropPath;

    public Movie(String mPath, boolean mAdult, String mOverView, String mRDate,
                 List<Integer> mGenreIds, int mId, String mTitle, String mBackdropPath){
        this.posterPath = mPath;
        this.adult = mAdult;
        this.overView = mOverView;
        this.releaseDate = mRDate;
        this.genreIds = mGenreIds;
        this.id = mId;
        this.title = mTitle;
        this.backdropPath = mBackdropPath;
    }

    public Movie(){}



    public String getPosterPath() {
        return BuildConfig.BASE_IMAGE_URL + posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverView() {
        return overView;
    }

    public void setOverView(String overView) {
        this.overView = overView;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
}
