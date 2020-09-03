package com.example.wsr_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
   // boolean has_ring;
    EditText Name,Email,Password,Phone,Address,Pincode;
    Button mRegister_btn;
    TextView mCreateText;
    CheckBox check_box;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    DatabaseReference ref;
    Member member;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

  //      Intent myintent = getIntent();
  //      if(myintent!=null) {
  //          has_ring = myintent.getBooleanExtra("hasring",false);
        //     }

        Name = (EditText)findViewById(R.id.Name);
        Email = (EditText)findViewById(R.id.Email) ;
        Password = (EditText) findViewById(R.id.Password);
        Phone = (EditText)findViewById(R.id.Phone) ;
        Address = (EditText)findViewById(R.id.Address);
        Pincode = (EditText)findViewById(R.id.Pincode);
        check_box = findViewById(R.id.checkBox2);
        mRegister_btn = (Button)findViewById(R.id.Register_btn) ;
        mCreateText = (TextView)findViewById(R.id.CreateText);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);


        ref = FirebaseDatabase.getInstance().getReference().child("Member");
        fAuth = FirebaseAuth.getInstance();

        mRegister_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String v_name = Name.getText().toString().trim();
                final String v_email = Email.getText().toString().trim();
                final String v_pass = Password.getText().toString().trim();
                final String v_ph = Phone.getText().toString().trim();
                final String v_add = Address.getText().toString().trim();
                final String v_code = Pincode.getText().toString().trim();

                if (TextUtils.isEmpty(v_name)) {
                    Name.setError("Name Field Required!");
                    return;
                }
                if (TextUtils.isEmpty(v_email)) {
                    Email.setError("Email Field Required!");
                    return;
                }
                if (TextUtils.isEmpty(v_pass)) {
                    Password.setError("Password Field Required!");
                    return;
                }
                if (v_pass.length() < 8) {
                    Password.setError("Strong 8 Character Password Required!");
                    return;
                }
                if (TextUtils.isEmpty(v_ph)) {
                    Phone.setError("Phone number Required!");
                    return;
                }
                if (TextUtils.isEmpty(v_add)) {
                    Address.setError("Address Required!");
                    return;
                }
                if (TextUtils.isEmpty(v_code)) {
                    Pincode.setError("Pincode Required!");
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(v_email,v_pass).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())  {

                            Member member = new Member(v_name,v_email,v_pass,v_ph,v_add,v_code);
                       //     member.setName(Name.getText().toString().trim());
                       //     member.setEmail(Email.getText().toString().trim());
                       //     member.setPassword(Password.getText().toString().trim());
                       //     member.setPhone(Phone.getText().toString().trim());
                       //     member.setAddress(Address.getText().toString().trim());
                       //     member.setPincode(Pincode.getText().toString().trim());


                       //     FirebaseDatabase.getInstance().getReference("Member");
                            FirebaseUser user = fAuth.getCurrentUser();
                            String userId = user.getUid();

                        //    member.setUserId(userId);

                            ref.child(userId).setValue(member);

                       /*     ref.child(userId).child("UserID").push().setValue(userId);
                            ref.child(userId).child("Name").push().setValue(v_name);
                            ref.child(userId).child("Email").push().setValue(v_email);
                            ref.child(userId).child("Password").push().setValue(v_pass);
                            ref.child(userId).child("Phone").push().setValue(v_ph);
                            ref.child(userId).child("Address").push().setValue(v_add);
                            ref.child(userId).child("Pincode").push().setValue(v_code);   */

                            progressBar.setVisibility(View.GONE);

                         if(check_box.isChecked()){
                             Toast.makeText(Register.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                             startActivity(new Intent(getApplicationContext(),StoreContacts.class));
                         } else{
                             Toast.makeText(Register.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                             startActivity(new Intent(getApplicationContext(),General_User_Activity.class));
                         }


                        } else {
                            Toast.makeText(Register.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        mCreateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }
}