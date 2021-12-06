package com.example.proyecto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    Button btnRegister, btnLogin, btnLoginWithGoogle;
    EditText etEmail, etPassword;
    ConstraintLayout authLayout;

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        authLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authLayout = findViewById(R.id.authLayout);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginWithGoogle = findViewById(R.id.btnLoginWithGoogle);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        comprobarLogin();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logWithEmail();
            }
        });

        btnLoginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logWithGoogle();
            }
        });
    }

    private void logWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("689333673487-vfch3r593flrpmbaemmoj8cfsgooi0d1.apps.googleusercontent.com")
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn();
    }

    private void signIn() {
        Intent signInTent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInTent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i("FIREBASE LOGIN GOOGLE", account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e){
                Log.i("FIREBASE LOGIN GOOGLE", "Fallo al iniciar sesión", task.getException());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential ac = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(ac).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.i("FIREBASE LOGIN GOOGLE", "Inicio sesión correcto");
                    FirebaseUser user = mAuth.getCurrentUser();
                    goMain(user.getEmail());
                } else {
                    Log.i("FIREBASE LOGIN GOOGLE", "Inicio sesión incorrecto", task.getException());
                }
            }
        });
    }

    private void logWithEmail() {
        String email = etEmail.getText().toString();
        String pwd = etPassword.getText().toString();

        if (!email.isEmpty() && !pwd.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i("FIREBASE LOGIN", "Login correcto");
                        FirebaseUser user = mAuth.getCurrentUser();
                        goMain(email);
                    } else {
                        Log.i("FIREBASE LOGIN", "Login incorrecto", task.getException());
                        Toast.makeText(LoginActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Debes rellenar los 2 campos para continuar", Toast.LENGTH_SHORT).show();
        }
    }

    private void registrarUsuario() {
        String email = etEmail.getText().toString();
        String pwd = etPassword.getText().toString();

        if (!email.isEmpty() && !pwd.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.i("FIREBASE REGISTER", "Se ha registrado correctamente");
                        Toast.makeText(LoginActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                    } else {
                        Log.i("FIREBASE REGISTER", "Ha ocurrido un error al registrar", task.getException());
                        Toast.makeText(LoginActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Debe rellenar los 2 campos para registrarse", Toast.LENGTH_SHORT).show();
            Log.i("FIREBASE REGISTER", "No se han rellenado los 2 campos");
        }
    }

    private void comprobarLogin() {
        SharedPreferences sesion = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        String email = sesion.getString("email", null);
        if (email != null) {
            authLayout.setVisibility(View.INVISIBLE);
            goMain(email);
        }
    }

    private void goMain(String email) {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("email", email);
        startActivity(i);
    }
}