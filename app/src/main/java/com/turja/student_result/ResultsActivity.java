package com.turja.student_result;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ResultsActivity extends AppCompatActivity {

    private EditText etName, etId, etRegistrationNumber, etSession, etDepartment, etTotalGpa, etCgpa;
    private Spinner spYear, spSemester;
    private FirebaseFirestore db;
    private HashMap<String, String> studentData;
    private String documentId;
    private LinearLayout subjectContainer;
    private Button btnAddSubject, btnSaveResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        etName = findViewById(R.id.etName);
        etId = findViewById(R.id.etId);
        etRegistrationNumber = findViewById(R.id.etRegistrationNumber);
        etSession = findViewById(R.id.etSession);
        etDepartment = findViewById(R.id.etDepartment);
        etTotalGpa = findViewById(R.id.etTotalGpa);
        etCgpa = findViewById(R.id.etCgpa);
        spYear = findViewById(R.id.spYear);
        spSemester = findViewById(R.id.spSemester);
        subjectContainer = findViewById(R.id.subjectContainer);
        btnAddSubject = findViewById(R.id.btnAddSubject);
        btnSaveResult = findViewById(R.id.btnSaveResult);

        // Make fields read-only
        etName.setEnabled(false);
        etId.setEnabled(false);
        etRegistrationNumber.setEnabled(false);
        etSession.setEnabled(false);
        etDepartment.setEnabled(false);

        // Get data from Intent
        studentData = (HashMap<String, String>) getIntent().getSerializableExtra("studentData");
        documentId = getIntent().getStringExtra("documentId");

        if (studentData != null && documentId != null) {
            // Populate fields with passed data
            etName.setText(studentData.get("studentName") != null ? studentData.get("studentName") : "N/A");
            etId.setText(studentData.get("studentId") != null ? studentData.get("studentId") : "N/A");
            etRegistrationNumber.setText(studentData.get("registrationNumber") != null ? studentData.get("registrationNumber") : "N/A");
            etSession.setText(studentData.get("session") != null ? studentData.get("session") : "N/A");
            etDepartment.setText(studentData.get("department") != null ? studentData.get("department") : "N/A");

            // Fetch and populate Spinners
            fetchYearSemesterSubjectData();
            addSubjectField();
        } else {
            Toast.makeText(this, "No student data available", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up Add Subject button
        btnAddSubject.setOnClickListener(v -> addSubjectField());

        // Set up Save Result button
        btnSaveResult.setOnClickListener(v -> saveResult());
    }

    private void fetchYearSemesterSubjectData() {
        String department = studentData.get("department");
        if (department == null || department.equals("N/A")) {
            Toast.makeText(this, "Department not available", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("ResultsActivity", "Fetching data for department: " + department);

        // Fetch years and semesters from Firestore with department filter
        db.collection("semesters")
                .whereEqualTo("department", department)
                .get()
                .addOnSuccessListener(semesterQuerySnapshots -> {
                    Set<String> years = new HashSet<>();
                    Set<String> semesters = new HashSet<>();

                    for (QueryDocumentSnapshot document : semesterQuerySnapshots) {
                        String year = document.getString("yearName");
                        String semester = document.getString("semesterName");
                        if (year != null && !year.equals("N/A")) years.add(year);
                        if (semester != null && !semester.equals("N/A")) semesters.add(semester);
                    }

                    // If no data with filter, try fetching all semesters as fallback
                    if (years.isEmpty() || semesters.isEmpty()) {
                        db.collection("semesters")
                                .get()
                                .addOnSuccessListener(allSemesterQuerySnapshots -> {
                                    for (QueryDocumentSnapshot document : allSemesterQuerySnapshots) {
                                        String year = document.getString("yearName");
                                        String semester = document.getString("semesterName");
                                        if (year != null && !year.equals("N/A")) years.add(year);
                                        if (semester != null && !semester.equals("N/A")) semesters.add(semester);
                                    }
                                    populateSpinners(years, semesters);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("ResultsActivity", "Fallback semester fetch failed: " + e.getMessage());
                                    populateSpinners(years, semesters);
                                });
                    } else {
                        populateSpinners(years, semesters);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ResultsActivity", "Semester fetch failed: " + e.getMessage());
                    db.collection("semesters")
                            .get()
                            .addOnSuccessListener(allSemesterQuerySnapshots -> {
                                Set<String> years = new HashSet<>();
                                Set<String> semesters = new HashSet<>();
                                for (QueryDocumentSnapshot document : allSemesterQuerySnapshots) {
                                    String year = document.getString("yearName");
                                    String semester = document.getString("semesterName");
                                    if (year != null && !year.equals("N/A")) years.add(year);
                                    if (semester != null && !semester.equals("N/A")) semesters.add(semester);
                                }
                                populateSpinners(years, semesters);
                            })
                            .addOnFailureListener(e2 -> {
                                Log.e("ResultsActivity", "Fallback all semesters fetch failed: " + e2.getMessage());
                                populateSpinners(new HashSet<>(), new HashSet<>());
                            });
                });

        // Fetch subjects from Firestore
        db.collection("subjects")
                .whereEqualTo("department", department)
                .get()
                .addOnSuccessListener(subjectQuerySnapshots -> {
                    ArrayList<String> subjects = new ArrayList<>();
                    for (QueryDocumentSnapshot document : subjectQuerySnapshots) {
                        String subjectName = document.getString("subjectName");
                        if (subjectName != null && !subjectName.equals("N/A")) subjects.add(subjectName);
                    }
                    updateSubjectSpinners(subjects);
                })
                .addOnFailureListener(e -> {
                    Log.e("ResultsActivity", "Subject fetch failed: " + e.getMessage());
                    ArrayList<String> subjectList = new ArrayList<>();
                    subjectList.add("No Subjects Available");
                    updateSubjectSpinners(subjectList);
                });
    }

    private void populateSpinners(Set<String> years, Set<String> semesters) {
        ArrayList<String> yearList = new ArrayList<>(years);
        ArrayList<String> semesterList = new ArrayList<>(semesters);
        if (yearList.isEmpty()) yearList.add("No Years Available");
        if (semesterList.isEmpty()) semesterList.add("No Semesters Available");

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(yearAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesterList);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSemester.setAdapter(semesterAdapter);
    }

    private void updateSubjectSpinners(ArrayList<String> subjects) {
        for (int i = 0; i < subjectContainer.getChildCount(); i++) {
            View view = subjectContainer.getChildAt(i);
            if (view instanceof LinearLayout) {
                Spinner subjectSpinner = view.findViewById(R.id.spSubject);
                if (subjectSpinner != null) {
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    subjectSpinner.setAdapter(subjectAdapter);
                }
            }
        }
    }

    private void addSubjectField() {
        View subjectView = getLayoutInflater().inflate(R.layout.item_subject3, subjectContainer, false);

        Spinner spSubject = subjectView.findViewById(R.id.spSubject);
        EditText etTotalNumber = subjectView.findViewById(R.id.etTotalNumber);
        EditText etGrade = subjectView.findViewById(R.id.etGrade);

        subjectContainer.addView(subjectView);

        db.collection("subjects")
                .whereEqualTo("department", studentData.get("department"))
                .get()
                .addOnSuccessListener(subjectQuerySnapshots -> {
                    ArrayList<String> subjects = new ArrayList<>();
                    for (QueryDocumentSnapshot document : subjectQuerySnapshots) {
                        String subjectName = document.getString("subjectName");
                        if (subjectName != null && !subjectName.equals("N/A")) subjects.add(subjectName);
                    }
                    if (subjects.isEmpty()) subjects.add("No Subjects Available");
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSubject.setAdapter(subjectAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.e("ResultsActivity", "Subject fetch for new field failed: " + e.getMessage());
                    ArrayList<String> subjectList = new ArrayList<>();
                    subjectList.add("No Subjects Available");
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectList);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSubject.setAdapter(subjectAdapter);
                });
    }

    private void saveResult() {
        String name = etName.getText().toString().trim();
        String studentId = etId.getText().toString().trim();
        String registrationNumber = etRegistrationNumber.getText().toString().trim();
        String session = etSession.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String year = spYear.getSelectedItem() != null ? spYear.getSelectedItem().toString() : "N/A";
        String semester = spSemester.getSelectedItem() != null ? spSemester.getSelectedItem().toString() : "N/A";
        String totalGpa = etTotalGpa.getText().toString().trim();
        String cgpa = etCgpa.getText().toString().trim();

        if (year.equals("No Years Available") || semester.equals("No Semesters Available") || totalGpa.isEmpty() || cgpa.isEmpty()) {
            Toast.makeText(this, "Please select a valid year, semester, and enter GPA/CGPA", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> resultData = new HashMap<>();
        resultData.put("name", name);
        resultData.put("studentId", studentId);
        resultData.put("registrationNumber", registrationNumber);
        resultData.put("session", session);
        resultData.put("department", department);
        resultData.put("year", year);
        resultData.put("semester", semester);
        resultData.put("totalGpa", totalGpa);
        resultData.put("cgpa", cgpa);
        resultData.put("timestamp", System.currentTimeMillis());

        ArrayList<HashMap<String, String>> subjectsData = new ArrayList<>();
        for (int i = 0; i < subjectContainer.getChildCount(); i++) {
            View view = subjectContainer.getChildAt(i);
            if (view instanceof LinearLayout) {
                Spinner spSubject = view.findViewById(R.id.spSubject);
                EditText etTotalNumber = view.findViewById(R.id.etTotalNumber);
                EditText etGrade = view.findViewById(R.id.etGrade);

                String subject = spSubject.getSelectedItem() != null ? spSubject.getSelectedItem().toString() : "N/A";
                String totalNumber = etTotalNumber.getText().toString().trim();
                String grade = etGrade.getText().toString().trim();

                if (!subject.equals("N/A") && !totalNumber.isEmpty() && !grade.isEmpty()) {
                    HashMap<String, String> subjectEntry = new HashMap<>();
                    subjectEntry.put("subject", subject);
                    subjectEntry.put("totalNumber", totalNumber);
                    subjectEntry.put("grade", grade);
                    subjectsData.add(subjectEntry);
                }
            }
        }

        if (subjectsData.isEmpty()) {
            Toast.makeText(this, "Please add at least one subject with valid data", Toast.LENGTH_SHORT).show();
            return;
        }

        resultData.put("subjects", subjectsData);

        db.collection("results")
                .add(resultData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Result saved successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResultsActivity.this, ResultDisplayActivity.class);
                    intent.putExtra("studentId", studentId);
                    intent.putExtra("department", department);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("ResultsActivity", "Failed to save result: " + e.getMessage());
                    Toast.makeText(this, "Error saving result", Toast.LENGTH_SHORT).show();
                });
    }
}