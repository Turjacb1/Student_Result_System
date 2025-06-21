package com.turja.student_result;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


    }

    // Navigation methods for menu items
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
        Toast.makeText(this, "Navigating to Change Password", Toast.LENGTH_SHORT).show();
    }

    public void goToLogout(View view) {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Clear session (e.g., Firebase Auth if used)
                    // For example: FirebaseAuth.getInstance().signOut();
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class)); // Adjust to your login activity
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
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(new Intent(AdminActivity.this, CreateSemesterActivity.class));
                                break;
                            case 1:
                                startActivity(new Intent(AdminActivity.this, TotalSemesterActivity.class));
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void showSubjectsSubMenu() {
        final String[] options = {"Create Subject", "Total Subjects"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student Subjects")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(new Intent(AdminActivity.this, CreateSubjectsActivity.class));
                                break;
                            case 1:
                                startActivity(new Intent(AdminActivity.this, TotalSubjectsActivity.class));
                                break;
                        }
                    }
                });
        builder.create().show();
    }



    private void showStudentsSubMenu() {
        final String[] options = {"Add Student", "Total Students"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student Details")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startActivity(new Intent(AdminActivity.this, CreateStudentActivity.class));
                                break;
                            case 1:
                                startActivity(new Intent(AdminActivity.this, TotalStudentActivity.class));
                                break;
                        }
                    }
                });
        builder.create().show();
    }
}