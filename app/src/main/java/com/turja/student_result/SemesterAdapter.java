package com.turja.student_result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;


public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder> {

    private ArrayList<HashMap<String, String>> semesterList;

    public SemesterAdapter(ArrayList<HashMap<String, String>> semesterList) {
        this.semesterList = semesterList;
    }

    @NonNull
    @Override
    public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_semester, parent, false);
        return new SemesterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {
        HashMap<String, String> semester = semesterList.get(position);
        // Labels are static in the layout, so just set the data
        holder.tvSemesterName.setText(semester.get("semesterName") != null ? semester.get("semesterName") : "N/A");
        holder.tvYearName.setText(semester.get("yearName") != null ? semester.get("yearName") : "N/A");
    }

    @Override
    public int getItemCount() {
        return semesterList.size();
    }

    public static class SemesterViewHolder extends RecyclerView.ViewHolder {
        TextView tvSemesterName, tvYearName;

        public SemesterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSemesterName = itemView.findViewById(R.id.tvSemesterName);
            tvYearName = itemView.findViewById(R.id.tvYearName);
        }
    }
}