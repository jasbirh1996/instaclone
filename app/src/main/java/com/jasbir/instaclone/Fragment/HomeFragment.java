package com.jasbir.instaclone.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jasbir.instaclone.Adapter.PostAdapter;
import com.jasbir.instaclone.Models.Post;
import com.jasbir.instaclone.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {



   private RecyclerView recyclerView;
   private PostAdapter postAdapter;
   private List<Post> postList;

   private List<String> followingList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home,container,false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),postList);

        recyclerView.setAdapter(postAdapter);
        // Inflate the layout for this fragment

        checkFollowing();
        return view;
    }

    private void checkFollowing(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    followingList.add(snapshot.getKey());

                }
                readPost();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private  void readPost(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Post post = snapshot.getValue(Post.class);
                    for(String id : followingList){

                        if(post.getPublisher().equals(id)){

                            postList.add(post);


                        }

                    }

                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
