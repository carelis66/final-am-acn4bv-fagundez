**Parcial 2 - Aplicaciones Móviles (Escuela Da Vinci)** Comisión [ACN4BV]
- Aplicación móvil en **Java con Android Studio**:para la gestión de tareas y quehaceres diarios.
📱 MisQuehaceresApp
- **Carelis Fagúndez Frías**
- Dni: 19094687.
- Prof. Sergio Medina
---
# README — MisQuehaceresApp (Android + Firebase Firestore)

## Descripción del Proyecto

MisQuehaceresApp es una aplicación móvil desarrollada en Android Studio que permite gestionar tareas diarias de forma simple y visual. La aplicación permite:

* Agregar tareas
* Mover tareas entre categorías (Pendientes, Prioridad, Terminadas)
* Eliminar tareas
* Mostrar mensajes de bienvenida
* Navegar entre pantallas
* Persistir todas las tareas en Firebase Firestore

El proyecto combina diseño XML, lógica en Java y persistencia en la nube mediante Firebase.

---

## Características Principales

### Pantalla de Inicio

* Botón para acceder a la gestión de tareas
* Botón para ver información acerca de la aplicación

### Gestión de Tareas

* Crear tareas dinámicamente desde un campo de texto
* Menú contextual en cada tarea:

  * Marcar como terminada
  * Marcar como pendiente
  * Marcar como importante
  * Eliminar tarea
* Interfaz basada en LinearLayouts y ConstraintLayouts
* Diseño organizado mediante recursos de colores, dimensiones y strings

### Persistencia en Firebase Firestore

Todas las tareas se guardan y cargan desde la nube.
La aplicación realiza:

* Guardado automático al crear una tarea
* Actualización de estado al moverla entre categorías
* Eliminación sincronizada con la base de datos
* Lectura completa de tareas al iniciar la actividad principal

Cada documento Firestore contiene la siguiente estructura:

```
{
  "texto": "Comprar pan",
  "estado": "pendiente"
}
```

---

## Tecnologías Utilizadas

| Tecnología                 | Uso                               |
| -------------------------- | --------------------------------- |
| Java                       | Lógica principal de la aplicación |
| XML                        | Diseño de interfaz                |
| Android Studio             | Entorno de desarrollo             |
| Firebase Firestore         | Base de datos NoSQL               |
| Firebase Google Services   | Integración de servicios          |
| Material Design Components | Identidad visual moderna          |

---


## Configuración de Firebase

### 1. Crear proyecto en Firebase Console

### 2. Registrar la aplicación Android

* Nombre del paquete:
  `com.example.misquehaceresapp`
* Descargar `google-services.json`
* Colocarlo en la carpeta:
  `app/google-services.json`


---

## Funcionamiento con Firestore

### Agregar una tarea

* Se sube a Firestore.
* Se muestra en pantalla solo si la operación se completa exitosamente.

### Cargar tareas al abrir la app

* Se consulta la colección `tareas` en Firestore.
* Se reconstruye la interfaz dinámica con elementos generados en Java.

### Mover una tarea de categoría

* Se actualiza el estado en Firestore.
* Se mueve visualmente en la interfaz.

### Eliminar una tarea

* Se borra el documento correspondiente en Firestore.
* Se remueve el elemento visual.

---

## Pantallas Incluidas

### HomeActivity

Pantalla de bienvenida y navegación principal.

### MainActivity

Pantalla de gestión de tareas con integración completa a Firestore.

### AcercaActivity

Pantalla con información de autor, descripción y versión de la app.

---

## Cómo ejecutar el proyecto

1. Clonar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Vincular la aplicación con Firebase y colocar el archivo `google-services.json` propio.
4. Sincronizar Gradle.
5. Ejecutar con el emulador.

---
