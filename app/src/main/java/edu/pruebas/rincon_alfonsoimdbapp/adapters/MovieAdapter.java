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
import edu.pruebas.rincon_alfonsoimdbapp.MovieDetailsActivity;
import edu.pruebas.rincon_alfonsoimdbapp.R;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final Context context;
    private final List<Movie> peliculas;

    public MovieAdapter(Context context, List<Movie> peliculas) {
        this.context = context;
        this.peliculas = peliculas;
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

        // Asignar datos a las vistas
        holder.tituloTextView.setText(pelicula.getTitulo());
        holder.anioTextView.setText(pelicula.getFechaSalida());

        // Cargar la imagen del póster usando Glide
        Glide.with(context)
                .load(pelicula.getRutaPoster())
                .into(holder.posterImageView);

        // Listener para el clic en cada elemento
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra("pelicula", pelicula);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return peliculas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTextView;
        TextView anioTextView;
        ImageView posterImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTextView = itemView.findViewById(R.id.textViewTitle);
            anioTextView = itemView.findViewById(R.id.textViewYear);
            posterImageView = itemView.findViewById(R.id.imageViewPoster);
        }
    }
}
