package com.example.pemmob_fp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);  // Set login layout

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind UI elements
        EditText etUsernameOrEmail = findViewById(R.id.etUsernameOrEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnGoToRegister = findViewById(R.id.btnGoToRegister);  // Button to go to register

        // Handle Login button click
        btnLogin.setOnClickListener(v -> {
            String usernameOrEmail = etUsernameOrEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(usernameOrEmail) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Try logging in
            if (isValidEmail(usernameOrEmail)) {
                // Proceed with email login
                loginUser(usernameOrEmail, password);
            } else {
                // If it's not an email, check if it's a username
                loginWithUsername(usernameOrEmail, password);
            }
        });

        // Handle "Go to Reg@ister" button click
        btnGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);  // Navigate to RegisterActivity
        });
    }

    private boolean isValidEmail(String email) {
        // Check if the input is a valid email address
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        // Navigate to HomeActivity or another screen
                        Intent intent = new Intent(LoginActivity.this,MainPageActivity.class);
                        startActivity(intent);
                        finish();  // Close LoginActivity
                    } else {
                        // Login failed
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginWithUsername(String username, String password) {
        // Convert input username to lowercase to handle case-insensitive search
        String usernameLowerCase = username.toLowerCase();

        // Query Firestore to get the email associated with the username
        db.collection("users")
                .whereEqualTo("username", usernameLowerCase)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            String email = task.getResult().getDocuments().get(0).getString("email");
                            if (email != null) {
                                loginUser(email, password);
                            } else {
                                Toast.makeText(LoginActivity.this, "Email associated with username is missing", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("FirestoreQuery", "No matching username found.");
                            Toast.makeText(LoginActivity.this, "Username not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle failure
                        Log.e("FirestoreQuery", "Query failed: " + task.getException().getMessage());
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
