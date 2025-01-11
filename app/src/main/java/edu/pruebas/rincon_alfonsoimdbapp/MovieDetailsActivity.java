package edu.pruebas.rincon_alfonsoimdbapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import edu.pruebas.rincon_alfonsoimdbapp.api.TMDBApiService;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;
import edu.pruebas.rincon_alfonsoimdbapp.models.MovieDetailsResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView txtTitulo;
    private TextView txtDescripcion;
    private TextView txtFechaSalida;
    private TextView txtRating;
    private Button btnSMS;

    private String selectedContactNumber;
    private String movieTitle;
    private String movieRating;

    // Launchers para manejar permisos y selección de contacto
    private ActivityResultLauncher<String> contactPermissionLauncher;
    private ActivityResultLauncher<Intent> contactPickerLauncher;
    private ActivityResultLauncher<String> smsPermissionLauncher;

    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String TMDB_API_KEY = "aaf2cf26c82660c7a38d10d55ed5c92d"; // Tu clave de API de TMDB

    private TMDBApiService tmdbApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Inicializar vistas
        imageView = findViewById(R.id.imageView);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtFechaSalida = findViewById(R.id.txtFechaSalida);
        txtRating = findViewById(R.id.txtRating);
        btnSMS = findViewById(R.id.btnSMS);

        // Se recibe la película desde el Intent
        Intent intent = getIntent();
        Movie pelicula = intent.getParcelableExtra("pelicula");

        if (pelicula != null) {
            Log.d("MovieDetailsActivity", "Película recibida: " + pelicula.getTitulo());
            mostrarDetallesBasicos(pelicula);
        } else {
            Log.e("MovieDetailsActivity", "Película es nula");
            Toast.makeText(this, "Película no encontrada", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar launchers
        setupLaunchers();

        // Configurar el botón SMS
        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MovieDetailsActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    seleccionarContacto();
                } else {
                    contactPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
                }
            }
        });
    }

    private void mostrarDetallesBasicos(Movie pelicula) {
        // Configuración de Retrofit y OkHttp para TMDB
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDB_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        tmdbApiService = retrofit.create(TMDBApiService.class);

        // Llamada a la API de TMDB para obtener detalles de la película
        Call<MovieDetailsResponse> call = tmdbApiService.obtenerDetallesPeliculas(
                pelicula.getId(),
                TMDB_API_KEY,
                "es-ES" // Idioma español
        );

        call.enqueue(new Callback<MovieDetailsResponse>() {
            @Override
            public void onResponse(Call<MovieDetailsResponse> call, Response<MovieDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetailsResponse detalles = response.body();

                    // Asignar los datos a los TextViews
                    txtDescripcion.setText(detalles.getDescripcion() != null ? detalles.getDescripcion() : "Descripción no disponible");
                    txtRating.setText("Rating: " + detalles.getPuntuacion());
                    txtFechaSalida.setText(detalles.getFechaSalida() != null ? formatearFecha(detalles.getFechaSalida()) : "Fecha no disponible");

                    // Cargar la imagen del póster
                    String posterPath = detalles.getRutaPoster();
                    if (posterPath != null && !posterPath.isEmpty()) {
                        String posterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                        Glide.with(MovieDetailsActivity.this)
                                .load(posterUrl)
                                .into(imageView);
                    }

                    // Guardar el título y rating para el SMS
                    movieTitle = detalles.getTitulo();
                    movieRating = String.valueOf(detalles.getPuntuacion());

                } else {
                    Log.e("MovieDetailsActivity", "Error en la respuesta de TMDB: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("MovieDetailsActivity", "Cuerpo del error: " + errorBody);
                        } catch (IOException e) {
                            Log.e("MovieDetailsActivity", "No se pudo leer el cuerpo del error: " + e.getMessage());
                        }
                    }
                    txtDescripcion.setText("Error al obtener descripción");
                    txtRating.setText("Error al obtener el rating");
                    txtFechaSalida.setText("Error al obtener fecha");
                }
            }

            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {
                Log.e("MovieDetailsActivity", "Error en la llamada a TMDB: " + t.getMessage());
                txtDescripcion.setText("Error al obtener descripción");
                txtRating.setText("Error al obtener puntuación");
                txtFechaSalida.setText("Error al obtener fecha");
            }
        });

        // Configuración básica con datos locales
        txtTitulo.setText(pelicula.getTitulo() != null ? pelicula.getTitulo() : "Título no disponible");
    }

    private String formatearFecha(String fecha) {
        try {
            // Asumiendo que la fecha viene en formato "YYYY-MM-DD"
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = formatoEntrada.parse(fecha);

            SimpleDateFormat formatoSalida = new SimpleDateFormat("'Fecha de estreno:' dd 'de' MMMM 'de' yyyy", Locale.getDefault());
            return formatoSalida.format(date);
        } catch (Exception e) {
            Log.e("MovieDetailsActivity", "Error al formatear la fecha: " + e.getMessage());
            return fecha; // Devolver la fecha sin formatear en caso de error
        }
    }

    // Configuración de los Launchers
    private void setupLaunchers() {
        // Launcher para el permiso de contactos
        contactPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        seleccionarContacto();
                    } else {
                        Toast.makeText(this, "Permiso para acceder a contactos denegado", Toast.LENGTH_SHORT).show();
                    }
                });

        // Launcher para seleccionar un contacto
        contactPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri contactUri = result.getData().getData();
                        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                        try (Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                selectedContactNumber = cursor.getString(column);

                                // Pedir permiso para enviar SMS
                                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                                    enviarSMS();
                                } else {
                                    smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
                                }
                            }
                        }
                    }
                });

        // Launcher para el permiso de SMS
        smsPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        enviarSMS();
                    } else {
                        Toast.makeText(this, "Permiso para enviar SMS denegado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Intent para seleccionar un contacto al cual enviarle el SMS
    private void seleccionarContacto() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        contactPickerLauncher.launch(intent);
    }

    // Función de enviar el SMS con la información en cuestión
    private void enviarSMS() {
        if (selectedContactNumber != null) {
            String smsBody = "Esta película te gustará: " + movieTitle + ", Rating: " + movieRating;
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + selectedContactNumber));
            smsIntent.putExtra("sms_body", smsBody);
            startActivity(smsIntent);
        } else {
            Toast.makeText(this, "No se seleccionó ningún contacto", Toast.LENGTH_SHORT).show();
        }
    }
}
