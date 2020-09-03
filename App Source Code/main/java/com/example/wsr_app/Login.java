package com.example.wsr_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static androidx.core.content.ContextCompat.getSystemService;

public class Login extends AppCompatActivity {
    TextView CreateText;
    EditText Email,Password;
    Button login_btn,register_btn;
    CheckBox check_box2;
    ProgressBar progressBar2;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.Email) ;
        Password = findViewById(R.id.Password);
        check_box2 = findViewById(R.id.checkBox2);
        login_btn = findViewById(R.id.Llogin_btn) ;
        register_btn = findViewById(R.id.Lregister_btn) ;
        CreateText = findViewById(R.id.quest);
        progressBar2 = findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();




        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String v_email = Email.getText().toString().trim();
                String v_pass = Password.getText().toString().trim();

                if (TextUtils.isEmpty(v_email)) {
                    Email.setError("Email Field Required!");
                    return;
                }
                if (TextUtils.isEmpty(v_pass)) {
                    Email.setError("Password Field Required!");
                    return;
                }
                if (v_pass.length() < 8) {
                    Email.setError("Strong 8 Character Password Required!");
                    return;
                }

                progressBar2.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(v_email,v_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(check_box2.isChecked()){
                                Toast.makeText(Login.this, "Logged In!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else{
                                Toast.makeText(Login.this, "Logged In!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),General_User_Activity.class));
                            }
                        } else {
                            Toast.makeText(Login.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar2.setVisibility(View.GONE);
                        }
                    }
                });

            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                finish();
            }
        });


        CreateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showRecoverPasswordDialog();
            }
        });


    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 String email = emailEt.getText().toString().trim();
                 beginRecovery(email);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void beginRecovery(String email) {
        progressBar2.setVisibility(View.VISIBLE);
        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar2.setVisibility(View.GONE);
                    Toast.makeText(Login.this,"Email Sent",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
