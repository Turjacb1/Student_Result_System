package com.turja.student_result;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CreateSemesterActivity extends AppCompatActivity {

    private EditText etSemesterName, etYearName;
    private Button btnCreate;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_semester);

        etSemesterName = findViewById(R.id.etSemesterName);
        etYearName = findViewById(R.id.etYearName);
        btnCreate = findViewById(R.id.btnCreate);

        db = FirebaseFirestore.getInstance();

        btnCreate.setOnClickListener(v -> {
            String semesterName = etSemesterName.getText() != null ? etSemesterName.getText().toString().trim() : "";
            String yearName = etYearName.getText() != null ? etYearName.getText().toString().trim() : "";

            if (semesterName.isEmpty() || yearName.isEmpty()) {
                Toast.makeText(this, "Please enter both semester and year", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> semesterData = new HashMap<>();
            semesterData.put("semesterName", semesterName);
            semesterData.put("yearName", yearName);

            db.collection("semesters")
                    .add(semesterData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Semester created successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
