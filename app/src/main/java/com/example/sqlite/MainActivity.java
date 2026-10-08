package com.example.sqlite;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // 🔹 Declaración de los campos de texto y botones
    private EditText txtNombre, txtApellido, txtEdad;
    private Button btnGuardar, btnVer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Carga el diseño XML principal de la app

        // Se enlazan los elementos del XML con las variables del código
        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtEdad = findViewById(R.id.txtEdad);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVer = findViewById(R.id.btnVer);

        //  Acción del botón "Guardar" — inserta un registro en la base de datos
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtiene los valores escritos por el usuario
                String nombre = txtNombre.getText().toString().trim();
                String apellido = txtApellido.getText().toString().trim();
                String edad = txtEdad.getText().toString().trim();

                // Verifica que todos los campos estén llenos antes de guardar
                if (nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // 🔹 Se crea una instancia del DBHelper, que administra la base de datos SQLite
                    DBHelper dbHelper = new DBHelper(MainActivity.this);

                    // 🔹 Se obtiene una base de datos en modo escritura
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    // 🔹 Sentencia SQL parametrizada (segura contra inyección de código)
                    db.execSQL("INSERT INTO persona (nombre, apellido, edad) VALUES (?, ?, ?)",
                            new Object[]{nombre, apellido, Integer.parseInt(edad)});

                    // 🔹 Se cierra la conexión después de insertar
                    db.close();

                    // Muestra confirmación al usuario
                    Toast.makeText(MainActivity.this, "Registro guardado correctamente.", Toast.LENGTH_SHORT).show();

                    // Limpia los campos del formulario para ingresar un nuevo registro
                    txtNombre.setText("");
                    txtApellido.setText("");
                    txtEdad.setText("");
                    txtNombre.requestFocus();

                } catch (Exception e) {
                    // 🔹 Captura y muestra cualquier error durante la operación
                    Toast.makeText(MainActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        //  Acción del botón "Ver" — abre la Activity que muestra los registros
        btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent explícito para abrir la Activity Leer
                Intent intent = new Intent(MainActivity.this, Leer.class);
                startActivity(intent); // Lanza la nueva pantalla
            }
        });
    }
}