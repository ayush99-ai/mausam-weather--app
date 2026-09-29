package com.example.farmers.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmers.R;
import com.example.farmers.data.model.PersonaItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class PersonaAdapter extends RecyclerView.Adapter<PersonaAdapter.PersonaViewHolder> {

    public interface OnPersonaSelectedListener {
        void onPersonaSelected(PersonaItem item);
    }

    private final List<PersonaItem> items = new ArrayList<>();
    private final OnPersonaSelectedListener listener;
    private int selectedPosition = 0;

    public PersonaAdapter(OnPersonaSelectedListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PersonaItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public PersonaItem getSelectedPersona() {
        if (selectedPosition >= 0 && selectedPosition < items.size()) {
            return items.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public PersonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_persona, parent, false);
        return new PersonaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonaViewHolder holder, int position) {
        PersonaItem item = items.get(position);
        Context context = holder.itemView.getContext();
        boolean isSelected = (position == selectedPosition);

        holder.tvIcon.setText(item.getIcon());
        holder.tvTitle.setText(item.getTitle());

        if (isSelected) {
            int activeColor = ContextCompat.getColor(context, R.color.primary_green);
            holder.cardPersona.setCardBackgroundColor(activeColor);
            holder.cardPersona.setStrokeColor(activeColor);
            holder.tvTitle.setTextColor(Color.WHITE);
        } else {
            int bgColor = ContextCompat.getColor(context, R.color.card_bg_light);
            int strokeColor = ContextCompat.getColor(context, R.color.card_stroke_light);
            int textColor = ContextCompat.getColor(context, R.color.text_primary);

            holder.cardPersona.setCardBackgroundColor(bgColor);
            holder.cardPersona.setStrokeColor(strokeColor);
            holder.tvTitle.setTextColor(textColor);
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onPersonaSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PersonaViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardPersona;
        TextView tvIcon;
        TextView tvTitle;

        public PersonaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPersona = itemView.findViewById(R.id.cardPersona);
            tvIcon = itemView.findViewById(R.id.tvPersonaIcon);
            tvTitle = itemView.findViewById(R.id.tvPersonaTitle);
        }
    }
}
