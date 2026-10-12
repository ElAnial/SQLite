package com.example.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class CategoriasActivity extends AppCompatActivity {

    private TextInputEditText etNombreCategoria, etDescripcionCategoria;
    private MaterialButton btnGuardarCategoria;
    private ListView lstCategorias;

    private DBHelper dbHelper;
    private ArrayList<String> listaCategoriasString;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        dbHelper = new DBHelper(this);

        etNombreCategoria = findViewById(R.id.etNombreCategoria);
        etDescripcionCategoria = findViewById(R.id.etDescripcionCategoria);
        btnGuardarCategoria = findViewById(R.id.btnGuardarCategoria);
        lstCategorias = findViewById(R.id.lstCategorias);

        listaCategoriasString = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaCategoriasString);
        lstCategorias.setAdapter(adapter);

        btnGuardarCategoria.setOnClickListener(v -> guardarCategoria());

        cargarCategorias();
    }

    private void guardarCategoria() {
        String nombre = etNombreCategoria.getText() != null ? etNombreCategoria.getText().toString().trim() : "";
        String descripcion = etDescripcionCategoria.getText() != null ? etDescripcionCategoria.getText().toString().trim() : "";

        if (TextUtils.isEmpty(nombre)) {
            etNombreCategoria.setError("Ingrese el nombre de la categoría");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre_categoria", nombre);
        values.put("descripcion", descripcion);
        values.put("activo", 1);

        try {
            long id = db.insert("categoria", null, values);
            if (id != -1) {
                Toast.makeText(this, "Categoría registrada correctamente (CU-09)", Toast.LENGTH_SHORT).show();
                etNombreCategoria.setText("");
                etDescripcionCategoria.setText("");
                etNombreCategoria.clearFocus();
                cargarCategorias(); // Recargar lista
            } else {
                Toast.makeText(this, "Error al registrar la categoría", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }

    private void cargarCategorias() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        listaCategoriasString.clear();

        String sql = "SELECT id_categoria, nombre_categoria, descripcion FROM categoria WHERE activo = 1 ORDER BY nombre_categoria ASC";

        try (Cursor c = db.rawQuery(sql, null)) {
            if (c.moveToFirst()) {
                int idIdx = c.getColumnIndexOrThrow("id_categoria");
                int nombreIdx = c.getColumnIndexOrThrow("nombre_categoria");
                int descIdx = c.getColumnIndexOrThrow("descripcion");

                do {
                    int id = c.getInt(idIdx);
                    String nombre = c.getString(nombreIdx);
                    String desc = c.getString(descIdx);

                    String item = id + " - " + nombre + (TextUtils.isEmpty(desc) ? "" : " (" + desc + ")");
                    listaCategoriasString.add(item);
                } while (c.moveToNext());
            }

            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar categorías: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }
}
