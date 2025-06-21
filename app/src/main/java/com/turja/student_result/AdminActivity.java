package com.turja.student_result;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashSet;

public class AdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView tvTotalStudentCount;
    private TextView tvDepartmentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find the TextView for Total Student count and Department count
        tvTotalStudentCount = findViewById(R.id.tvTotalStudentCount);
        tvDepartmentCount = findViewById(R.id.tvSubjectCount); // Using tvSubjectCount as placeholder for Department count

        // Fetch total unique students by studentId
        fetchTotalStudents();

        // Fetch total unique departments
        fetchTotalDepartments();

        // Navigation methods remain the same, called via XML onClick
    }

    private void fetchTotalStudents() {
        db.collection("students")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    HashSet<String> uniqueStudentIds = new HashSet<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String studentId = document.getString("studentId");
                        if (studentId != null) {
                            uniqueStudentIds.add(studentId);
                        }
                    }
                    int totalStudents = uniqueStudentIds.size();
                    if (tvTotalStudentCount != null) {
                        tvTotalStudentCount.setText(String.valueOf(totalStudents));
                    } else {
                        Toast.makeText(this, "Error: Student Count TextView not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching students: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (tvTotalStudentCount != null) {
                        tvTotalStudentCount.setText("0");
                    }
                });
    }

    private void fetchTotalDepartments() {
        db.collection("students")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    HashSet<String> uniqueDepartments = new HashSet<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String department = document.getString("department");
                        if (department != null) {
                            uniqueDepartments.add(department);
                        }
                    }
                    int totalDepartments = uniqueDepartments.size();
                    if (tvDepartmentCount != null) {
                        tvDepartmentCount.setText(String.valueOf(totalDepartments));
                    } else {
                        Toast.makeText(this, "Error: Department Count TextView not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching departments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (tvDepartmentCount != null) {
                        tvDepartmentCount.setText("0");
                    }
                });
    }

    public void goToDashboard(View view) {
        Toast.makeText(this, "You are on Dashboard", Toast.LENGTH_SHORT).show();
    }

    public void goToAttendance(View view) {
        Toast.makeText(this, "Navigating to Attendance", Toast.LENGTH_SHORT).show();
    }

    public void goToStudentClasses(View view) {
        showStudentClassesSubMenu();
    }

    public void goToSubjects(View view) {
        showSubjectsSubMenu();
    }

    public void goToStudents(View view) {
        showStudentsSubMenu();
    }

    public void goToResult(View view) {
        startActivity(new Intent(this, ResultsActivity.class));
    }

    public void goToAdminChangePassword(View view) {
        startActivity(new Intent(this, AdminPassChanActivity.class));
    }

    public void goToLogout(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void showStudentClassesSubMenu() {
        final String[] options = {"Create Semester", "Total Semester"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student Classes")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            startActivity(new Intent(AdminActivity.this, CreateSemesterActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(AdminActivity.this, TotalSemesterActivity.class));
                            break;
                    }
                });
        builder.create().show();
    }

    private void showSubjectsSubMenu() {
        final String[] options = {"Create Subject", "Total Subjects"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student Subjects")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            startActivity(new Intent(AdminActivity.this, CreateSubjectsActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(AdminActivity.this, TotalSubjectsActivity.class));
                            break;
                    }
                });
        builder.create().show();
    }

    private void showStudentsSubMenu() {
        final String[] options = {"Add Student", "Total Students"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student Details")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            startActivity(new Intent(AdminActivity.this, CreateStudentActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(AdminActivity.this, TotalStudentActivity.class));
                            break;
                    }
                });
        builder.create().show();
    }
}