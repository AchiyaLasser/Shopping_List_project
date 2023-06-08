package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    FirebaseAuth mAuth;
    Button btnRegister, btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        btnLogIn = findViewById(R.id.btn_log_in);
        btnRegister = findViewById(R.id.btn_register);
        btnLogIn.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnLogIn.setOnTouchListener(this);
        btnRegister.setOnTouchListener(this);
    }

    @Override
    public void onStart() { // when enter to LoginActivity check if there a user connected
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogIn) {
            EditText etEmail = findViewById(R.id.et_email);
            EditText etPassword = findViewById(R.id.et_password);
            if (etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty())
                Toast.makeText(this, "please put username and password", Toast.LENGTH_SHORT).show();
            else {
                mAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                else
                                    Toast.makeText(LoginActivity.this, "Email or password is incorrect. password need to be 6 digits or above", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
        else if (v == btnRegister) {
            EditText etEmail = findViewById(R.id.et_email);
            EditText etPassword = findViewById(R.id.et_password);
            if (etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty())
                Toast.makeText(this, "please put username and password", Toast.LENGTH_SHORT).show();
            else {
                mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
                                    DatabaseReference usersRef = database.getReference("users");

                                    // Store the email and user ID
                                    String email = mAuth.getCurrentUser().getEmail();
                                    String userId = mAuth.getCurrentUser().getUid();

                                    User user = new User(email, userId);

                                    usersRef.push().setValue(user)
                                            .addOnSuccessListener(aVoid -> {
                                                // Successfully stored the email and user ID
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            })
                                            .addOnFailureListener(e -> {
                                                // Failed to store the email and user ID
                                                Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                            });
                                } else
                                    Toast.makeText(LoginActivity.this, "Please provide valid Email and password. password need to be 6 digits or above", Toast.LENGTH_LONG).show();
                            }
                        });
            }

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view == btnLogIn) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Button is being touched
                    btnLogIn.setAlpha(0.5f); // Set alpha to 0.5 (half-transparent)
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Touch is released or canceled
                    btnLogIn.setAlpha(1.0f); // Restore original alpha (fully opaque)
                    break;
            }
        }
        else if(view == btnRegister){
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Button is being touched
                    btnRegister.setAlpha(0.5f); // Set alpha to 0.5 (half-transparent)
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Touch is released or canceled
                    btnRegister.setAlpha(1.0f); // Restore original alpha (fully opaque)
                    break;
            }
        }

        return false;
    }
}