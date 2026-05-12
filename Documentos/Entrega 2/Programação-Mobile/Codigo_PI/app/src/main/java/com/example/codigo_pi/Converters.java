package com.example.codigo_pi;

import androidx.room.TypeConverter;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * Conversores de tipo para o Room persistir objetos complexos (Timestamp e Listas).
 */
public class Converters {
    
    // Converte Long (milissegundos) para Timestamp do Firebase
    @TypeConverter
    public static Timestamp fromTimestamp(Long value) {
        return value == null ? null : new Timestamp(new Date(value));
    }

    // Converte Timestamp do Firebase para Long
    @TypeConverter
    public static Long dateToTimestamp(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toDate().getTime();
    }

    // Converte String JSON para lista de exercícios de treino
    @TypeConverter
    public static List<TreinoExercicio> fromString(String value) {
        if (value == null) return null;
        Type listType = new TypeToken<List<TreinoExercicio>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    // Converte lista de exercícios de treino para String JSON
    @TypeConverter
    public static String fromList(List<TreinoExercicio> list) {
        if (list == null) return null;
        return new Gson().toJson(list);
    }
}
