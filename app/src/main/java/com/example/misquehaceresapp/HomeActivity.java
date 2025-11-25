package com.example.misquehaceresapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Referencias a los botones
        Button btnIrTareas = findViewById(R.id.btnIrTareas);
        Button btnAcerca = findViewById(R.id.btnAcerca);

        // Ir a la pantalla de tareas (MainActivity original)
        btnIrTareas.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            // EXTRA que enviamos a MainActivity
            intent.putExtra("mensajeBienvenida", getString(R.string.toast_bienvenida));
            startActivity(intent);
        });


        // Ir a la pantalla acerca de la app
        btnAcerca.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AcercaActivity.class);
            startActivity(intent);
        });
    }
}
