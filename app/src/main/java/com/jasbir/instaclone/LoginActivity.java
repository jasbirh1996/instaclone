package com.jasbir.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    TextView txt_signup;
    Button login;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        txt_signup = findViewById(R.id.txt_signup);
        login = findViewById(R.id.login);
        mAuth = FirebaseAuth.getInstance();

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txt_email = email.getText().toString();
                String txt_pass = password.getText().toString();

                if(TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_pass)){
                    Toast.makeText(LoginActivity.this, "Please Enter all the Field", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.signInWithEmailAndPassword(txt_email,txt_pass)
                            .addOnCompleteListener(LoginActivity.this,new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                                .child("Users").child(mAuth.getCurrentUser().getUid());


                                      reference.addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                              Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                              startActivity(intent);
                                              finish();
                                          }

                                          @Override
                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                          }
                                      });


                                    }else{
                                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }

            }
        });


    }
}
