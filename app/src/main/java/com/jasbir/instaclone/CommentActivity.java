package com.jasbir.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jasbir.instaclone.Models.User;

import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {

    EditText comment;
    ImageView profile_pic;
    TextView post;

    String postid;
    String publisherid;

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //startActivity(new Intent(CommentActivity.this,MainActivity.class));
            }
        });

        comment = findViewById(R.id.comment);
        profile_pic = findViewById(R.id.profile_pic);
        post = findViewById(R.id.post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();

        postid= intent.getStringExtra("postid");
        publisherid= intent.getStringExtra("publisherid");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment.getText().toString().equals("")){

                    Toast.makeText(CommentActivity.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
                }else{
                    addComment();
                }
            }
        });
        getImage();

    }

    private void addComment(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postid);
        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("comment",comment.getText().toString());
        hashMap.put("publisher",firebaseUser.getUid());

        reference.push().setValue(hashMap);
        comment.setText("");
    }

    private void getImage(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(profile_pic);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
