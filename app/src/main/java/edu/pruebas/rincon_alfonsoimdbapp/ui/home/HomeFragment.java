package edu.pruebas.rincon_alfonsoimdbapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.pruebas.rincon_alfonsoimdbapp.R;
import edu.pruebas.rincon_alfonsoimdbapp.adapters.MovieAdapter;
import edu.pruebas.rincon_alfonsoimdbapp.api.IMDBApiService;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;
import edu.pruebas.rincon_alfonsoimdbapp.models.PopularMoviesResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private IMDBApiService imdbApiService;
    private List<Movie> movieList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Configurar RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewTopMovies);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new MovieAdapter(getContext(), movieList);
        recyclerView.setAdapter(adapter);

        // Configuración de Retrofit y OkHttp
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request modifiedRequest = chain.request().newBuilder()
                            .addHeader("X-RapidAPI-Key", "bdb1444c4amshd033444ce845bbbp12ff63jsn7bf5fe5f9fab")
                            .addHeader("X-RapidAPI-Host", "imdb-com.p.rapidapi.com").build();
                    return chain.proceed(modifiedRequest);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://imdb-com.p.rapidapi.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        imdbApiService = retrofit.create(IMDBApiService.class);

        // Llamada a la API
        fetchTopMovies();

        return view;
    }

    private void fetchTopMovies() {
        Call<PopularMoviesResponse> call = imdbApiService.top10("US");
        call.enqueue(new Callback<PopularMoviesResponse>() {
            @Override
            public void onResponse(Call<PopularMoviesResponse> call, Response<PopularMoviesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PopularMoviesResponse.Edge> edges = response.body().getData().getTopMeterTitles().getEdges();
                    if (edges != null && !edges.isEmpty()) {
                        movieList.clear();
                        for (int i = 0; i < Math.min(edges.size(), 10); i++) {
                            PopularMoviesResponse.Edge edge = edges.get(i);
                            PopularMoviesResponse.Node node = edge.getNode();
                            Movie movie = new Movie();
                            movie.setId(node.getId());
                            movie.setTitulo(node.getTitleText().getText());
                            movie.setFechaSalida(node.getReleaseDate().getYear() + "");
                            movie.setRutaPoster(node.getPrimaryImage().getUrl());
                            movieList.add(movie);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("HomeFragment", "Error en la respuesta: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PopularMoviesResponse> call, Throwable t) {
                Log.e("HomeFragment", "Error en la llamada API: " + t.getMessage());
            }
        });
    }
}
