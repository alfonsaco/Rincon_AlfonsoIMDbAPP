package edu.pruebas.rincon_alfonsoimdbapp.api;

import edu.pruebas.rincon_alfonsoimdbapp.models.MovieDetailsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDBApiService {
    @GET("movie/{movie_id}")
    Call<MovieDetailsResponse> obtenerDetallesPeliculas(
            @Path("movie_id") String movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );
}
