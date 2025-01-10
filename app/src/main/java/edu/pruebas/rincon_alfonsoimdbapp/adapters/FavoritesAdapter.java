package edu.pruebas.rincon_alfonsoimdbapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.pruebas.rincon_alfonsoimdbapp.MovieDetailsActivity;
import edu.pruebas.rincon_alfonsoimdbapp.R;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    // Contexto del fragmento donde se va a usar el adaptador
    private final Context context;
    private final List<Movie> peliculasFavoritas;
    private final OnMovieLongClickListener onMovieLongClickListener;

    // Interfaz para los eventos de LongClick, para eliminar una peli de favoritos
    public interface OnMovieLongClickListener {
        void onMovieLongClick(Movie movie);
    }
    // Constructor
    public FavoritesAdapter(Context context, List<Movie> peliculasFavoritas, OnMovieLongClickListener listener) {
        this.context=context;
        this.peliculasFavoritas=peliculasFavoritas;
        this.onMovieLongClickListener=listener;
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
        Movie pelicula=peliculasFavoritas.get(position);

        holder.txtTituloFavorita.setText(pelicula.getTitulo());
        holder.txtRatingFavorita.setText(pelicula.getPuntuacion() != null ? "Rating: " + pelicula.getPuntuacion() : "Rating no disponible");

        // Se carga la imagen
        Glide.with(context).load(pelicula.getRutaPoster()).into(holder.posterImageView);

        // Evento de Click, para abrir los datos de la película cuando se pulse en ella
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra("pelicula", pelicula);
                context.startActivity(intent);
            }
        });

        // Evento de onLongClick, para eliminar la película de la BBDD
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onMovieLongClickListener.onMovieLongClick(pelicula);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return peliculasFavoritas.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView txtTituloFavorita;
        TextView txtRatingFavorita;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inserta los datos en los componentes del "item_favorite_movie.xml"
            posterImageView=itemView.findViewById(R.id.posterImageView);
            txtTituloFavorita=itemView.findViewById(R.id.txtTituloFavorita);
            txtRatingFavorita=itemView.findViewById(R.id.txtRatingFavorita);
        }
    }

    public void actualizarDatos(List<Movie> nuevasPeliculas) {
        this.peliculasFavoritas.clear();
        this.peliculasFavoritas.addAll(nuevasPeliculas);
        // Para notificar al adaptador que los datos han cambiado
        notifyDataSetChanged();
    }
}
