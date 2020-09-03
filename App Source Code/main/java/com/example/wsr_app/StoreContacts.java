package com.example.wsr_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StoreContacts extends AppCompatActivity {

    TextView tvinfo,contact;
    EditText et_phone;
    Button add_more,next;
    FirebaseAuth fAuth;
    FirebaseUser user;
    DatabaseReference ref, currentref, contact_ref;
    String current_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_contacts);


        tvinfo = findViewById(R.id.tvinfo);
        contact = findViewById(R.id.Contact);
        et_phone = findViewById(R.id.et_phone);
        add_more = findViewById(R.id.add_more);
        next = findViewById(R.id.next);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();//("Member");//.child("Member");
        currentref = FirebaseDatabase.getInstance().getReference("Member").child(user.getUid()).child("EmergencyContacts");
        current_user_id = user.getUid();



        add_more.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String contact = et_phone.getText().toString();

             if (TextUtils.isEmpty(contact)){
                 et_phone.setError("Phone number Required!");
                 return;
             }

             Query query = ref.child("Member").orderByChild("Phone").equalTo(contact);//.(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())

              query.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {
                            for (DataSnapshot dss : dataSnapshot.getChildren()) {
                                Member member = dss.getValue(Member.class);
                                String contact_id = dss.getKey();
                                String contact_name = member.Name.toString();
                                String contact_phone = member.Phone.toString();


                                ContactsJoin e_contact = new ContactsJoin(contact_name,contact_phone);
                                currentref.child(contact_id).setValue(e_contact);
                                Toast.makeText(getApplicationContext(), " "+contact_name+" added. ", Toast.LENGTH_SHORT).show();
                                //   currentref.child(current_user_id).child("UserID").push().setValue(contact_id);
                                //   currentref.child(current_user_id).child("Name").push().setValue(contact_name);
                                //   currentref.child(current_user_id).child("Phone").push().setValue(contact_ph);
                            }

                        }else{
                            Toast.makeText(getApplicationContext(), "Error! This user has not registered on woman safety app", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error! adding this contact", Toast.LENGTH_SHORT).show();
                    }
              });




                       /* if(dataSnapshot.exists()){
                           Member member = null;
                            for(DataSnapshot dss : dataSnapshot.getChildren()){
                                 member = dss.getValue(Member.class);
                                contact_user_id = member.UserId;

                                contact_ref = FirebaseDatabase.getInstance().getReference().child("Member").child(current_user_id).child("MyContacts");


                                ContactsJoin contactsjoin = new ContactsJoin(contact_user_id);
                                currentref.child(user.getUid()).setValue(contactsjoin)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getApplicationContext(),"Emergency contact added!",Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getApplicationContext(),StoreContacts.class));
                                                }
                                            }
                                        });
                            }   */


                     }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }
}
