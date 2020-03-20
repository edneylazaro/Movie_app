package com.example.mainstreammovieapp.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;


public class MovieJSON {
    JsonParser jsonParser = new JsonParser();

    private static final String MDB_POSTER = "poster_path";
    private static final String MDB_ADULT = "adult";
    private  static final String MDB_OVERVIEW = "overview";
    private static final String MDB_RELEASE_DATE = "release_date";
    private static final String MDB_GENRE_IDS = "genre_ids";
    private  static final String MDB_ID = "id";
    private  static final String MDB_ORIGINAL_TITLE = "original_title";
    private  static final String MDB_ORIGINAL_LANGUAGE = "original_language";
    private  static final String MDB_TITLE = "title";
    private  static final String MDB_BACKDROP_PATH = "backdrop_path";
    private  static final String MDB_POPULARITY = "popularity";
    private  static final String MDB_VOTE_COUNT = "vote_count";
    private  static final String MDB_VIDEO = "video";
    private  static final String MDB_VOTE_AVERAGE = "vote_average";

    private static final String MDB_MESSAGE_CODE = "cod";

   /* public static  ContentValues[] getMovieContentValuesFromJson(Context context, String movieJsonStr)
            throws JSONException {

        throw JSONException{
            JSONObject movieJson = new JSONObject(movieJsonStr);
            if(movieJson.has(MDB_MESSAGE_CODE)){
                int errorCode = movieJson.getInt(MDB_MESSAGE_CODE);

                switch (errorCode){
                    case HttpURLConnection.HTTP_OK:
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                        return null;
                    default:
                        return null;

                }
            }
            JSONArray jasonMovieArray = movieJson.getJSONArray(MDB_POSTER);


        }
    }*/
}
