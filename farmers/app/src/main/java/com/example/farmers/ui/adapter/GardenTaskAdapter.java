package com.example.farmers.ui.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmers.R;

import java.util.ArrayList;
import java.util.List;

public class GardenTaskAdapter extends RecyclerView.Adapter<GardenTaskAdapter.TaskViewHolder> {

    public static class GardenTask {
        public String title;
        public String sub;
        public String tag;
        public boolean done;

        public GardenTask(String title, String sub, String tag, boolean done) {
            this.title = title;
            this.sub = sub;
            this.tag = tag;
            this.done = done;
        }
    }

    private final List<GardenTask> tasks = new ArrayList<>();

    public void setTasks(List<GardenTask> newTasks) {
        tasks.clear();
        if (newTasks != null) tasks.addAll(newTasks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        GardenTask task = tasks.get(position);
        holder.tvTaskTitle.setText(task.title);
        holder.tvTaskSub.setText(task.sub);
        holder.tvTaskTag.setText(task.tag);
        holder.cbTaskDone.setChecked(task.done);

        updateStrikeThrough(holder.tvTaskTitle, task.done);

        holder.cbTaskDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.done = isChecked;
            updateStrikeThrough(holder.tvTaskTitle, isChecked);
        });
    }

    private void updateStrikeThrough(TextView tv, boolean done) {
        if (done) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv.setAlpha(0.6f);
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tv.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbTaskDone;
        TextView tvTaskTitle, tvTaskSub, tvTaskTag;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbTaskDone = itemView.findViewById(R.id.cbTaskDone);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskSub = itemView.findViewById(R.id.tvTaskSub);
            tvTaskTag = itemView.findViewById(R.id.tvTaskTag);
        }
    }
}
