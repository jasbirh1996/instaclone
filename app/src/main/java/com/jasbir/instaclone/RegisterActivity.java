package com.jasbir.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.mtp.MtpStorageInfo;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, email, pass;
    Button register;
    TextView login_page;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        login_page = findViewById(R.id.login_page);
        register = findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();

        login_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd =  new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String txt_username = username.getText().toString();
                String txt_fullname = fullname.getText().toString();
                String txt_email = email.getText().toString();
                String txt_pass = pass.getText().toString();

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_fullname) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_pass)){
                    Toast.makeText(RegisterActivity.this, "Please Enter all the Field ", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else if(txt_pass.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password length must be greater than 5 ", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else{

                    Register(txt_username,txt_fullname,txt_email,txt_pass);
                }

            }
        });

    }

    private void Register(final String username, final String fullname , String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser user = mAuth.getCurrentUser();
                    String userid = user.getUid();
                     reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id",userid);
                    hashMap.put("username",username.toLowerCase());
                    hashMap.put("Fullname",fullname);
                    hashMap.put("bio","");
                    hashMap.put("imageurl","https://firebasestorage.googleapis.com/v0/b/instaclone-ccaa6.appspot.com/o/files%2Ficonfinder_user-01_186382.png?alt=media&token=2cc5bb75-dc81-43db-805b-ac2c4a3d7977");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                pd.dismiss();
                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }

                        }
                    });





                }else{
                    Toast.makeText(RegisterActivity.this, "You can not Register with this email", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
