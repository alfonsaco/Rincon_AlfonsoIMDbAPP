package edu.pruebas.rincon_alfonsoimdbapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import edu.pruebas.rincon_alfonsoimdbapp.MovieDetailsActivity;
import edu.pruebas.rincon_alfonsoimdbapp.R;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;
import edu.pruebas.rincon_alfonsoimdbapp.utils.Constants;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private final Context context;
    private final List<Movie> favoritas;
    private final String source;
    private final OnItemLongClickListener longClickListener;

    // Interfaz para manejar el evento de LongClick
    public interface OnItemLongClickListener {
        void onItemLongClick(Movie movie);
    }

    // Constructor del adaptador
    public FavoritesAdapter(Context context, List<Movie> favoritas, OnItemLongClickListener longClickListener, String source) {
        this.context = context;
        this.favoritas = favoritas;
        this.longClickListener = longClickListener;
        this.source = source;
    }

    @NonNull
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new FavoritesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.ViewHolder holder, int position) {
        Movie pelicula = favoritas.get(position);

        // Verificar que la película no sea nula
        if (pelicula != null) {
            // Asignar datos a los componentes
            holder.tituloTextView.setText(pelicula.getTitulo());
            String año = "";
            if (pelicula.getFechaSalida() != null && !pelicula.getFechaSalida().isEmpty()) {
                año = pelicula.getFechaSalida().substring(0, 4);
            }
            holder.anioTextView.setText(año);

            // Cargar la imagen del póster usando Glide
            String rutaImagen = pelicula.getRutaPoster();
            if (rutaImagen != null && !rutaImagen.isEmpty()) {
                if (!rutaImagen.startsWith("http://") && !rutaImagen.startsWith("https://")) {
                    if (source.equals(Constants.SOURCE_TMDB)) {
                        // Se verifica que la imagen contenga ese texto
                        rutaImagen = "https://image.tmdb.org/t/p/w500" + rutaImagen;
                    } else if (source.equals(Constants.SOURCE_IMD)) {
                        rutaImagen = "https://image.imdb.com" + rutaImagen;
                    }
                }
                Glide.with(context)
                        .load(rutaImagen)
                        .into(holder.posterImageView);
            }

            // Manejar el Click normal para abrir detalles
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra("pelicula", pelicula);
                    intent.putExtra("source", source);
                    context.startActivity(intent);
                }
            });

            // Manejar el clic largo para eliminar de favoritos
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (longClickListener != null) {
                        longClickListener.onItemLongClick(pelicula);
                        return true;
                    }
                    return false;
                }
            });

        } else {
            Log.e("FavoritesAdapter", "Película en posición " + position + " es nula.");
        }
    }

    @Override
    public int getItemCount() {
        return (favoritas != null) ? favoritas.size() : 0;
    }

    // Método para actualizar la lista de favoritas. Se vacía y luego se añaden todos los elementos de la lista
    public void actualizarDatos(List<Movie> nuevasFavoritas) {
        this.favoritas.clear();
        this.favoritas.addAll(nuevasFavoritas);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tituloTextView;
        TextView anioTextView;
        ImageView posterImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Se obtienen los componente3s por el id
            tituloTextView = itemView.findViewById(R.id.txtTituloPelicula);
            anioTextView = itemView.findViewById(R.id.txtAñoPelicula);
            posterImageView = itemView.findViewById(R.id.imageViewPoster);
        }
    }
}
