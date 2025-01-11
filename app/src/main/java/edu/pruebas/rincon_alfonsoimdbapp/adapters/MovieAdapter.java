package edu.pruebas.rincon_alfonsoimdbapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.pruebas.rincon_alfonsoimdbapp.MovieDetailsActivity;
import edu.pruebas.rincon_alfonsoimdbapp.R;
import edu.pruebas.rincon_alfonsoimdbapp.database.FavoritesManager;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final Context context;
    private final List<Movie> peliculas;
    private final FavoritesManager favoritesManager;

    public MovieAdapter(Context context, List<Movie> peliculas) {
        this.context = context;
        this.peliculas = peliculas;
        this.favoritesManager = new FavoritesManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie pelicula = peliculas.get(position);

        // Verificar los datos de la película por consola
        if (pelicula != null) {
            Log.d("MovieAdapter", "Bind position: " + position + ", Título: " + pelicula.getTitulo());
            Log.d("MovieAdapter", "Ruta Poster: " + pelicula.getRutaPoster());
            Log.d("MovieAdapter", "Fecha Salida: " + pelicula.getFechaSalida());
        } else {
            Log.e("MovieAdapter", "Película en posición " + position + " es nula.");
        }

        // Asignar datos a los componentes. Se usará el título, poster y el año
        holder.tituloTextView.setText(pelicula.getTitulo());
        String anio = "Año no disponible";
        if (pelicula.getFechaSalida() != null && !pelicula.getFechaSalida().isEmpty()) {
            anio = pelicula.getFechaSalida().substring(0, 4);
        }
        holder.anioTextView.setText(anio);

        // Cargar la imagen del póster usando Glide
        String rutaImagen = pelicula.getRutaPoster();
        if (rutaImagen != null && !rutaImagen.isEmpty()) {
            // Verificar si la URL es válida
            if (!rutaImagen.startsWith("http://") && !rutaImagen.startsWith("https://")) {
                rutaImagen = "https://image.tmdb.org/t/p/w500" + rutaImagen;
            }
            Glide.with(context)
                    .load(rutaImagen)
                    .into(holder.posterImageView);
        }

        // Abrir datos de la película al hacer Click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra("pelicula", pelicula);
                context.startActivity(intent);
            }
        });

        // Listener para añadir a favoritos. Se obtiene el usuario actual, para diferenciar películas
        // favoritas entre usuarios
        FirebaseUser usuarioFirebase = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioFirebase != null) {
            String idUsuario = usuarioFirebase.getUid();
            // Evento de LongClick, que añadirá a favoritos la película
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    favoritesManager.addFavorite(idUsuario, pelicula);
                    Toast.makeText(context, "Película añadida a favoritos", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        } else {
            holder.itemView.setOnLongClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return (peliculas != null) ? peliculas.size() : 0;
    }

    // Método para actualizar la lista de películas
    public void setMovies(List<Movie> newPeliculas) {
        this.peliculas.clear();
        this.peliculas.addAll(newPeliculas);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTextView;
        TextView anioTextView;
        ImageView posterImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ponemos los datos en los respectivos sitios
            tituloTextView = itemView.findViewById(R.id.txtTituloPelicula);
            anioTextView = itemView.findViewById(R.id.txtAñoPelicula);
            posterImageView = itemView.findViewById(R.id.imageViewPoster);
        }
    }
}
