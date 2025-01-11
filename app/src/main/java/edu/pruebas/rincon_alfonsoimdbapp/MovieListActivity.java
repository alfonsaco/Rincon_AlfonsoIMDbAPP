package edu.pruebas.rincon_alfonsoimdbapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.pruebas.rincon_alfonsoimdbapp.adapters.MovieAdapter;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;
import edu.pruebas.rincon_alfonsoimdbapp.models.MovieResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieListActivity extends AppCompatActivity {

    private static final String TAG = "MovieListActivity";
    private static final String API_KEY = "aaf2cf26c82660c7a38d10d55ed5c92d";
    private static final String URL_BUSQUEDAS = "https://api.themoviedb.org/3/discover/movie";

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private OkHttpClient client;
    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        recyclerView = findViewById(R.id.recyclerView);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        // OkHttpClient y Gson
        client = new OkHttpClient();
        gson = new Gson();

        // Obtener datos del Intent
        int fecha = getIntent().getIntExtra("fecha", 0);
        int generoId = getIntent().getIntExtra("generoId", 0);

        // Se verifica una vez más que los datos sean válidos, y que hay una fecha y un id numérico
        if (fecha == 0 || generoId == 0) {
            Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Realizar la solicitud a la API
        mostrarPeliculasBusqueda(fecha, generoId);
    }

    private void mostrarPeliculasBusqueda(int año, int idGenero) {
        String url = URL_BUSQUEDAS + "?api_key=" + API_KEY +
                "&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false" +
                "&page=1&primary_release_year=" + año +
                "&with_genres=" + idGenero;

        // Se crea la solicitud
        Request request = new Request.Builder().url(url).get()
                .addHeader("accept", "application/json").build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Manejar fallo en la solicitud
                runOnUiThread(() -> {
                    Toast.makeText(MovieListActivity.this, "Error al cargar películas", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Respuesta de películas: " + responseBody);

                    // Analizar JSON
                    MovieResponse movieResponse = gson.fromJson(responseBody, MovieResponse.class);
                    List<Movie> movies = movieResponse.getResults();

                    // Verificar la lista de películas por consola
                    if (movies != null) {
                        Log.d(TAG, "Número de películas recibidas: " + movies.size());
                        if (!movies.isEmpty()) {
                            Log.d(TAG, "Primera película: " + movies.get(0).getTitulo());
                        }
                    } else {
                        Log.e(TAG, "La lista de películas es nula.");
                    }

                    // Actualizar RecyclerView en el hilo principal
                    runOnUiThread(() -> {
                        if (movies != null && !movies.isEmpty()) {
                            movieAdapter.setMovies(movies);
                        } else {
                            Toast.makeText(MovieListActivity.this, "No se encontraron películas", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Caso de error
                    runOnUiThread(() -> {
                        Toast.makeText(MovieListActivity.this, "Error al cargar películas", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}
