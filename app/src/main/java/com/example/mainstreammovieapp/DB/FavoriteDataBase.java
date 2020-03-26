package com.example.mainstreammovieapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.mainstreammovieapp.DetailActivity;
import com.example.mainstreammovieapp.utilities.Movie;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorite.db";

    private static final  int DATABASE_VERSION = 1;

    public static final String LOG_TAG = "FAVORITE";

    SQLiteOpenHelper dbHandler;

    SQLiteDatabase db;

    public FavoriteDataBase (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open(){
        Log.i(LOG_TAG, "Database Opened");
        db = dbHandler.getWritableDatabase();
    }

    public void close(){
        Log.i(LOG_TAG, "Database Closed");
        dbHandler.close();
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
      final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteContract.FavoriteEntry.TABLE_NAME + " (" +
              FavoriteContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
              FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
              FavoriteContract.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
              FavoriteContract.FavoriteEntry.COLUMN_USER_RATING + " REAL NOT NULL, " +
              FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
              FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS + " TEXT NOT NULL" +
              "); ";
      sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }

    public boolean checkFav(String movieId){
        boolean myBool = false;
        try {
            Cursor c = db.rawQuery("SELECT * FROM " + FavoriteContract.FavoriteEntry.COLUMN_TITLE + " WhERE "
                    + FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " like ? ", new String[]{"%" + movieId + "%"});
            if(c.moveToFirst()){
                do{
                    if(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID.equals(movieId)){
                         myBool = true;
                    } else {
                        myBool = false;
                    }
                } while (c.moveToNext());
            }
        }catch (Exception e){
            movieId = null;
        }
        return  myBool;
    }

    public void addFavorite( Movie movie){
        boolean myBool;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, movie.getId());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, movie.getOriginalTitle());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_USER_RATING, movie.getVoteAverage());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS, movie.getOverView());
        myBool = checkFav(values.getAsString(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID));
        if(!(myBool)){
            db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, values);
            db.close();
        } else {
            db.close();
        }

    }

    public void deleteFavorite(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME,
                FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + "=" + id, null);
    }

    public List<Movie> getAllFavorite(){
        String[] columns = {
                FavoriteContract.FavoriteEntry._ID,
                FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID,
                FavoriteContract.FavoriteEntry.COLUMN_TITLE,
                FavoriteContract.FavoriteEntry.COLUMN_USER_RATING,
                FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH,
                FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS
        };
        String sortOrder =
                FavoriteContract.FavoriteEntry._ID + " ASC";
        List<Movie> favoriteList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);
        if(cursor.moveToFirst()){
            do{
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID))));
                movie.setOriginalTitle(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE)));
                movie.setVoteAverage(Double.parseDouble(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_USER_RATING))));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH)));
                movie.setOverView(cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_PLOT_SYNOPSIS)));

                favoriteList.add(movie);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteList;
    }
}
