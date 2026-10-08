package com.example.sqlite;

// Importa las clases necesarias para gestionar SQLite
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    //  Nombre físico del archivo de la base de datos y su versión
    private static final String DB_NAME = "BD_EJEMPLO.db";
    private static final int DB_VERSION = 1;

    //  Constructor: recibe el contexto (la Activity o app que la usa)
    public DBHelper(Context context) {
        // Llama al constructor de la clase padre SQLiteOpenHelper
        // Si la base de datos no existe, Android la crea automáticamente
        super(context, DB_NAME, null, DB_VERSION);
    }

    //  Método que se ejecuta UNA SOLA VEZ al crear la base de datos por primera vez
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crea la tabla "persona" con sus columnas y restricciones
        db.execSQL("CREATE TABLE persona (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + // ID autoincremental
                "nombre TEXT NOT NULL, " +                  // Nombre obligatorio
                "apellido TEXT NOT NULL, " +                // Apellido obligatorio
                "edad INTEGER NOT NULL)");                  // Edad obligatoria (numérica)
    }

    //  Método que se ejecuta cuando cambias la versión de la base de datos (DB_VERSION)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Si hay una nueva versión, elimina la tabla anterior y la vuelve a crear
        db.execSQL("DROP TABLE IF EXISTS persona");
        onCreate(db);
    }
}