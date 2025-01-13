// package edu.pruebas.rincon_alfonsoimdbapp.models;

package edu.pruebas.rincon_alfonsoimdbapp.models;

import com.google.gson.annotations.SerializedName;

public class MovieDetailsResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String titulo;

    @SerializedName("release_date")
    private String fechaSalida;

    @SerializedName("overview")
    private String descripcion;

    @SerializedName("vote_average")
    private float puntuacion;

    @SerializedName("poster_path")
    private String rutaPoster;

    // Getters y Setters
    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public float getPuntuacion() {
        return puntuacion;
    }

    public String getRutaPoster() {
        return rutaPoster;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPuntuacion(float puntuacion) {
        this.puntuacion = puntuacion;
    }

    public void setRutaPoster(String rutaPoster) {
        this.rutaPoster = rutaPoster;
    }
}
