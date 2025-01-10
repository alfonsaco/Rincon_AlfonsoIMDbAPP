package edu.pruebas.rincon_alfonsoimdbapp.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import edu.pruebas.rincon_alfonsoimdbapp.R;
import edu.pruebas.rincon_alfonsoimdbapp.adapters.FavoritesAdapter;
import edu.pruebas.rincon_alfonsoimdbapp.database.FavoritesManager;
import edu.pruebas.rincon_alfonsoimdbapp.databinding.FragmentSearchBinding;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    private FavoritesManager favoritesManager;
    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private String userId;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Obtenemos el usuario que está usando la app
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        // Inicializar RecyclerView y FavoritesManager
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        favoritesManager = new FavoritesManager(requireContext());

        // Cargar las películas favoritas
        loadFavorites();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadFavorites() {
        List<Movie> favorites = favoritesManager.getFavorites(userId);

        if (favorites.isEmpty()) {
            Toast.makeText(requireContext(), "No hay películas favoritas para este usuario.", Toast.LENGTH_SHORT).show();
        } else {
            for (Movie movie : favorites) {
                Log.d("SearchFragment", "Película favorita: " + movie.getTitulo());
            }
        }

        if (adapter == null) {
            adapter = new FavoritesAdapter(requireContext(), favorites, movie -> {
                favoritesManager.removeFavorite(userId, movie.getId());
                Toast.makeText(getContext(), "Película eliminada de favoritos", Toast.LENGTH_SHORT).show();
                loadFavorites();
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.actualizarDatos(favorites);
        }
    }

}