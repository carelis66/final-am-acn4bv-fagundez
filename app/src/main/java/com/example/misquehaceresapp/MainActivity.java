package com.example.misquehaceresapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout contenedorPendientes, contenedorTerminadas, contenedorPrioridad;
    private EditText entradaQuehacer;
    private Button btnAgregar, btnPerfil;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ------------------ PROTEGER SI NO HAY LOGIN ------------------
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Si vino con mensaje de bienvenida lo mostramos
        String mensaje = getIntent().getStringExtra("mensajeBienvenida");
        if (mensaje != null && !mensaje.isEmpty()) {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }

        // ------------------ FIRESTORE ------------------
        db = FirebaseFirestore.getInstance();

        contenedorPendientes = findViewById(R.id.contenedorPendientes);
        contenedorTerminadas = findViewById(R.id.contenedorTerminadas);
        contenedorPrioridad = findViewById(R.id.contenedorPrioridad);
        entradaQuehacer = findViewById(R.id.entradaQuehacer);
        btnAgregar = findViewById(R.id.btnAgregar);

        btnPerfil = findViewById(R.id.btnPerfil);
        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        // ------------------ CARGAR TAREAS ------------------
        cargarTareasDesdeFirestore();

        // ------------------ AGREGAR TAREA ------------------
        btnAgregar.setOnClickListener(v -> {
            String texto = entradaQuehacer.getText().toString().trim();

            if (!texto.isEmpty()) {
                agregarTareaFirestore(texto, "pendiente");
                entradaQuehacer.setText("");
            } else {
                Toast.makeText(this, getString(R.string.toast_escribe_quehacer), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= FIRESTORE → GUARDAR =================
    private void agregarTareaFirestore(String texto, String estado) {
        Map<String, Object> tarea = new HashMap<>();
        tarea.put("texto", texto);
        tarea.put("estado", estado);

        db.collection("tareas")
                .add(tarea)
                .addOnSuccessListener(ref -> agregarTareaVisual(texto, estado, ref.getId()))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error guardando tarea", Toast.LENGTH_SHORT).show()
                );
    }

    // ================= FIRESTORE → CARGAR =================
    private void cargarTareasDesdeFirestore() {
        db.collection("tareas")
                .get()
                .addOnSuccessListener(snap -> {
                    for (QueryDocumentSnapshot doc : snap) {
                        String texto = doc.getString("texto");
                        String estado = doc.getString("estado");

                        if (texto != null && estado != null) {
                            agregarTareaVisual(texto, estado, doc.getId());
                        }
                    }
                });
    }

    // ================= AGREGAR VISUAL =================
    private void agregarTareaVisual(String texto, String estado, String idDocumento) {

        LinearLayout destino;
        int color;

        switch (estado) {
            case "terminada":
                destino = contenedorTerminadas;
                color = R.color.tareaTerminada;
                break;
            case "prioridad":
                destino = contenedorPrioridad;
                color = R.color.tareaImportante;
                break;
            default:
                destino = contenedorPendientes;
                color = R.color.tareaNormal;
        }

        TextView nuevaTarea = new TextView(this);
        nuevaTarea.setText("• " + texto);
        nuevaTarea.setTextSize(16f);
        nuevaTarea.setPadding(8, 8, 8, 8);
        nuevaTarea.setTextColor(ContextCompat.getColor(this, color));
        nuevaTarea.setTag(idDocumento);

        nuevaTarea.setOnClickListener(v -> mostrarMenu(v, nuevaTarea));

        destino.addView(nuevaTarea);
    }

    private void mostrarMenu(View vista, TextView tarea) {
        PopupMenu popup = new PopupMenu(this, vista);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> manejarAccionMenu(item, tarea));
        popup.show();
    }

    // ======= AQUÍ VA LA VERSIÓN **SIN SWITCH**, SOLO IF/ELSE =======
    private boolean manejarAccionMenu(MenuItem item, TextView tarea) {

        String id = (String) tarea.getTag();
        if (id == null) return false;

        int itemId = item.getItemId();

        if (itemId == R.id.action_marcar_terminada) {

            actualizarEstadoFirestore(id, "terminada");
            moverTarea(tarea, contenedorTerminadas, R.color.tareaTerminada);
            return true;

        } else if (itemId == R.id.action_marcar_prioridad) {

            actualizarEstadoFirestore(id, "prioridad");
            moverTarea(tarea, contenedorPrioridad, R.color.tareaImportante);
            return true;

        } else if (itemId == R.id.action_marcar_pendiente) {

            actualizarEstadoFirestore(id, "pendiente");
            moverTarea(tarea, contenedorPendientes, R.color.tareaNormal);
            return true;

        } else if (itemId == R.id.action_eliminar) {

            eliminarTareaFirestore(id);
            ((LinearLayout) tarea.getParent()).removeView(tarea);
            Toast.makeText(this, getString(R.string.toast_tarea_eliminada), Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    private void actualizarEstadoFirestore(String id, String nuevoEstado) {
        db.collection("tareas").document(id).update("estado", nuevoEstado);
    }

    private void eliminarTareaFirestore(String id) {
        db.collection("tareas").document(id).delete();
    }

    private void moverTarea(TextView tarea, LinearLayout nuevoContenedor, int color) {
        ((LinearLayout) tarea.getParent()).removeView(tarea);
        tarea.setTextColor(ContextCompat.getColor(this, color));
        nuevoContenedor.addView(tarea);
    }
}
