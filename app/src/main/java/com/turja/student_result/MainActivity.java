package com.turja.student_result;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToHome(View view) {
        // Implement Home activity navigation
        Toast.makeText(this, "Navigating to Home", Toast.LENGTH_SHORT).show();
    }

    public void goToStudents(View view) {
        // Implement Students activity navigation
        startActivity(new Intent(this, StudentLoginActivity.class));
    }

    public void goToAdmin(View view) {
        // Navigate to Admin Login activity
        startActivity(new Intent(this, AdminLoginActivity.class));
    }
}