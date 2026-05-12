package com.example.codigo_pi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

/**
 * Adapter para exibir imagens de um exercício em um ViewPager2 ou RecyclerView.
 */
public class ExercicioAdapter extends RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder> {

    private final List<String> imagens;

    public ExercicioAdapter(List<String> imagens) {
        this.imagens = imagens;
    }

    @NonNull
    @Override
    public ExercicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item de imagem
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercicio_imagem, parent, false);
        return new ExercicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercicioViewHolder holder, int position) {
        String imageUrl = imagens.get(position);
        
        // Carrega a imagem da URL usando a biblioteca Glide
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.dor_lombar)
                .error(R.drawable.dor_lombar)
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imagens.size();
    }

    /**
     * ViewHolder para as imagens do exercício.
     */
    static class ExercicioViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ExercicioViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_exercicio);
        }
    }
}
