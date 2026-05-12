package com.example.codigo_pi;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * Configuração do banco de dados local Room.
 */
@Database(entities = {Treino.class, Historico.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    // Retorna o DAO para operações no banco
    public abstract AppDao appDao();

    // Padrão Singleton para garantir uma única instância do banco
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "maya_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
