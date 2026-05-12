package com.example.codigo_pi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter para exibir a lista do histórico de exercícios realizados.
 */
public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.ViewHolder> {

    private List<Historico> historicoList;

    public HistoricoAdapter(List<Historico> historicoList) {
        this.historicoList = historicoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout de cada item do histórico
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historico, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Historico item = historicoList.get(position);
        
        // Preenche os dados do exercício, dor e observação
        holder.txtExercicio.setText(item.getExercicio_nome());
        holder.txtDor.setText("Dor: " + item.getNivel_dor());
        
        // Formata e exibe a data de conclusão
        String dataStr = "";
        if (item.getData_conclusao() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            dataStr = sdf.format(item.getData_conclusao().toDate());
        }
        holder.txtData.setText(dataStr);

        holder.txtObs.setText(item.getObservacao());
        
        // Oculta o campo de observação se estiver vazio
        if (item.getObservacao() == null || item.getObservacao().isEmpty()) {
            holder.txtObs.setVisibility(View.GONE);
        } else {
            holder.txtObs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return historicoList.size();
    }

    /**
     * ViewHolder para gerenciar as Views de cada item da lista.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtExercicio, txtDor, txtData, txtObs;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtExercicio = itemView.findViewById(R.id.txt_item_exercicio);
            txtDor = itemView.findViewById(R.id.txt_item_dor);
            txtData = itemView.findViewById(R.id.txt_item_data);
            txtObs = itemView.findViewById(R.id.txt_item_obs);
        }
    }
}
