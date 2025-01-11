package edu.pruebas.rincon_alfonsoimdbapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import edu.pruebas.rincon_alfonsoimdbapp.MovieDetailsActivity;
import edu.pruebas.rincon_alfonsoimdbapp.R;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private final Context context;
    private final List<Movie> peliculasFavoritas;
    // Esto es una interfaz para eliminar las peliculas de favoritos
    private final OnFavoriteClickListener listener;

    // Interfaz para eliminar de favoritos
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Movie movie);
    }

    // Constructor
    public FavoritesAdapter(Context context, List<Movie> peliculasFavoritas, OnFavoriteClickListener listener) {
        this.context = context;
        this.peliculasFavoritas = peliculasFavoritas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_movie, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        // Se obtiene la película en cuestión
        Movie pelicula = peliculasFavoritas.get(position);

        // Cargar datos de la imagen: nombre y poster
        holder.txtTituloFavorita.setText(pelicula.getTitulo());
        String rutaImagen = pelicula.getRutaPoster();
        if (rutaImagen != null && !rutaImagen.isEmpty()) {
            // Se verifica que sea válila la URL
            if (!rutaImagen.startsWith("http://") && !rutaImagen.startsWith("https://")) {
                rutaImagen = "https://image.tmdb.org/t/p/w500" + rutaImagen;
            }
            // Se pone con Glide
            Glide.with(context)
                    .load(rutaImagen)
                    .into(holder.posterImageView);
        }

        // Evento de Click para abrir detalles de la película
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra("pelicula", pelicula);
                context.startActivity(intent);
            }
        });

        // Evento de onLongClick para eliminar la película de favoritos
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    listener.onFavoriteClick(pelicula);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return (peliculasFavoritas != null) ? peliculasFavoritas.size() : 0;
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView txtTituloFavorita;
        TextView txtRatingFavorita;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inserta los datos en los componentes del "item_favorite_movie.xml"
            posterImageView = itemView.findViewById(R.id.posterImageView);
            txtTituloFavorita = itemView.findViewById(R.id.txtTituloFavorita);
            txtRatingFavorita = itemView.findViewById(R.id.txtRating);
        }
    }

    // Método para actualizar los datos en el RecyclerView. Se vacía, y después, se añaden lo elementos de nuevo
    public void actualizarDatos(List<Movie> nuevasPeliculas) {
        this.peliculasFavoritas.clear();
        this.peliculasFavoritas.addAll(nuevasPeliculas);
        notifyDataSetChanged();
    }
}
