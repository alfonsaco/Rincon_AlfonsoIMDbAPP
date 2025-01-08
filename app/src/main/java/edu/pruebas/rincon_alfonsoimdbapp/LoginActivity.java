package edu.pruebas.rincon_alfonsoimdbapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Configurar Firebase y Google SignIn
        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        // Obtenemos el botón, y cambiamos su texto
        SignInButton signInButton = findViewById(R.id.btnSignIn);
        ((TextView) signInButton.getChildAt(0)).setText("Sign in with Google");

        // Configurar el clic en el botón
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                googleSignInLauncher.launch(signInIntent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Se comprueba si el usuario ha iniciado sesión
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            irAMainActivity();
        }
    }

    // Launcher para comenzar el inicio de sesión
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                GoogleSignInAccount account = signInTask.getResult(ApiException.class);
                                if (account != null) {
                                    autentificarFirebase(account.getIdToken());
                                }
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, "Error durante el inicio de sesión", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
    );

    // Autenticación con Firebase
    private void autentificarFirebase(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        irAMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "La autentificación ha fallado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Volvemos a la MainActivity tras aber iniciado sesión de forma correcta
    private void irAMainActivity() {
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}


