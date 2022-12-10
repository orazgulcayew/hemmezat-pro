package com.gocreative.team.hemmezat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.gocreative.tm.hemmezat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class OtpActivity extends AppCompatActivity {
    Button accept, wrongNumber;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId, userPhoneNumber, name;
    private PinView pinFromUser;
    private TextView info;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        accept = findViewById(R.id.verify_button);
        pinFromUser = findViewById(R.id.verification_pin);
        wrongNumber = findViewById(R.id.wrong_number_button);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Biraz garaşyň...");

        mAuth = FirebaseAuth.getInstance();

        userPhoneNumber = getIntent().getStringExtra("number");
        name = getIntent().getStringExtra("name");

        // Setting phone number to text view
        info = findViewById(R.id.verification_info_text);
        String infoText = userPhoneNumber + " " + info.getText().toString();
        info.setText(infoText);

        wrongNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OtpActivity.this, RegisterActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null){
                    pinFromUser.setText(code);
                    verifyCode(code);
                }
            }
            @Override
            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                Toasty.error(OtpActivity.this, "Näsazlyk ýüze çykdy!", Toast.LENGTH_SHORT).show();
                Log.d("Phone otp", "onVerificationFailed: " + e.getMessage());
            }
            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(s, token);
                mVerificationId = s;
            }

        };
        sendVerificationCode(userPhoneNumber);
    }
    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void verifyCode(String code) {
        progressDialog.show();
        if(mVerificationId != null){
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = fStore.collection("users").document(currentUid);

                    Map<String, Object> info = new HashMap<>();
                    info.put("name", name);
                    info.put("user_id", currentUid);
                    info.put("phone_number", userPhoneNumber);

                    documentReference.set(info, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finishAffinity();
                        }
                    });

                } else{
                    progressDialog.dismiss();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        Toasty.error(OtpActivity.this, "Tassyklaýyşda näsazlyk ýüze çykdy!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public void verifyManually(View view){
        String code = pinFromUser.getText().toString().trim();
        if (!(code.length() < 6)){
            if (mVerificationId != null){
                verifyCode(code);
            }else{
                pinFromUser.setText("");
                Toasty.warning(OtpActivity.this, "Biraz garaşyň... (Wait a minute...)", Toasty.LENGTH_SHORT).show();

            }
        }
        else{
            Toast.makeText(this, "Tassyklaýyş kodyny doly giriziň!", Toast.LENGTH_SHORT).show();
        }
    }
}