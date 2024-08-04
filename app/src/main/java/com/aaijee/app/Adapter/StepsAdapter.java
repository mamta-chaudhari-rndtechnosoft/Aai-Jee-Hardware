package com.aaijee.app.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.aaijee.app.Model.Steps;
import com.aaijee.app.R;

import java.util.ArrayList;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {

    private Activity activity;
    ArrayList<Steps> steps;

    public StepsAdapter(Activity activity, ArrayList<Steps> steps) {
        this.activity = activity;
        this.steps = steps;
    }

    @Override
    public StepsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(activity).inflate(R.layout.steps_layout, parent, false);

        return new StepsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepsAdapter.ViewHolder holder, final int position) {
        Glide.with(activity).load(steps.get(position).getImage()).into(holder.img_steps);
        holder.tv_steps.setText(steps.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_steps;
        TextView tv_steps;

        public ViewHolder(View itemView)
        {
            super(itemView);

            img_steps = (ImageView) itemView.findViewById(R.id.img_steps);
            tv_steps = (TextView) itemView.findViewById(R.id.tv_steps);

        }
    }
}