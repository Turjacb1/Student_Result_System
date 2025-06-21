//package com.turja.student_result;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class AdminLoginActivity extends AppCompatActivity {
//
//    private EditText etUsername, etPassword;
//    private Button btnLogin;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin_login); // Ensure correct file name!
//
//        // Initialize components
//        etUsername = findViewById(R.id.etUsername);
//        etPassword = findViewById(R.id.etPassword);
//        btnLogin = findViewById(R.id.btnLogin);
//
//        btnLogin.setOnClickListener(v -> {
//            String username = etUsername.getText().toString().trim();
//            String password = etPassword.getText().toString().trim();
//
//            if (username.isEmpty() || password.isEmpty()) {
//                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Hardcoded credentials
//            String adminUsername = "admin";
//            String adminPassword = "admin123";
//
//            if (username.equals(adminUsername) && password.equals(adminPassword)) {
//                Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
//                startActivity(intent);
//                finish();
//            } else {
//                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}


package com.turja.student_result;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etPhoneNumber;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize components
        etPhoneNumber = findViewById(R.id.etUsername);
        btnLogin = findViewById(R.id.btnLogin);

        // Update hint and input type
        etPhoneNumber.setHint("Phone Number (e.g., +8801XXXXXXXXX)");
        etPhoneNumber.setInputType(android.text.InputType.TYPE_CLASS_PHONE);

        btnLogin.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // For simplicity, assume phone number format is validated (e.g., +880 followed by 10 digits)
            if (!phoneNumber.matches("^\\+8801[0-9]{9}$")) {
                Toast.makeText(this, "Invalid phone number format. Use +8801XXXXXXXXX", Toast.LENGTH_SHORT).show();
                return;
            }

            // Start phone authentication
            sendVerificationCode(phoneNumber);
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60, // Timeout duration in seconds
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verificationId = s;
                        resendToken = forceResendingToken;
                        Toast.makeText(AdminLoginActivity.this, "Verification code sent", Toast.LENGTH_SHORT).show();

                        // Launch OTP verification activity
                        Intent intent = new Intent(AdminLoginActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("verificationId", verificationId);
                        intent.putExtra("phoneNumber", phoneNumber);
                        startActivity(intent);
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Auto-retrieval of verification code (rare case)
                        signInWithCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(AdminLoginActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    // This method would be called from OtpVerificationActivity after OTP entry
    public void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}