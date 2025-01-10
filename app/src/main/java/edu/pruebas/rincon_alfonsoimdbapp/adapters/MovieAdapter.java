package edu.pruebas.rincon_alfonsoimdbapp.adapters;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

import edu.pruebas.rincon_alfonsoimdbapp.MovieDetailsActivity;
import edu.pruebas.rincon_alfonsoimdbapp.MovieDetailsActivity;
import edu.pruebas.rincon_alfonsoimdbapp.R;
import edu.pruebas.rincon_alfonsoimdbapp.database.FavoritesManager;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final Context context;
    private final List<Movie> peliculas;
    private FavoritesManager favoritesManager;

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

        // Asignar datos a las vistas
        holder.tituloTextView.setText(pelicula.getTitulo());
        holder.anioTextView.setText(pelicula.getFechaSalida());

        // Cargar la imagen del póster usando Glide
        Glide.with(context)
                .load(pelicula.getRutaPoster())
                .into(holder.posterImageView);

        // Listener para el clic en cada elemento
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra("pelicula", pelicula);
                context.startActivity(intent);
            }
        });

        // Obtener el ID de usuario que esta usando la app. Con esto, podremos guardar los favoritos
        // para cada usuario
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                favoritesManager.addFavorite(userId, pelicula);
                Toast.makeText(context, "Película añadida a favoritos", Toast.LENGTH_SHORT).show();
                return true;
            }
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
