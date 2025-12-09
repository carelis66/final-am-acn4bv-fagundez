package com.example.misquehaceresapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText entradaEmail, entradaPassword;
    private Button btnLogin, btnRegistro;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        // con usuario logueado caigo directamente al MainActivity
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            irAlMain(user.getEmail());
            return;
        }

        entradaEmail = findViewById(R.id.entradaEmail);
        entradaPassword = findViewById(R.id.entradaPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistro = findViewById(R.id.btnRegistro);

        btnLogin.setOnClickListener(v -> loginUsuario());
        btnRegistro.setOnClickListener(v -> registrarUsuario());
    }

    private void loginUsuario() {
        String email = entradaEmail.getText().toString().trim();
        String password = entradaPassword.getText().toString().trim();

        if (!validarCampos(email, password)) return;

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    irAlMain(email);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void registrarUsuario() {
        String email = entradaEmail.getText().toString().trim();
        String password = entradaPassword.getText().toString().trim();

        if (!validarCampos(email, password)) return;

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                    irAlMain(email);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private boolean validarCampos(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void irAlMain(String email) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("mensajeBienvenida", "¡Bienvenida, " + email + "!");
        startActivity(intent);
        finish();
    }
}
