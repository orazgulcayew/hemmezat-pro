package com.gocreative.tm.hemmezat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {
    Button accept;
    EditText phoneNumber, nameSurname;
    String number, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        accept = findViewById(R.id.accept);
        phoneNumber = findViewById(R.id.number);
        nameSurname = findViewById(R.id.name_surname);

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                        intent.putExtra("number", number);
                        intent.putExtra("name", name);
                        startActivity(intent);

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        accept.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                number = "+993" + phoneNumber.getText().toString();
                builder.setMessage(number + " belgisi dogrymy").setPositiveButton("Hawa", dialogClickListener)
                        .setNegativeButton("Ýok", dialogClickListener);

                name = nameSurname.getText().toString();
                if (number.length() < 12) {
                    Toasty.error(RegisterActivity.this, "Telefon belgiňiz doly däl!", Toast.LENGTH_SHORT, true).show();
                }else if (name.isEmpty()){
                    Toasty.error(RegisterActivity.this, "Adyňyzy giriziň!", Toast.LENGTH_SHORT, true).show();
                }else{
                    builder.show();
                }
            }
        });
    }
}