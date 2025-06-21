package com.turja.student_result;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdminPassChanActivity extends AppCompatActivity {

    private EditText etAdminUsername, etPresentPassword, etNewPassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pass_chan);

        // Initialize components
        etAdminUsername = findViewById(R.id.etAdminUsername);
        etPresentPassword = findViewById(R.id.etPresentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v -> {
            String adminUsername = etAdminUsername.getText().toString().trim();
            String presentPassword = etPresentPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();

            if (adminUsername.isEmpty() || presentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hardcoded credentials from AdminLoginActivity
            String validUsername = "admin";
            String validPassword = "admin123";

            if (adminUsername.equals(validUsername) && presentPassword.equals(validPassword)) {
                // Show confirmation dialog
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Password Change")
                        .setMessage("Are you sure you want to change the password to " + newPassword + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Simulate password change (in a real app, update Firebase Auth or a secure backend)
                            Toast.makeText(this, "Password changed successfully to " + newPassword, Toast.LENGTH_SHORT).show();
                            // For demo, you might want to update a local variable or navigate back
                            startActivity(new Intent(this, AdminActivity.class));
                            finish();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .setCancelable(true)
                        .show();
            } else {
                Toast.makeText(this, "Invalid username or present password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}