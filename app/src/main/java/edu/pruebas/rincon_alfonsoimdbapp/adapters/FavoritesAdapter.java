package edu.pruebas.rincon_alfonsoimdbapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.pruebas.rincon_alfonsoimdbapp.R;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private final Context context;
    private final List<Movie> favoriteMovies;
    private final OnMovieLongClickListener onMovieLongClickListener;

    public interface OnMovieLongClickListener {
        void onMovieLongClick(Movie movie);
    }

    public FavoritesAdapter(Context context, List<Movie> favoriteMovies, OnMovieLongClickListener listener) {
        this.context = context;
        this.favoriteMovies = favoriteMovies;
        this.onMovieLongClickListener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite_movie, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Movie movie = favoriteMovies.get(position);

        holder.titleTextView.setText(movie.getTitulo());
        holder.ratingTextView.setText(movie.getPuntuacion() != null ? "Rating: " + movie.getPuntuacion() : "Rating no disponible");

        // Usar Glide para cargar la imagen
        Glide.with(context)
                .load(movie.getRutaPoster())
                .into(holder.posterImageView);

        // Configurar el evento de OnLongClick en todo el elemento
        holder.itemView.setOnLongClickListener(v -> {
            onMovieLongClickListener.onMovieLongClick(movie);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return favoriteMovies.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView titleTextView;
        TextView ratingTextView;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
        }
    }
}
