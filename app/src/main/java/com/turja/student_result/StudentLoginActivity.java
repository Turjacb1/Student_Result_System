package com.turja.student_result;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class StudentLoginActivity extends AppCompatActivity {

    private EditText etStudentId, etSession;
    private Button btnLogin;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        etStudentId = findViewById(R.id.etStudentId);
        etSession = findViewById(R.id.etSession);
        btnLogin = findViewById(R.id.btnLogin);

        // Set up login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentId = etStudentId.getText().toString().trim();
                String session = etSession.getText().toString().trim();

                // Basic validation
                if (studentId.isEmpty() || session.isEmpty()) {
                    Toast.makeText(StudentLoginActivity.this, "Please enter both ID and Session", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if it's an admin login (e.g., hardcoded admin ID)
                if (studentId.equals("admin123")) {
                    Intent intent = new Intent(StudentLoginActivity.this, ResultDisplayActivity.class);
                    intent.putExtra("isAdmin", true); // Flag for admin mode
                    startActivity(intent);
                    finish();
                    return;
                }

                // Validate student credentials
                validateStudentCredentials(studentId, session);
            }
        });
    }

    private void validateStudentCredentials(String studentId, String session) {
        db.collection("results")
                .whereEqualTo("studentId", studentId)
                .whereEqualTo("session", session)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (!querySnapshot.isEmpty()) {
                            // Get the first matching document to extract department
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String department = document.getString("department");
                                if (department != null) {
                                    Intent intent = new Intent(StudentLoginActivity.this, ResultDisplayActivity.class);
                                    intent.putExtra("studentId", studentId);
                                    intent.putExtra("department", department);
                                    intent.putExtra("isAdmin", false); // Normal student mode
                                    startActivity(intent);
                                    finish();
                                    return;
                                }
                            }
                        } else {
                            Toast.makeText(StudentLoginActivity.this, "Invalid ID or Session", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StudentLoginActivity.this, "Error checking credentials: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}