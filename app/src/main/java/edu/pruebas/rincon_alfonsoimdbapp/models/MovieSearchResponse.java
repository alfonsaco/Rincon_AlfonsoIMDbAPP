package edu.pruebas.rincon_alfonsoimdbapp.models;

import java.util.List;

public class MovieSearchResponse {
    private List<Genre> genres;

    // Getter
    public List<Genre> getGenres() {
        return genres;
    }

    // Setter
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}