package com.turja.student_result;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateStudentActivity extends AppCompatActivity {

    private EditText etStudentName, etStudentId, etRegistrationNumber, etSession, etEmail, etMobileNumber, etZila;
    private Spinner spGender, spReligion, spDepartment;
    private Button btnAdd;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Bind views
        etStudentName = findViewById(R.id.etStudentName);
        etStudentId = findViewById(R.id.etStudentId);
        etRegistrationNumber = findViewById(R.id.etRegistrationNumber);
        etSession = findViewById(R.id.etSession);
        etEmail = findViewById(R.id.etEmail);
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etZila = findViewById(R.id.etZila);
        spGender = findViewById(R.id.spGender);
        spReligion = findViewById(R.id.spReligion);
        spDepartment = findViewById(R.id.spDepartment); // Add department spinner
        btnAdd = findViewById(R.id.btnAdd);

        // Setup spinners
        setupSpinners();

        btnAdd.setOnClickListener(v -> {
            String studentName = etStudentName.getText().toString().trim();
            String studentId = etStudentId.getText().toString().trim();
            String registrationNumber = etRegistrationNumber.getText().toString().trim();
            String session = etSession.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String mobileNumber = etMobileNumber.getText().toString().trim();
            String zila = etZila.getText().toString().trim();
            String gender = spGender.getSelectedItem().toString();
            String religion = spReligion.getSelectedItem().toString();
            String department = spDepartment.getSelectedItem().toString();

            if (studentName.isEmpty() || studentId.isEmpty() || registrationNumber.isEmpty() ||
                    session.isEmpty() || email.isEmpty() || mobileNumber.isEmpty() || zila.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> student = new HashMap<>();
            student.put("studentName", studentName);
            student.put("studentId", studentId);
            student.put("registrationNumber", registrationNumber);
            student.put("session", session);
            student.put("email", email);
            student.put("mobileNumber", mobileNumber);
            student.put("gender", gender);
            student.put("department", department); // Add department
            student.put("zila", zila);
            student.put("religion", religion);

            db.collection("students")
                    .add(student)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Student added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error adding student: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> religionAdapter = ArrayAdapter.createFromResource(this,
                R.array.religion_array, android.R.layout.simple_spinner_item);
        religionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReligion.setAdapter(religionAdapter);

        ArrayAdapter<CharSequence> departmentAdapter = ArrayAdapter.createFromResource(this,
                R.array.departments_array, android.R.layout.simple_spinner_item);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDepartment.setAdapter(departmentAdapter);
    }
}