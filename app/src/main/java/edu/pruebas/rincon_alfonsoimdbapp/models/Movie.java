package edu.pruebas.rincon_alfonsoimdbapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String id;
    private String titulo;
    private String fechaSalida;
    private String rutaPoster;
    private String descripcion;
    private String puntuacion;

    public Movie() {
    }

    // Constructor con todos los campos
    public Movie(String id, String title, String releaseDate, String posterPath, String descripcion, String rating) {
        this.id = id;
        this.titulo = title;
        this.fechaSalida = releaseDate;
        this.rutaPoster = posterPath;
        this.descripcion = descripcion;
        this.puntuacion = rating;
    }

    // Constructor para el Parcel
    protected Movie(Parcel in) {
        id = in.readString();
        titulo = in.readString();
        fechaSalida = in.readString();
        rutaPoster = in.readString();
        descripcion = in.readString();
        puntuacion = in.readString();
    }

    // Métodos para la serialización con Parcelable
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getRutaPoster() {
        return rutaPoster;
    }

    public void setRutaPoster(String rutaPoster) {
        this.rutaPoster = rutaPoster;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(String puntuacion) {
        this.puntuacion = puntuacion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(titulo);
        dest.writeString(fechaSalida);
        dest.writeString(rutaPoster);
        dest.writeString(descripcion);
        dest.writeString(puntuacion);
    }
}
