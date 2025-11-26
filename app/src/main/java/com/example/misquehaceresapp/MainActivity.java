package com.example.misquehaceresapp;

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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private LinearLayout contenedorPendientes, contenedorTerminadas, contenedorPrioridad;
    private EditText entradaQuehacer;
    private Button btnAgregar;

    //  FIRESTORE
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firestore en la base
        db = FirebaseFirestore.getInstance();

        //mensaje de bienvenida
        String mensaje = getIntent().getStringExtra("mensajeBienvenida");
        if (mensaje != null && !mensaje.isEmpty()) {
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
        }

        //Ref a vistas
        contenedorPendientes = findViewById(R.id.contenedorPendientes);
        contenedorTerminadas = findViewById(R.id.contenedorTerminadas);
        contenedorPrioridad = findViewById(R.id.contenedorPrioridad);
        entradaQuehacer = findViewById(R.id.entradaQuehacer);
        btnAgregar = findViewById(R.id.btnAgregar);

        //  Cargar tareas desde Firestore
        cargarTareasDesdeFirestore();

        //Botón Agregar
        btnAgregar.setOnClickListener(v -> {
            String texto = entradaQuehacer.getText().toString().trim();

            if (!texto.isEmpty()) {
                agregarTareaFirestore(texto, "pendiente");
                entradaQuehacer.setText(""); // limpiar campo
            } else {
                Toast.makeText(this,
                        getString(R.string.toast_escribe_quehacer),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =====================================================================================
    //  GUARDAR TAREA EN FIRESTORE
    // =====================================================================================
    private void agregarTareaFirestore(String texto, String estado) {
        Map<String, Object> tarea = new HashMap<>();
        tarea.put("texto", texto);
        tarea.put("estado", estado);

        db.collection("tareas")
                .add(tarea)
                .addOnSuccessListener(ref -> {
                    agregarTareaVisual(texto, estado, ref.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error guardando tarea", Toast.LENGTH_SHORT).show();
                });
    }

    // =====================================================================================
    //  CARGAR TODAS LAS TAREAS DESDE FIRESTORE
    // =====================================================================================
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

    // =====================================================================================
    // AGREGAR TAREA A LA PANTALLA
    // =====================================================================================
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

        nuevaTarea.setTag(idDocumento); // 🔥 Guardamos el ID de Firestore dentro del TextView

        nuevaTarea.setOnClickListener(v -> mostrarMenu(v, nuevaTarea));

        destino.addView(nuevaTarea);
    }

    // =====================================================================================
    // MENÚ CONTEXTUAL
    // =====================================================================================
    private void mostrarMenu(View vista, TextView tarea) {
        PopupMenu popup = new PopupMenu(this, vista);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> manejarAccionMenu(item, tarea));
        popup.show();
    }

    // =====================================================================================
    //  MANEJAR ACCIONES DEL MENÚ
    // =====================================================================================
    private boolean manejarAccionMenu(MenuItem item, TextView tarea) {

        String id = (String) tarea.getTag();

        if (id == null) return false;

        if (item.getItemId() == R.id.action_marcar_terminada) {
            actualizarEstadoFirestore(id, "terminada");
            moverTarea(tarea, contenedorTerminadas, R.color.tareaTerminada);
            return true;

        } else if (item.getItemId() == R.id.action_marcar_prioridad) {
            actualizarEstadoFirestore(id, "prioridad");
            moverTarea(tarea, contenedorPrioridad, R.color.tareaImportante);
            return true;

        } else if (item.getItemId() == R.id.action_marcar_pendiente) {
            actualizarEstadoFirestore(id, "pendiente");
            moverTarea(tarea, contenedorPendientes, R.color.tareaNormal);
            return true;

        } else if (item.getItemId() == R.id.action_eliminar) {
            eliminarTareaFirestore(id);
            ((LinearLayout) tarea.getParent()).removeView(tarea);
            Toast.makeText(this, getString(R.string.toast_tarea_eliminada), Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    // =====================================================================================
    //  ACTUALIZAR ESTADO EN FIRESTORE
    // =====================================================================================
    private void actualizarEstadoFirestore(String id, String nuevoEstado) {
        db.collection("tareas").document(id)
                .update("estado", nuevoEstado);
    }

    // =====================================================================================
    //  ELIMINAR TAREA DE FIRESTORE
    // =====================================================================================
    private void eliminarTareaFirestore(String id) {
        db.collection("tareas").document(id)
                .delete();
    }

    // =====================================================================================
    // MOVER TAREA EN PANTALLA
    // =====================================================================================
    private void moverTarea(TextView tarea, LinearLayout nuevoContenedor, int color) {
        ((LinearLayout) tarea.getParent()).removeView(tarea);
        tarea.setTextColor(ContextCompat.getColor(this, color));
        nuevoContenedor.addView(tarea);
    }
}












