package edu.pruebas.rincon_alfonsoimdbapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;

public class FavoritesManager {

    private final FavoritesDatabaseHelper dbHelper;

    public FavoritesManager(Context context) {
        dbHelper = new FavoritesDatabaseHelper(context);
    }

    // Método para que el usuario añada una película a favoritos
    public void añadirFavorita(String idUsuario, Movie pelicula) {
        Log.d("FavoritesManager", "Añadiendo película: " + pelicula.getTitulo() + ", ID: " + pelicula.getId() + ", Usuario: " + idUsuario);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavoritesDatabaseHelper.COLUMNA_ID_USUARIO, idUsuario);
        values.put(FavoritesDatabaseHelper.COLUMNA_ID_PELICULA, pelicula.getId()); // String
        values.put(FavoritesDatabaseHelper.COLUMNA_TITULO, pelicula.getTitulo());
        values.put(FavoritesDatabaseHelper.COLUMNA_RUTA_POSTER, pelicula.getRutaPoster());
        values.put(FavoritesDatabaseHelper.COLUMNA_CALIFICACION, pelicula.getPuntuacion()); // float

        long result = db.insert(FavoritesDatabaseHelper.NOMBRE_TABLA, null, values);
        if (result == -1) {
            Log.e("FavoritesManager", "Error al insertar película en la base de datos.");
        } else {
            Log.d("FavoritesManager", "Película insertada con éxito. ID de inserción: " + result);
        }

        logParaVerLasPeliculas();

        db.close();
    }

    // Método para que el usuario pueda borrar una película que está en favoritos
    public void borrarFavorita(String userId, String movieId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(FavoritesDatabaseHelper.NOMBRE_TABLA,
                FavoritesDatabaseHelper.COLUMNA_ID_USUARIO + "=? AND " + FavoritesDatabaseHelper.COLUMNA_ID_PELICULA + "=?",
                new String[]{userId, movieId});
        // Mostramos por consola el número de películas eliminadas
        Log.d("FavoritesManager", "Películas eliminadas: " + rowsAffected);
        db.close();
    }

    // Este método nos da todas las películas relacionadas al usuario en cuestión
    public List<Movie> obtenerFavoritas(String idUsuario) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Movie> favorites = new ArrayList<>();

        // Buscamos a ver si hay registros con el usuario
        Cursor cursor = db.query(FavoritesDatabaseHelper.NOMBRE_TABLA,
                null, FavoritesDatabaseHelper.COLUMNA_ID_USUARIO + "=?",
                new String[]{idUsuario}, null, null, null);

        // Si hay registros, se mostrarán todas
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Recuperar los datos con los tipos correctos
                String movieId = cursor.getString(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMNA_ID_PELICULA));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMNA_TITULO));
                String rutaPoster = cursor.getString(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMNA_RUTA_POSTER));
                float puntuacion = cursor.getFloat(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMNA_CALIFICACION));

                // Crear una instancia de Movie con los datos correctos
                Movie pelicula = new Movie(
                        movieId,
                        titulo,
                        null, // fechaSalida no está almacenada en favoritos
                        rutaPoster,
                        null, // descripcion no está almacenada en favoritos
                        puntuacion
                );
                Log.d("FavoritesManager", "Película en Favoritos: " + pelicula.getTitulo());
                favorites.add(pelicula);
            }
            cursor.close();
        }

        db.close();
        return favorites;
    }

    // Se muestra por consola todos los elementos de la base de datos, para ver que funciona correctamente
    public void logParaVerLasPeliculas() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(FavoritesDatabaseHelper.NOMBRE_TABLA, null, null, null, null, null, null);

        // Se usa un cursor para recorrer todas las filas
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String userId = cursor.getString(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMNA_ID_USUARIO));
                String movieId = cursor.getString(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMNA_ID_PELICULA));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMNA_TITULO));
                float puntuacion = cursor.getFloat(cursor.getColumnIndexOrThrow(FavoritesDatabaseHelper.COLUMNA_CALIFICACION));
                Log.d("FavoritesManager", "- Usuario: " + userId + ", Película: " + title + ", ID: " + movieId + ", Puntuación: " + puntuacion);
            }
            cursor.close();
        } else {
            Log.d("FavoritesManager", "No se encontraron registros en la tabla favoritos.");
        }

        db.close();
    }
}
