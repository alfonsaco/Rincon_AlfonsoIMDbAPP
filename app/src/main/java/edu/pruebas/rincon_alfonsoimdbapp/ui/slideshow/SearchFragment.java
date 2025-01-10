package edu.pruebas.rincon_alfonsoimdbapp.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.pruebas.rincon_alfonsoimdbapp.adapters.FavoritesAdapter;
import edu.pruebas.rincon_alfonsoimdbapp.database.FavoritesManager;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;
import edu.pruebas.rincon_alfonsoimdbapp.R;

public class SearchFragment extends Fragment {

    private FavoritesManager favoritesManager;
    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private String userId = "default_user"; // Cambiar según el usuario logeado

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Inicializar RecyclerView y FavoritesManager
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        favoritesManager = new FavoritesManager(requireContext());

        // Cargar las películas favoritas
        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        List<Movie> favorites = favoritesManager.getFavorites(userId);

        adapter = new FavoritesAdapter(requireContext(), favorites, movie -> {
            // Eliminar película de favoritos al realizar un OnLongClick
            favoritesManager.removeFavorite(userId, movie.getId());
            Toast.makeText(getContext(), "Película eliminada de favoritos", Toast.LENGTH_SHORT).show();
            loadFavorites(); // Volver a cargar las favoritas
        });

        recyclerView.setAdapter(adapter);
    }
}
