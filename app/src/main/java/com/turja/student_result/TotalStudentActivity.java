package com.turja.student_result;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class TotalStudentActivity extends AppCompatActivity {

    private ActivityTotalStudentBinding binding;
    private FirebaseFirestore db;
    private StudentAdapter adapter;
    private ArrayList<HashMap<String, String>> studentList;
    private ArrayList<String> documentIds;
    private ArrayList<HashMap<String, String>> filteredList;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = ActivityTotalStudentBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            if (binding.rvStudents == null) {
                throw new IllegalStateException("rvStudents is null in binding");
            }

            // Initialize Firestore
            db = FirebaseFirestore.getInstance();

            // Get admin status from intent
            isAdmin = getIntent().getBooleanExtra("isAdmin", false);

            // Initialize RecyclerView
            binding.rvStudents.setLayoutManager(new LinearLayoutManager(this));
            studentList = new ArrayList<>();
            documentIds = new ArrayList<>();
            filteredList = new ArrayList<>();
            adapter = new StudentAdapter(filteredList, documentId -> deleteStudent(documentId), (student, documentId) -> {
                Intent intent = new Intent(TotalStudentActivity.this, ResultsActivity.class);
                intent.putExtra("studentData", new HashMap<>(student));
                intent.putExtra("documentId", documentId);
                intent.putExtra("isAdmin", isAdmin); // Pass admin status
                startActivity(intent);
            });
            binding.rvStudents.setAdapter(adapter);

            // Initialize Search EditText manually
            EditText etSearch = findViewById(R.id.etSearch);
            if (etSearch != null) {
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            } else {
                throw new IllegalStateException("etSearch is null in layout");
            }

            // Retrieve students from Firestore
            db.collection("students")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        studentList.clear();
                        documentIds.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            HashMap<String, String> student = new HashMap<>();
                            student.put("studentName", document.getString("studentName") != null ? document.getString("studentName") : "N/A");
                            student.put("studentId", document.getString("studentId") != null ? document.getString("studentId") : "N/A");
                            student.put("registrationNumber", document.getString("registrationNumber") != null ? document.getString("registrationNumber") : "N/A");
                            student.put("session", document.getString("session") != null ? document.getString("session") : "N/A");
                            student.put("email", document.getString("email") != null ? document.getString("email") : "N/A");
                            student.put("mobileNumber", document.getString("mobileNumber") != null ? document.getString("mobileNumber") : "N/A");
                            student.put("gender", document.getString("gender") != null ? document.getString("gender") : "N/A");
                            student.put("department", document.getString("department") != null ? document.getString("department") : "N/A");
                            student.put("zila", document.getString("zila") != null ? document.getString("zila") : "N/A");
                            student.put("religion", document.getString("religion") != null ? document.getString("religion") : "N/A");
                            studentList.add(student);
                            documentIds.add(document.getId());
                        }
                        filteredList.addAll(studentList);
                        adapter.setDocumentIds(documentIds);
                        adapter.notifyDataSetChanged();
                        if (studentList.isEmpty()) {
                            Toast.makeText(this, "No students found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error loading students: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Crash: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    private void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(studentList);
        } else {
            query = query.toLowerCase();
            for (HashMap<String, String> student : studentList) {
                String name = student.get("studentName") != null ? student.get("studentName").toLowerCase() : "";
                String session = student.get("session") != null ? student.get("session").toLowerCase() : "";
                String roll = student.get("studentId") != null ? student.get("studentId").toLowerCase() : "";
                String department = student.get("department") != null ? student.get("department").toLowerCase() : "";
                if (name.contains(query) || session.contains(query) || roll.contains(query) || department.contains(query)) {
                    filteredList.add(student);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void deleteStudent(String documentId) {
        int position = documentIds.indexOf(documentId);
        if (position == -1) {
            Toast.makeText(this, "Student not found for deletion", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this student?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("students").document(documentId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                studentList.remove(position);
                                documentIds.remove(position);
                                filter(""); // Refresh filtered list after deletion
                                Toast.makeText(this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error deleting student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}