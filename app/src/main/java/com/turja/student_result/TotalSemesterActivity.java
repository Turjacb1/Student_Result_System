package com.turja.student_result;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class TotalSemesterActivity extends AppCompatActivity {

    private RecyclerView rvSemesters;
    private FirebaseFirestore db;
    private SemesterAdapter adapter;
    private ArrayList<HashMap<String, String>> semesterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_semester);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        rvSemesters = findViewById(R.id.rvSemesters);
        rvSemesters.setLayoutManager(new LinearLayoutManager(this));
        semesterList = new ArrayList<>();
        adapter = new SemesterAdapter(semesterList);
        rvSemesters.setAdapter(adapter);

        // Retrieve semesters from Firestore
        db.collection("semesters")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    semesterList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        HashMap<String, String> semester = new HashMap<>();
                        semester.put("semesterName", Objects.requireNonNull(document.getString("semesterName")));
                        semester.put("yearName", Objects.requireNonNull(document.getString("yearName")));
                        semesterList.add(semester);
                    }
                    adapter.notifyDataSetChanged();
                    if (semesterList.isEmpty()) {
                        Toast.makeText(this, "No semesters found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading semesters: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}