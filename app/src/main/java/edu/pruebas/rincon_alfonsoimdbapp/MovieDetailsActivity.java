package edu.pruebas.rincon_alfonsoimdbapp;

import android.app.Activity;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import edu.pruebas.rincon_alfonsoimdbapp.api.IMDBApiService;
import edu.pruebas.rincon_alfonsoimdbapp.database.FavoritesManager;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;
import edu.pruebas.rincon_alfonsoimdbapp.models.MovieOverviewResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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

    private FavoritesManager favoritesManager;

    private static final String BASE_URL = "https://imdb-com.p.rapidapi.com/";
    private IMDBApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Inicializar
        imageView = findViewById(R.id.imageView);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtFechaSalida = findViewById(R.id.txtFechaSalida);
        txtRating = findViewById(R.id.txtRating);
        btnSMS=findViewById(R.id.btnSMS);

        // Se recibe la película desde el Intent
        Intent intent=getIntent();
        Movie pelicula=intent.getParcelableExtra("pelicula");

        if (pelicula != null) {
            mostrarDetallesBasicos(pelicula);
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

        // Obtener el ID de usuario que esta usando la app. Con esto, podremos guardar los favoritos
        // para cada usuario
        FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                favoritesManager.addFavorite(userId, pelicula);
                Toast.makeText(MovieDetailsActivity.this, "Película añadida a favoritos", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }

    private void mostrarDetallesBasicos(Movie pelicula) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request modifiedRequest = chain.request().newBuilder()
                            .addHeader("X-RapidAPI-Key", "bdb1444c4amshd033444ce845bbbp12ff63jsn7bf5fe5f9fab")
                            .addHeader("X-RapidAPI-Host", "imdb-com.p.rapidapi.com").build();
                    return chain.proceed(modifiedRequest);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://imdb-com.p.rapidapi.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(IMDBApiService.class);

        Call<MovieOverviewResponse> call = api.obtencionDatos(pelicula.getId());
        call.enqueue(new Callback<MovieOverviewResponse>() {
            @Override
            public void onResponse(Call<MovieOverviewResponse> call, Response<MovieOverviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieOverviewResponse.Data data = response.body().getData();
                    if (data != null) {
                        MovieOverviewResponse.Title title = data.getTitle();
                        if (title != null) {
                            // Obtener descripción
                            if (title.getPlot() != null && title.getPlot().getPlotText() != null) {
                                String descripcion = title.getPlot().getPlotText().getPlainText();
                                txtDescripcion.setText(descripcion != null ? descripcion : "Descripción no disponible");
                            } else {
                                txtDescripcion.setText("Descripción no disponible");
                            }

                            // Obtener rating
                            if (title.getRatingsSummary() != null) {
                                double rating = title.getRatingsSummary().getAggregateRating();
                                txtRating.setText("Rating: " + rating);
                            } else {
                                txtRating.setText("Puntuación no disponible");
                            }
                        }
                    }

                } else {
                    Log.e("API Response", "Error en la respuesta: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("API Error Body", errorBody);
                        } catch (IOException e) {
                            Log.e("API Error", "No se pudo leer el cuerpo del error: " + e.getMessage());
                        }
                    }
                    txtDescripcion.setText("Error al obtener descripción");
                    txtRating.setText("Error al obtener el rating");
                }
            }

            @Override
            public void onFailure(Call<MovieOverviewResponse> call, Throwable t) {
                Log.e("API Error", "Error en la llamada API: " + t.getMessage());
                txtDescripcion.setText("Error al obtener descripción");
                txtRating.setText("Error al obtener puntuación");
            }
        });

        // Configuración básica con datos locales
        txtTitulo.setText(pelicula.getTitulo() != null ? pelicula.getTitulo() : "Título no disponible");
        movieTitle = pelicula.getTitulo();
        txtFechaSalida.setText(pelicula.getFechaSalida() != null ? pelicula.getFechaSalida() : "Fecha no disponible");

        if (pelicula.getRutaPoster() != null) {
            Glide.with(this).load(pelicula.getRutaPoster()).into(imageView);
        }
    }



    private void obtenerDetallesAdicionales(String movieId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IMDBApiService apiService = retrofit.create(IMDBApiService.class);

        Call<MovieOverviewResponse> call = apiService.obtencionDatos(movieId);
        call.enqueue(new Callback<MovieOverviewResponse>() {
            @Override
            public void onResponse(Call<MovieOverviewResponse> call, Response<MovieOverviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieOverviewResponse movieDetails = response.body();

                    String descripcion = movieDetails.getData().getTitle().getPlot().getPlotText().toString();
                    txtDescripcion.setText(descripcion);

                    if (movieDetails.getData().getTitle().getRatingsSummary() != null) {
                        txtRating.setText(String.valueOf(movieDetails.getData().getTitle().getRatingsSummary().getAggregateRating()));
                    } else {
                        txtRating.setText("Puntuación no disponible");
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieOverviewResponse> call, Throwable t) {
                txtDescripcion.setText("No se pudo obtener la descripción");
                txtRating.setText("No se pudo obtener la puntuación");
                Log.e("API Error", "Error en la llamada a la API", t);
            }
        });
    }

    // Configuración de los Launchers
    private void setupLaunchers() {
        // Launcher para el permiso de contactos
        contactPermissionLauncher=registerForActivityResult(
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
        Intent intent=new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
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
