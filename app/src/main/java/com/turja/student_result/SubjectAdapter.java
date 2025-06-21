package com.turja.student_result;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private ArrayList<HashMap<String, String>> subjectList;
    private OnDeleteClickListener deleteClickListener;
    private ArrayList<String> documentIds;

    public interface OnDeleteClickListener {
        void onDeleteClick(String documentId);
    }

    public SubjectAdapter(ArrayList<HashMap<String, String>> subjectList, OnDeleteClickListener listener) {
        this.subjectList = subjectList != null ? subjectList : new ArrayList<>();
        this.deleteClickListener = listener;
        this.documentIds = new ArrayList<>();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject2, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        if (position >= 0 && position < subjectList.size()) {
            HashMap<String, String> subject = subjectList.get(position);
            holder.tvSubjectName.setText(subject.get("subjectName") != null ? subject.get("subjectName") : "N/A");
            holder.tvSubjectCode.setText(subject.get("subjectCode") != null ? subject.get("subjectCode") : "N/A");
            holder.tvCredit.setText(subject.get("credit") != null ? subject.get("credit") : "N/A");
            holder.tvDepartment.setText(subject.get("department") != null ? subject.get("department") : "N/A");
            holder.tvYearName.setText(subject.get("yearName") != null ? subject.get("yearName") : "N/A");
            holder.tvSemesterName.setText(subject.get("semesterName") != null ? subject.get("semesterName") : "N/A");

            // Set delete button click listener
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null && position < documentIds.size()) {
                    deleteClickListener.onDeleteClick(documentIds.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return subjectList != null ? subjectList.size() : 0;
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvSubjectCode, tvCredit, tvDepartment, tvYearName, tvSemesterName;
        Button btnDelete;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvSubjectCode = itemView.findViewById(R.id.tvSubjectCode); // Fixed typo: findViewId -> findViewById
            tvCredit = itemView.findViewById(R.id.tvCredit);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvYearName = itemView.findViewById(R.id.tvYearName);
            tvSemesterName = itemView.findViewById(R.id.tvSemesterName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Method to update document IDs
    public void setDocumentIds(ArrayList<String> ids) {
        this.documentIds = ids != null ? ids : new ArrayList<>();
    }
}