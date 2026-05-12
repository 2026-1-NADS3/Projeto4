package com.example.codigo_pi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.List;

/**
 * Adapter para exibir a lista de treinos do usuário com barra de progresso.
 */
public class TreinoAdapter extends RecyclerView.Adapter<TreinoAdapter.TreinoViewHolder> {

    private List<Treino> treinoList;
    private OnTreinoClickListener listener;

    // Interface para capturar o clique em um treino
    public interface OnTreinoClickListener {
        void onTreinoClick(Treino treino);
    }

    public TreinoAdapter(List<Treino> treinoList, OnTreinoClickListener listener) {
        this.treinoList = treinoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TreinoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do card de treino
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_treino_card, parent, false);
        return new TreinoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreinoViewHolder holder, int position) {
        Treino treino = treinoList.get(position);
        holder.bind(treino, listener);
    }

    @Override
    public int getItemCount() {
        return treinoList.size();
    }

    /**
     * ViewHolder que gerencia os componentes visuais de cada item da lista.
     */
    static class TreinoViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtStatus, txtPorcentagem;
        LinearProgressIndicator progressIndicator;

        public TreinoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txt_treino_titulo);
            txtStatus = itemView.findViewById(R.id.txt_treino_status);
            txtPorcentagem = itemView.findViewById(R.id.txt_treino_porcentagem);
            progressIndicator = itemView.findViewById(R.id.progress_treino);
        }

        public void bind(Treino treino, OnTreinoClickListener listener) {
            txtTitulo.setText(treino.getTitulo());
            
            // Calcula e exibe o resumo de exercícios concluídos
            int total = treino.getExercicios() != null ? treino.getExercicios().size() : 0;
            int concluidos = 0;
            if (treino.getExercicios() != null) {
                for (TreinoExercicio ex : treino.getExercicios()) {
                    if (ex.isConcluido()) concluidos++;
                }
            }
            
            txtStatus.setText(concluidos + " de " + total + " exercícios concluídos");
            
            // Atualiza o progresso visual
            int progresso = treino.getProgresso();
            progressIndicator.setProgress(progresso);
            txtPorcentagem.setText(progresso + "%");

            itemView.setOnClickListener(v -> listener.onTreinoClick(treino));
        }
    }
}
