package com.turja.student_result;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CreateSubjectsActivity extends AppCompatActivity {

    private EditText etSubjectName, etSubjectCode, etCredit;
    private Spinner spDepartment, spYearName, spSemesterName;
    private Button btnCreate;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_subjects);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Bind views
        etSubjectName = findViewById(R.id.etSubjectName);
        etSubjectCode = findViewById(R.id.etSubjectCode);
        etCredit = findViewById(R.id.etCredit);
        spDepartment = findViewById(R.id.spDepartment);
        spYearName = findViewById(R.id.spYearName);
        spSemesterName = findViewById(R.id.spSemesterName);
        btnCreate = findViewById(R.id.btnCreate);

        if (etSubjectName == null || etSubjectCode == null || etCredit == null || spDepartment == null ||
                spYearName == null || spSemesterName == null || btnCreate == null) {
            Toast.makeText(this, "UI initialization failed. Please check layout.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Load spinners with dynamic data from Firestore
        setupSpinners();

        btnCreate.setOnClickListener(v -> {
            try {
                String subjectName = etSubjectName.getText() != null ? etSubjectName.getText().toString().trim() : "";
                String subjectCode = etSubjectCode.getText() != null ? etSubjectCode.getText().toString().trim() : "";
                String credit = etCredit.getText() != null ? etCredit.getText().toString().trim() : "";
                String department = spDepartment.getSelectedItem() != null ? spDepartment.getSelectedItem().toString() : "";
                String yearName = spYearName.getSelectedItem() != null ? spYearName.getSelectedItem().toString() : "";
                String semesterName = spSemesterName.getSelectedItem() != null ? spSemesterName.getSelectedItem().toString() : "";

                if (subjectName.isEmpty() || subjectCode.isEmpty() || credit.isEmpty()) {
                    Toast.makeText(this, "Please fill in Subject Name, Subject Code, and Credit", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show confirmation dialog
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Creation")
                        .setMessage("Are you sure you want to create this subject? After final creation, you cannot edit.")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Map<String, Object> subject = new HashMap<>();
                            subject.put("subjectName", subjectName);
                            subject.put("subjectCode", subjectCode);
                            subject.put("credit", credit);
                            subject.put("department", department);
                            subject.put("yearName", yearName);
                            subject.put("semesterName", semesterName);

                            db.collection("subjects")
                                    .add(subject)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(this, "Subject created!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .show();

            } catch (Exception e) {
                Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSpinners() {
        // Set up department spinner with static data
        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(this,
                R.array.departments_array, android.R.layout.simple_spinner_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartment.setAdapter(departmentAdapter);

        // Fetch dynamic year and semester data from Firestore
        db.collection("semesters")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Set<String> yearNames = new HashSet<>();
                    Set<String> semesterNames = new HashSet<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String yearName = document.getString("yearName");
                        String semesterName = document.getString("semesterName");
                        if (yearName != null) yearNames.add(yearName);
                        if (semesterName != null) semesterNames.add(semesterName);
                    }

                    ArrayList<String> yearList = new ArrayList<>(yearNames);
                    ArrayList<String> semesterList = new ArrayList<>(semesterNames);

                    if (yearList.isEmpty()) yearList.add("No Years Available");
                    if (semesterList.isEmpty()) semesterList.add("No Semesters Available");

                    // Set up year spinner
                    ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, yearList);
                    yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spYearName.setAdapter(yearAdapter);

                    // Set up semester spinner
                    ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, semesterList);
                    semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSemesterName.setAdapter(semesterAdapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading semester data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Fallback to static data if Firestore fails

                });
    }
}