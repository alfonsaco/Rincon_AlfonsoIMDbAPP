package edu.pruebas.rincon_alfonsoimdbapp.api;

import retrofit2.Call;
import edu.pruebas.rincon_alfonsoimdbapp.models.MovieOverviewResponse;
import edu.pruebas.rincon_alfonsoimdbapp.models.PopularMoviesResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IMDBApiService {
    @GET("title/get-top-meter")
    Call<PopularMoviesResponse> top10(@Query("Country") String country);

    @GET("title/get-overview")
    Call<MovieOverviewResponse> obtencionDatos(@Query("tconst") String movieId);
}
