package com.example.codigo_pi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adapter para o ViewPager2 da tela de exercícios.
 * Gerencia a inflação e vinculação de imagens de RPG e implementa 
 * a funcionalidade de visualização em tela cheia ao clicar na imagem.
 */
public class ExercicioAdapter extends RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder> {

    private final List<Integer> imagens;

    public ExercicioAdapter(List<Integer> imagens) {
        this.imagens = imagens;
    }

    @NonNull
    @Override
    public ExercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercicio_imagem, parent, false);
        return new ExercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercicioViewHolder holder, int position) {
        int imageRes = imagens.get(position);
        holder.imageView.setImageResource(imageRes);

        // Ao clicar na imagem do carrossel, abre uma nova Activity com a imagem em tela grande (Intent explícita)
        holder.imageView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, FullscreenImageActivity.class);
            intent.putExtra("IMAGE_RESOURCE", imageRes);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imagens.size();
    }

    static class ExercicioViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ExercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_exercicio);
        }
    }
}
