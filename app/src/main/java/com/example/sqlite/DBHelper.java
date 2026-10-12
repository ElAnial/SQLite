package com.example.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "BD_INVENTARIO.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tabla ROL
        db.execSQL("CREATE TABLE rol (" +
                "id_rol INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre_rol TEXT NOT NULL UNIQUE)");

        // 2. Tabla USUARIO
        db.execSQL("CREATE TABLE usuario (" +
                "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL, " +
                "nombre_completo TEXT NOT NULL, " +
                "id_rol INTEGER NOT NULL, " +
                "activo INTEGER DEFAULT 1, " +
                "FOREIGN KEY (id_rol) REFERENCES rol(id_rol))");

        // 3. Tabla CATEGORIA
        db.execSQL("CREATE TABLE categoria (" +
                "id_categoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre_categoria TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "activo INTEGER DEFAULT 1)");

        // 4. Tabla PROVEEDOR
        db.execSQL("CREATE TABLE proveedor (" +
                "id_proveedor INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "rut_empresa TEXT NOT NULL, " +
                "razon_social TEXT NOT NULL, " +
                "contacto TEXT)");

        // 5. Tabla PRODUCTO (Evolución de entidad)
        db.execSQL("CREATE TABLE producto (" +
                "id_producto INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "sku_codigo TEXT NOT NULL UNIQUE, " +
                "nombre TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "stock_actual INTEGER NOT NULL DEFAULT 0, " +
                "stock_minimo INTEGER NOT NULL DEFAULT 0, " +
                "id_categoria INTEGER, " +
                "activo INTEGER DEFAULT 1, " +
                "FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria))");

        // 6. Database Seeding (Datos Iniciales)
        seedingDatosIniciales(db);
    }

    private void seedingDatosIniciales(SQLiteDatabase db) {
        // Insertar Roles
        db.execSQL("INSERT INTO rol (id_rol, nombre_rol) VALUES (1, 'Administrador')");
        db.execSQL("INSERT INTO rol (id_rol, nombre_rol) VALUES (2, 'Operador')");

        // Insertar Usuario Administrador por Defecto (admin / admin123)
        String passHash = hashPassword("admin123");
        ContentValues adminValues = new ContentValues();
        adminValues.put("username", "admin");
        adminValues.put("password_hash", passHash);
        adminValues.put("nombre_completo", "Administrador del Sistema");
        adminValues.put("id_rol", 1);
        adminValues.put("activo", 1);
        db.insert("usuario", null, adminValues);

        // Insertar Categorías por Defecto
        db.execSQL("INSERT INTO categoria (nombre_categoria, descripcion, activo) VALUES ('Electrónica', 'Equipos y componentes electrónicos', 1)");
        db.execSQL("INSERT INTO categoria (nombre_categoria, descripcion, activo) VALUES ('Herramientas', 'Herramientas de trabajo y mantenimiento', 1)");
        db.execSQL("INSERT INTO categoria (nombre_categoria, descripcion, activo) VALUES ('Oficina', 'Insumos y útiles de oficina', 1)");

        // Insertar Producto de Ejemplo
        ContentValues prodValues = new ContentValues();
        prodValues.put("sku_codigo", "PROD-001");
        prodValues.put("nombre", "Laptop Lenovo 15\"");
        prodValues.put("descripcion", "Intel Core i5 16GB RAM");
        prodValues.put("stock_actual", 5);
        prodValues.put("stock_minimo", 10); // Alerta de stock bajo
        prodValues.put("id_categoria", 1);
        prodValues.put("activo", 1);
        db.insert("producto", null, prodValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS producto");
        db.execSQL("DROP TABLE IF EXISTS proveedor");
        db.execSQL("DROP TABLE IF EXISTS categoria");
        db.execSQL("DROP TABLE IF EXISTS usuario");
        db.execSQL("DROP TABLE IF EXISTS rol");
        db.execSQL("DROP TABLE IF EXISTS persona");
        onCreate(db);
    }

    // Método de utilidad estático para hashear contraseñas
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // Fallback en caso imprevisto
        }
    }
}
