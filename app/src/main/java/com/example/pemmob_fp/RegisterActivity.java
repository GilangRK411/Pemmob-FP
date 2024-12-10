package com.example.pemmob_fp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register); // Set layout sesuai file XML Anda

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind UI elements
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnGoToLogin = findViewById(R.id.btnGoToLogin);

        // Handle Register button click
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Register user with Firebase
            registerUser(email, username, password);
        });

        // Handle "Go to Login" button click
        btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent); // Navigate to LoginActivity
        });
    }

    private void registerUser(String email, String username, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Proses berhasil
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, email, username);
                    } else {
                        // Cek error message lebih rinci
                        Exception e = task.getException();
                        if (e != null) {
                            Log.e("FirebaseAuthError", "Error: " + e.getMessage(), e);
                        }
                        Toast.makeText(RegisterActivity.this, "Gagal Daftar: " + (e != null ? e.getMessage() : "Error tidak diketahui"), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void saveUserToFirestore(String userId, String email, String username) {
        // Create user data
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("username", username);

        // Save data to Firestore
        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    // Navigate to LoginActivity or HomeActivity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
