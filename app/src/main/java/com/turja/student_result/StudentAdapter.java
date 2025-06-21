package com.turja.student_result;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private ArrayList<HashMap<String, String>> studentList;
    private OnDeleteClickListener deleteClickListener;
    private ArrayList<String> documentIds;
    private OnAddResultClickListener addResultClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(String documentId);
    }

    public interface OnAddResultClickListener {
        void onAddResultClick(HashMap<String, String> student, String documentId);
    }

    public StudentAdapter(ArrayList<HashMap<String, String>> studentList, OnDeleteClickListener deleteListener, OnAddResultClickListener addResultListener) {
        this.studentList = studentList != null ? studentList : new ArrayList<>();
        this.deleteClickListener = deleteListener;
        this.addResultClickListener = addResultListener;
        this.documentIds = new ArrayList<>();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        if (position >= 0 && position < studentList.size()) {
            HashMap<String, String> student = studentList.get(position);
            holder.tvStudentName.setText(student.get("studentName") != null ? student.get("studentName") : "N/A");
            holder.tvStudentId.setText(student.get("studentId") != null ? student.get("studentId") : "N/A");
            holder.tvRegistrationNumber.setText(student.get("registrationNumber") != null ? student.get("registrationNumber") : "N/A");
            holder.tvSession.setText(student.get("session") != null ? student.get("session") : "N/A");
            holder.tvEmail.setText(student.get("email") != null ? student.get("email") : "N/A");
            holder.tvMobileNumber.setText(student.get("mobileNumber") != null ? student.get("mobileNumber") : "N/A");
            holder.tvGender.setText(student.get("gender") != null ? student.get("gender") : "N/A");
            holder.tvDepartment.setText(student.get("department") != null ? student.get("department") : "N/A");
            holder.tvZila.setText(student.get("zila") != null ? student.get("zila") : "N/A");
            holder.tvReligion.setText(student.get("religion") != null ? student.get("religion") : "N/A");

            // Set delete button click listener
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null && position < documentIds.size()) {
                    deleteClickListener.onDeleteClick(documentIds.get(position));
                }
            });

            // Set add result button click listener
            holder.btnAddResult.setOnClickListener(v -> {
                if (addResultClickListener != null && position < documentIds.size()) {
                    addResultClickListener.onAddResultClick(student, documentIds.get(position));
                }
            });

            // Set show result button click listener
            holder.btnShowResult.setOnClickListener(v -> {
                if (position < documentIds.size()) {
                    String documentId = documentIds.get(position);
                    String studentId = student.get("studentId");
                    String department = student.get("department");
                    if (studentId != null && department != null) {
                        Intent intent = new Intent(holder.itemView.getContext(), ResultDisplayActivity.class);
                        intent.putExtra("studentId", studentId);
                        intent.putExtra("department", department);
                        intent.putExtra("isAdmin", ((TotalStudentActivity) holder.itemView.getContext()).getIntent().getBooleanExtra("isAdmin", false));
                        holder.itemView.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Invalid student data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvStudentId, tvRegistrationNumber, tvSession, tvEmail, tvMobileNumber, tvGender, tvZila, tvReligion, tvDepartment;
        Button btnDelete, btnAddResult, btnShowResult;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvRegistrationNumber = itemView.findViewById(R.id.tvRegistrationNumber);
            tvSession = itemView.findViewById(R.id.tvSession);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvMobileNumber = itemView.findViewById(R.id.tvMobileNumber);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvZila = itemView.findViewById(R.id.tvZila);
            tvDepartment = itemView.findViewById(R.id.tvDepartment);
            tvReligion = itemView.findViewById(R.id.tvReligion);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnAddResult = itemView.findViewById(R.id.btnAddResult);
            btnShowResult = itemView.findViewById(R.id.btnShowResult);
        }
    }

    public void setDocumentIds(ArrayList<String> ids) {
        this.documentIds = ids != null ? ids : new ArrayList<>();
    }
}