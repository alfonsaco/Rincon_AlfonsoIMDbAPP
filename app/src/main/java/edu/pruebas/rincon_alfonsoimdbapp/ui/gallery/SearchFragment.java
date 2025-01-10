package edu.pruebas.rincon_alfonsoimdbapp.ui.gallery;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.List;

import edu.pruebas.rincon_alfonsoimdbapp.R;
import edu.pruebas.rincon_alfonsoimdbapp.adapters.FavoritesAdapter;
import edu.pruebas.rincon_alfonsoimdbapp.database.FavoritesManager;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;

public class SearchFragment extends Fragment {

    private FavoritesManager favoritesManager;
    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private String userId;

    private Button btnCompartir;
    private ActivityResultLauncher<String[]> bluetoothPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configurar el launcher para solicitar permisos
        bluetoothPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean bluetoothConnectGranted = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        bluetoothConnectGranted = result.getOrDefault(android.Manifest.permission.BLUETOOTH_CONNECT, false);
                    }
                    boolean bluetoothScanGranted = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        bluetoothScanGranted = result.getOrDefault(android.Manifest.permission.BLUETOOTH_SCAN, false);
                    }

                    if (bluetoothConnectGranted && bluetoothScanGranted) {
                        mostrarDialogoJSON();
                    } else {
                        Toast.makeText(requireContext(), "Permisos de Bluetooth denegados", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Obtenemos el usuario actual
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

        // Botón Compartir. Se verifican los permisos de Bluetooth, y se muestra el mensaje JSON
        btnCompartir = view.findViewById(R.id.btnCompartir);
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                        bluetoothPermissionLauncher.launch(new String[]{
                                android.Manifest.permission.BLUETOOTH_CONNECT,
                                android.Manifest.permission.BLUETOOTH_SCAN
                        });
                    } else {
                        mostrarDialogoJSON();
                    }
                } else {
                    // Android inferior a 12
                    if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        bluetoothPermissionLauncher.launch(new String[]{android.Manifest.permission.BLUETOOTH});
                    } else {
                        mostrarDialogoJSON();
                    }
                }
            }
        });

        return view;
    }

    // Se crea el JSON de las películas
    private void mostrarDialogoJSON() {
        // Obtener las películas favoritas
        List<Movie> favorites=favoritesManager.getFavorites(userId);

        if (favorites.isEmpty()) {
            Toast.makeText(requireContext(), "No tienes películas favoritas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir las películas a JSON
        Gson gson = new Gson();
        String jsonPeliculas = gson.toJson(favorites);

        // Inflar el diálogo
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_favoritas_json, null);
        TextView txtJSON = dialogView.findViewById(R.id.txtJSON);
        txtJSON.setText(jsonPeliculas);

        new AlertDialog.Builder(requireContext())
                .setTitle("Películas Favoritas")
                .setView(dialogView)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    // Se cargan las películas favoritas
    private void loadFavorites() {
        List<Movie> favorites=favoritesManager.getFavorites(userId);

        if (favorites.isEmpty()) {
            Toast.makeText(requireContext(), "No hay películas favoritas para este usuario.", Toast.LENGTH_SHORT).show();
        } else {
            for (Movie movie : favorites) {
                Log.d("SearchFragment", "Película favorita: " + movie.getTitulo());
            }
        }

        // Configurar o actualizar el adaptador del RecyclerView
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
