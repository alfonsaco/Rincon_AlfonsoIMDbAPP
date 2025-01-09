package edu.pruebas.rincon_alfonsoimdbapp;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import edu.pruebas.rincon_alfonsoimdbapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        // OBTENER LOS DATOS DEL INTENT
        // Se obtienen las vistas del nav_header. Se utilizará esto para acceder a los elementos
        View headerView=navigationView.getHeaderView(0);

        TextView nameTextView=headerView.findViewById(R.id.nombreEmail);
        TextView emailTextView=headerView.findViewById(R.id.email);
        ImageView profileImageView=headerView.findViewById(R.id.imagenEmail);
        // Se obtienen los datos de los intents
        String nombreUsuario=getIntent().getStringExtra("nombre");
        String emailUsuario=getIntent().getStringExtra("email");
        String imagenUsuario=getIntent().getStringExtra("imagen");

        // Ponemos los datos en los componentes
        nameTextView.setText(nombreUsuario);
        emailTextView.setText(emailUsuario);
        if (imagenUsuario != null) {
            Glide.with(this).load(imagenUsuario).into(profileImageView);
        } else {
            profileImageView.setImageResource(R.mipmap.ic_launcher_round);
        }


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}