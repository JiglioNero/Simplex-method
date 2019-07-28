package com.example.overlord.optimize.Other;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.overlord.optimize.Activity.InputActivity;
import com.example.overlord.optimize.Arithmetics.Equotion;
import com.example.overlord.optimize.Arithmetics.Number;
import com.example.overlord.optimize.R;

import java.util.ArrayList;
import java.util.Collection;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    private ArrayList<OptimizationTask> tasks = new ArrayList<>();
    private Context context;

    public TaskAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_holder, parent, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setItems(Collection<OptimizationTask> tasks) {
        this.tasks.addAll(tasks);
        notifyDataSetChanged();
    }

    public void clearItems() {
        tasks.clear();
        notifyDataSetChanged();
    }


    public class TaskHolder extends RecyclerView.ViewHolder {

        private TextView dimension;
        private TextView targetFuntion;
        private TextView limit;
        private TextView basis;
        private LinearLayout conditions;

        public TaskHolder(View itemView) {
            super(itemView);
            dimension = itemView.findViewById(R.id.dimension);
            targetFuntion = itemView.findViewById(R.id.targetFuntion);
            limit = itemView.findViewById(R.id.limit);
            basis = itemView.findViewById(R.id.basis);
            conditions = itemView.findViewById(R.id.conds);
        }

        public void bind(final OptimizationTask task) {
            dimension.setText(String.format("%d X %d", task.getTargetV().length, task.getConditions().size()));
            String tF = "";
            tF += task.getTargetV()[0] + "X" + 1;
            for (int i = 1; i < task.getTargetV().length; i++) {
                if (task.getTargetV()[i].lessThen(new Number(0))) {
                    tF += task.getTargetV()[i] + "X" + (i+1);
                } else {
                    tF += "+" + task.getTargetV()[i] + "X" + (i+1);
                }
            }
            targetFuntion.setText(tF);
            limit.setText(task.getLimit().toString());
            if(task.getBasis()!=null) {
                String b = "(";
                b += task.getBasis()[0];
                for (int i = 1; i < task.getBasis().length; i++) {
                        b += ";"+task.getBasis()[i];
                }
                b+=")";
                basis.setText(b);
            }
            else {
                basis.setVisibility(View.INVISIBLE);
            }
            conditions.removeAllViews();
            for(Equotion eq : task.getConditions()){
                TextView textView = new TextView(context);
                textView.setText(eq.toString());
                conditions.addView(textView);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, InputActivity.class);
                    intent.putExtra("task", task);
                    context.startActivity(intent);
                }
            });
        }
    }
}
