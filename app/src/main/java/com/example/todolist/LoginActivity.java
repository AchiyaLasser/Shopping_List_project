package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

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
                                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        } else if (v == btnRegister) {
            EditText etEmail = findViewById(R.id.et_email);
            EditText etPassword = findViewById(R.id.et_password);
            if (etEmail.getText().toString().isEmpty() || etPassword.getText().toString().isEmpty())
                Toast.makeText(this, "please put username and password", Toast.LENGTH_SHORT).show();
            else {
                mAuth.createUserWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                else
                                    Toast.makeText(LoginActivity.this, "Register failed", Toast.LENGTH_LONG).show();
                            }
                        });
            }

        }
    }
}