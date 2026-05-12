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
 * Adapter para exibir a lista de tópicos de chat.
 */
public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {

    private List<ChatTopic> topicList;
    private OnTopicClickListener listener;

    // Interface para tratar cliques nos tópicos
    public interface OnTopicClickListener {
        void onTopicClick(ChatTopic topic);
    }

    public TopicAdapter(List<ChatTopic> topicList, OnTopicClickListener listener) {
        this.topicList = topicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout de cada tópico na lista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_topic, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        ChatTopic topic = topicList.get(position);
        holder.bind(topic, listener);
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    /**
     * ViewHolder para os elementos visuais de um tópico.
     */
    static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtLastMessage, txtDate, txtUser;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txt_topic_user);
            txtTitle = itemView.findViewById(R.id.txt_topic_title);
            txtLastMessage = itemView.findViewById(R.id.txt_last_message);
            txtDate = itemView.findViewById(R.id.txt_topic_date);
        }

        public void bind(ChatTopic topic, OnTopicClickListener listener) {
            // Configura a exibição do nome do paciente e ID
            String userDisplay = "Paciente: " + (topic.getUserName() != null ? topic.getUserName() : "N/A");
            if (topic.getPatientId() != null) {
                userDisplay += " (" + topic.getPatientId() + ")";
            }
            txtUser.setText(userDisplay);

            txtTitle.setText(topic.getTitle());
            txtLastMessage.setText(topic.getLastMessage());
            
            // Formata e exibe a data da última mensagem
            if (topic.getTimestamp() != null) {
                txtDate.setText(sdf.format(topic.getTimestamp().toDate()));
            }

            itemView.setOnClickListener(v -> listener.onTopicClick(topic));
        }
    }
}
