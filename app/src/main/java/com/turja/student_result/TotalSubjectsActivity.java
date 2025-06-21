package com.turja.student_result;

import android.os.Bundle;
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
import java.util.Objects;

public class TotalSubjectsActivity extends AppCompatActivity {

    private ActivityTotalSubjectsBinding binding;
    private FirebaseFirestore db;
    private SubjectAdapter adapter;
    private ArrayList<HashMap<String, String>> subjectList;
    private ArrayList<String> documentIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTotalSubjectsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        binding.rvSubjects.setLayoutManager(new LinearLayoutManager(this));
        subjectList = new ArrayList<>();
        documentIds = new ArrayList<>();
        adapter = new SubjectAdapter(subjectList, documentId -> deleteSubject(documentId));
        binding.rvSubjects.setAdapter(adapter);

        // Retrieve subjects from Firestore
        db.collection("subjects")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    subjectList.clear();
                    documentIds.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        HashMap<String, String> subject = new HashMap<>();
                        subject.put("subjectName", Objects.requireNonNull(document.getString("subjectName")));
                        subject.put("subjectCode", Objects.requireNonNull(document.getString("subjectCode")));
                        subject.put("credit", Objects.requireNonNull(document.getString("credit")));
                        subject.put("department", Objects.requireNonNull(document.getString("department")));
                        subject.put("yearName", Objects.requireNonNull(document.getString("yearName")));
                        subject.put("semesterName", Objects.requireNonNull(document.getString("semesterName")));
                        subjectList.add(subject);
                        documentIds.add(document.getId());
                    }
                    adapter.setDocumentIds(documentIds);
                    adapter.notifyDataSetChanged();
                    if (subjectList.isEmpty()) {
                        Toast.makeText(this, "No subjects found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading subjects: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteSubject(String documentId) {
        int position = documentIds.indexOf(documentId);
        if (position == -1) {
            Toast.makeText(this, "Subject not found for deletion", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this subject?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("subjects").document(documentId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                subjectList.remove(position);
                                documentIds.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(this, "Subject deleted successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error deleting subject: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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