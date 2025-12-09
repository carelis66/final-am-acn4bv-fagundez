package com.example.misquehaceresapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilActivity extends AppCompatActivity {

    private TextView textoBienvenida, textoEmail;
    private ImageView imagenPerfil;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        textoBienvenida = findViewById(R.id.textoBienvenida);
        textoEmail = findViewById(R.id.textoEmail);
        imagenPerfil = findViewById(R.id.imagenPerfil);
        btnLogout = findViewById(R.id.btnLogout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            textoBienvenida.setText("¡Hola!");
            textoEmail.setText(email);

            // Imagen desde URL
            String urlImagen = "https://media.licdn.com/dms/image/v2/D4D03AQHNaIIb8eJqOg/profile-displayphoto-scale_200_200/B4DZh8nqBVHwAY-/0/1754437429021?e=2147483647&v=beta&t=hA0EnzrCHPeI6rPEUleXQf4ESqGrNiTYms3uHj0Smuc";
            Glide.with(this)
                    .load(urlImagen)
                    .circleCrop()
                    .into(imagenPerfil);
        }

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
