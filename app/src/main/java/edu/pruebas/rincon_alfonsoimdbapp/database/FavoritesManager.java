package edu.pruebas.rincon_alfonsoimdbapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.pruebas.rincon_alfonsoimdbapp.FavoritesDatabaseHelper;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;

public class FavoritesManager {

    private final FavoritesDatabaseHelper dbHelper;

    public FavoritesManager(Context context) {
        dbHelper = new FavoritesDatabaseHelper(context);
    }

    public void addFavorite(String userId, Movie movie) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavoritesDatabaseHelper.COLUMN_USER_ID, userId);
        values.put(FavoritesDatabaseHelper.COLUMN_MOVIE_ID, movie.getId());
        values.put(FavoritesDatabaseHelper.COLUMN_TITLE, movie.getTitulo());
        values.put(FavoritesDatabaseHelper.COLUMN_POSTER_PATH, movie.getRutaPoster());
        values.put(FavoritesDatabaseHelper.COLUMN_RATING, movie.getPuntuacion());

        db.insert(FavoritesDatabaseHelper.TABLE_NAME, null, values);
        db.close();
    }

    public void removeFavorite(String userId, String movieId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(FavoritesDatabaseHelper.TABLE_NAME,
                FavoritesDatabaseHelper.COLUMN_USER_ID + "=? AND " + FavoritesDatabaseHelper.COLUMN_MOVIE_ID + "=?",
                new String[]{userId, movieId});
        db.close();
    }

    public List<Movie> getFavorites(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Movie> favorites = new ArrayList<>();

        Cursor cursor = db.query(FavoritesDatabaseHelper.TABLE_NAME,
                null,
                FavoritesDatabaseHelper.COLUMN_USER_ID + "=?",
                new String[]{userId},
                null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Movie movie = new Movie(
                        cursor.getString(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMN_MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMN_TITLE)),
                        null,
                        cursor.getString(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMN_POSTER_PATH)),
                        null,
                        cursor.getString(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMN_RATING))
                );
                favorites.add(movie);
            }
            cursor.close();
        }

        db.close();
        return favorites;
    }
}
