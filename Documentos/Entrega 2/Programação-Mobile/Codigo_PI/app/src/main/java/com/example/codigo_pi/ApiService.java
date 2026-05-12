package com.example.codigo_pi;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/usuarios/{id}")
    Call<Usuario> getUsuario(@Path("id") String id);

    @POST("api/usuarios")
    Call<Usuario> salvarUsuario(@Body Usuario usuario);

    @GET("api/treinos/usuario/{usuarioId}")
    Call<List<Treino>> getTreinos(@Path("usuarioId") String usuarioId);

    @POST("api/treinos")
    Call<Treino> salvarTreino(@Body Treino treino);

    @GET("api/historico/usuario/{usuarioId}")
    Call<List<Historico>> getHistorico(@Path("usuarioId") String usuarioId);

    @POST("api/historico")
    Call<Historico> salvarHistorico(@Body Historico historico);
}
