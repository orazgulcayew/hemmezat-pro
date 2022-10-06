package com.gocreative.tm.hemmezatproadmin;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterActivity extends AppCompatActivity {
    Button accept;
    EditText adminEmail, passwordV;
    String email, password;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        accept = findViewById(R.id.accept);
        adminEmail = findViewById(R.id.admin_email);
        passwordV = findViewById(R.id.admin_password);

        auth = FirebaseAuth.getInstance();

        accept.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                email = adminEmail.getText().toString();
                password = passwordV.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()){
                    signInAdmin(email, password);
                }else{
                    Toast.makeText(RegisterActivity.this, "Maglumatlar doldur!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signInAdmin(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent intent =  new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Dogry däl!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}