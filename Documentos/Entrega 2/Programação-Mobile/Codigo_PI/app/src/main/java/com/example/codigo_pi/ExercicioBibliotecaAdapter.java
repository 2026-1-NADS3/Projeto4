package com.example.codigo_pi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ExercicioBibliotecaAdapter extends RecyclerView.Adapter<ExercicioBibliotecaAdapter.ViewHolder> {

    private List<Exercicio> exercicioList;
    private OnExercicioClickListener listener;

    public interface OnExercicioClickListener {
        void onExercicioClick(Exercicio exercicio);
    }

    public ExercicioBibliotecaAdapter(List<Exercicio> exercicioList, OnExercicioClickListener listener) {
        this.exercicioList = exercicioList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercicio_biblioteca, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercicio exercicio = exercicioList.get(position);
        holder.txtNome.setText(exercicio.getNome());
        holder.txtRepeticoes.setText(exercicio.getRepeticoes() + " repetições");

        String imageUrl = null;
        if (exercicio.getImagens() != null && !exercicio.getImagens().isEmpty()) {
            imageUrl = exercicio.getImagens().get(0);
        }

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.dor_lombar)
                .error(R.drawable.dor_lombar)
                .centerCrop()
                .into(holder.imgThumb);

        holder.itemView.setOnClickListener(v -> listener.onExercicioClick(exercicio));
    }

    @Override
    public int getItemCount() {
        return exercicioList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView txtNome, txtRepeticoes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.img_exercicio_thumb);
            txtNome = itemView.findViewById(R.id.txt_exercicio_nome);
            txtRepeticoes = itemView.findViewById(R.id.txt_exercicio_repeticoes);
        }
    }
}
