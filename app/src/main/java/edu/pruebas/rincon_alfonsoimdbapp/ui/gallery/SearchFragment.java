package edu.pruebas.rincon_alfonsoimdbapp.ui.gallery;

import android.Manifest;
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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private String idUsuario;

    private Button btnCompartir;

    private ActivityResultLauncher<String[]> launcherBluetooth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar el launcher para solicitar permisos de Bluetooth
        launcherBluetooth = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<java.util.Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(java.util.Map<String, Boolean> result) {
                        boolean allGranted = true;
                        for (Boolean granted : result.values()) {
                            if (!granted) {
                                allGranted = false;
                                break;
                            }
                        }

                        if (allGranted) {
                            mostrarDialogoJSON();
                        } else {
                            Toast.makeText(requireContext(), "Debe activar Bluetooth para mostrar JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Obtener usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            idUsuario = currentUser.getUid();
        } else {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            idUsuario = null;
        }

        // Inicializar RecyclerView y FavoritesManager
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        favoritesManager = new FavoritesManager(requireContext());
        cargarFavoritas();

        // Configurar botón Compartir
        btnCompartir = view.findViewById(R.id.btnCompartir);
        btnCompartir.setOnClickListener(v -> solicitarPermisosBluetooth());

        return view;
    }

    private void solicitarPermisosBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 y superior
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                // Solicitar permisos de Bluetooth
                launcherBluetooth.launch(new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                });
            } else {
                // Permisos ya concedidos
                mostrarDialogoJSON();
            }
        } else { // Versiones anteriores a Android 12
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {

                // Solicitar permisos de Bluetooth
                launcherBluetooth.launch(new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                });
            } else {
                // Permisos ya concedidos
                mostrarDialogoJSON();
            }
        }
    }

    // Método que muestra el diálogo JSON
    private void mostrarDialogoJSON() {
        // Lista de películas
        List<Movie> peliculasFavoritas = favoritesManager.obtenerFavoritas(idUsuario);
        // Se debe haber agregado alguna película para que pueda usarse la función
        if (peliculasFavoritas.isEmpty()) {
            Toast.makeText(requireContext(), "No tienes películas favoritas", Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        String jsonPeliculas = gson.toJson(peliculasFavoritas);

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_favoritas_json, null);
        TextView txtJSON = dialogView.findViewById(R.id.txtJSON);
        txtJSON.setText(jsonPeliculas);

        // Diálogo con el JSON
        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    // Carga todas las películas que han sido agregadas a favoritas al Recycler
    private void cargarFavoritas() {
        List<Movie> peliculasFavoritas = favoritesManager.obtenerFavoritas(idUsuario);
        if (peliculasFavoritas.isEmpty()) {
            Toast.makeText(requireContext(), "No hay películas favoritas.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("SearchFragment", "Películas favoritas cargadas.");
        }

        if (adapter == null) {
            adapter = new FavoritesAdapter(requireContext(), peliculasFavoritas, movie -> {
                favoritesManager.borrarFavorita(idUsuario, movie.getId());
                cargarFavoritas();
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.actualizarDatos(peliculasFavoritas);
        }
    }
}
