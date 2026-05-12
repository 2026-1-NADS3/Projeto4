package com.example.codigo_pi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * Tela que exibe a lista completa de exercícios disponíveis na biblioteca.
 */
public class BibliotecaExerciciosActivity extends AppCompatActivity {

    private RecyclerView rvBiblioteca;
    private ExercicioBibliotecaAdapter adapter;
    private List<Exercicio> exercicioList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca_exercicios);

        db = FirebaseFirestore.getInstance();
        rvBiblioteca = findViewById(R.id.rv_biblioteca);
        exercicioList = new ArrayList<>();

        // Configuração do Adapter com ação de clique para abrir detalhes do exercício
        adapter = new ExercicioBibliotecaAdapter(exercicioList, exercicio -> {
            Intent intent = new Intent(BibliotecaExerciciosActivity.this, TelaExercicios.class);
            intent.putExtra("EXERCICIO_ID", exercicio.getId());
            startActivity(intent);
        });

        rvBiblioteca.setLayoutManager(new LinearLayoutManager(this));
        rvBiblioteca.setAdapter(adapter);

        loadExercicios();
        setupNavigation();
    }

    /**
     * Carrega a lista de exercícios cadastrados no Firestore.
     */
    private void loadExercicios() {
        db.collection("planos_exercicios")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        exercicioList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Exercicio ex = document.toObject(Exercicio.class);
                            ex.setId(document.getId());
                            exercicioList.add(ex);
                        }
                        adapter.notifyDataSetChanged();

                        if (exercicioList.isEmpty()) {
                            Toast.makeText(this, "Biblioteca vazia. Abra a Home para gerar os dados.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.e("Firestore", "Erro ao carregar", task.getException());
                    }
                });
    }

    /**
     * Configura a navegação da barra inferior.
     */
    private void setupNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, homeMaya.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_agenda) {
                    startActivity(new Intent(this, Agenda_Consulta.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_progress) {
                    startActivity(new Intent(this, MeuProgresso.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_chat) {
                    startActivity(new Intent(this, TopicListActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(this, Perfil.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}
