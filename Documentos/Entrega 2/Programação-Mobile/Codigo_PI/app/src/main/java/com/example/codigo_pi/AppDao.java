package com.example.codigo_pi;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

/**
 * Interface de acesso a dados (DAO) para o Room.
 */
@Dao
public interface AppDao {
    // Insere ou atualiza uma lista de treinos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTreinos(List<Treino> treinos);

    // Recupera treinos de um usuário específico ordenados por data
    @Query("SELECT * FROM treinos WHERE usuarioId = :userId ORDER BY dataCriacao DESC")
    List<Treino> getTreinosByUserId(String userId);

    // Insere um único registro de histórico
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistorico(Historico historico);

    // Insere uma lista de registros de histórico
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistoricos(List<Historico> historicos);

    // Recupera o histórico de um usuário ordenado por data de conclusão
    @Query("SELECT * FROM historico WHERE usuario_id = :userId ORDER BY data_conclusao DESC")
    List<Historico> getHistoricoByUserId(String userId);
}
