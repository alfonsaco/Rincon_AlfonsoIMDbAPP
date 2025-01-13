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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import edu.pruebas.rincon_alfonsoimdbapp.api.IMDBApiService;
import edu.pruebas.rincon_alfonsoimdbapp.api.TMDBApiService;
import edu.pruebas.rincon_alfonsoimdbapp.models.Movie;
import edu.pruebas.rincon_alfonsoimdbapp.models.MovieDetailsResponse;
import edu.pruebas.rincon_alfonsoimdbapp.models.MovieOverviewResponse;
import edu.pruebas.rincon_alfonsoimdbapp.utils.Constants;
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

    // URL y Keys de las API
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String TMDB_API_KEY = "aaf2cf26c82660c7a38d10d55ed5c92d";
    private static final String IMD_BASE_URL = "https://imdb-com.p.rapidapi.com/";

    private TMDBApiService tmdbApiService;
    private IMDBApiService imdbApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        imageView = findViewById(R.id.imageView);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtFechaSalida = findViewById(R.id.txtFechaSalida);
        txtRating = findViewById(R.id.txtRating);
        btnSMS = findViewById(R.id.btnSMS);

        // Recibimos la película con un Intent
        Intent intent = getIntent();
        Movie pelicula = intent.getParcelableExtra("pelicula");
        String source = intent.getStringExtra("source");
        // Se verifica que la película haya sido recibida
        if (pelicula != null && source != null) {
            mostrarDetallesBasicos(pelicula, source);
        } else {
            Toast.makeText(this, "Contenido no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }

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

    // Método para mostrar los detalles de cad apelícula
    private void mostrarDetallesBasicos(Movie pelicula, String source) {
        if (source.equals(Constants.SOURCE_TMDB)) {
            obtenerDetallesTMDB(pelicula);
        } else if (source.equals(Constants.SOURCE_IMD)) {
            obtenerDetallesIMD(pelicula);
        } else {
            Toast.makeText(this, "Contenido desconocido", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configuración básica con datos locales
        txtTitulo.setText(pelicula.getTitulo() != null ? pelicula.getTitulo() : "Título no disponible");
    }

    // Método para obtener los detalles de la película de la API de TMDB
    private void obtenerDetallesTMDB(Movie pelicula) {
        //  Retrofit y OkHttp para TMDB
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
                "es-ES"
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
                    manejarErrorDetalles();
                }
            }

            @Override
            public void onFailure(Call<MovieDetailsResponse> call, Throwable t) {
                Log.e("MovieDetailsActivity", "Error en la llamada a TMDB: " + t.getMessage());
                manejarErrorDetalles();
            }
        });
    }

    // Método para obtener los detalles de la película de la API de IMD
    private void obtenerDetallesIMD(Movie serie) {
        //  Retrofit y OkHttp para IMD
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request modifiedRequest = chain.request().newBuilder()
                            .addHeader("X-RapidAPI-Key", "bdb1444c4amshd033444ce845bbbp12ff63jsn7bf5fe5f9fab")
                            .addHeader("X-RapidAPI-Host", "imdb-com.p.rapidapi.com")
                            .build();
                    return chain.proceed(modifiedRequest);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IMD_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        imdbApiService = retrofit.create(IMDBApiService.class);

        // Llamada a la API de IMD para obtener detalles de la serie o película
        Call<MovieOverviewResponse> call = imdbApiService.obtencionDatos(serie.getId());

        call.enqueue(new Callback<MovieOverviewResponse>() {
            @Override
            public void onResponse(Call<MovieOverviewResponse> call, Response<MovieOverviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieOverviewResponse detalles = response.body();

                    // Asignar los datos a los TextViews
                    if (detalles.getData() != null && detalles.getData().getTitle() != null) {
                        MovieOverviewResponse.Title titulo = detalles.getData().getTitle();

                        // Asignar descripción
                        if (titulo.getPlot() != null && titulo.getPlot().getPlotText() != null) {
                            txtDescripcion.setText(titulo.getPlot().getPlotText().getPlainText());
                        } else {
                            txtDescripcion.setText("Descripción no disponible");
                        }

                        // Asignar rating
                        if (titulo.getRatingsSummary() != null) {
                            txtRating.setText("Rating: " + titulo.getRatingsSummary().getAggregateRating());
                            movieRating = String.valueOf(titulo.getRatingsSummary().getAggregateRating());
                        } else {
                            txtRating.setText("Rating: No disponible");
                            movieRating = "No disponible";
                        }

                        // Asignar fecha de salida
                        if (titulo.getReleaseDate() != null) {
                            String fecha = String.format("%04d-%02d-%02d",
                                    titulo.getReleaseDate().getYear(),
                                    titulo.getReleaseDate().getMonth(),
                                    titulo.getReleaseDate().getDay());
                            txtFechaSalida.setText(formatearFecha(fecha));
                        } else {
                            txtFechaSalida.setText("Fecha no disponible");
                        }

                        // Asignar título
                        if (titulo.getTitleText() != null) {
                            txtTitulo.setText(titulo.getTitleText().getText());
                            movieTitle = titulo.getTitleText().getText();
                        } else {
                            txtTitulo.setText("Título no disponible");
                            movieTitle = "Título no disponible";
                        }

                        // Cargar la imagen del póster
                        if (titulo.getPrimaryImage() != null && titulo.getPrimaryImage().getUrl() != null) {
                            String posterUrl = titulo.getPrimaryImage().getUrl();
                            if (!posterUrl.startsWith("http://") && !posterUrl.startsWith("https://")) {
                                posterUrl = "https://image.imdb.com" + posterUrl;
                            }
                            Glide.with(MovieDetailsActivity.this)
                                    .load(posterUrl)
                                    .into(imageView);
                        }

                    } else {
                        Log.e("MovieDetailsActivity", "Datos de IMD son nulos o incompletos.");
                        manejarErrorDetalles();
                    }

                } else {
                    Log.e("MovieDetailsActivity", "Error en la respuesta de IMD: " + response.code());
                    manejarErrorDetalles();
                }
            }

            @Override
            public void onFailure(Call<MovieOverviewResponse> call, Throwable t) {
                Log.e("MovieDetailsActivity", "Error en la llamada a IMD: " + t.getMessage());
                manejarErrorDetalles();
            }
        });
    }

    // Método para cambiar el contenido de los txt en caso de error
    private void manejarErrorDetalles() {
        txtDescripcion.setText("Error al obtener descripción");
        txtRating.setText("Error al obtener puntuación");
        txtFechaSalida.setText("Error al obtener fecha");
    }

    // Método para cambiar el formato de la fecha
    private String formatearFecha(String fecha) {
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = formatoEntrada.parse(fecha);

            SimpleDateFormat formatoSalida = new SimpleDateFormat("'Fecha de estreno:' dd 'de' MMMM 'de' yyyy", Locale.getDefault());
            return formatoSalida.format(date);
        } catch (Exception e) {
            Log.e("MovieDetailsActivity", "Error al formatear la fecha: " + e.getMessage());
            return fecha;
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
            String smsBody = "Este contenido te gustará: " + movieTitle + ", Rating: " + movieRating;
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + selectedContactNumber));
            smsIntent.putExtra("sms_body", smsBody);
            startActivity(smsIntent);
        } else {
            Toast.makeText(this, "No se seleccionó ningún contacto", Toast.LENGTH_SHORT).show();
        }
    }
}
