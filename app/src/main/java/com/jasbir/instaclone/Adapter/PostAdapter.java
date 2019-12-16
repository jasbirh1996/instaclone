package com.jasbir.instaclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRegistrar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jasbir.instaclone.CommentActivity;
import com.jasbir.instaclone.Models.Post;
import com.jasbir.instaclone.Models.User;
import com.jasbir.instaclone.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder> {

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);

        return new PostAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(position);

        //Picasso.get().load(post.getPostImage()).into(holder.post_img);
       Glide.with(mContext).load(post.getPostImage()).into(holder.post_img);

        if(post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }else{
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        publisherInfo(holder.user_dp,holder.username,post.getPublisher());
        isLike(post.getPostid(),holder.likes);
        likeCount(holder.likes_count,post.getPostid());

        getComment(post.getPostid(),holder.comment);

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.likes.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                }else{

                    FirebaseDatabase.getInstance().getReference("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();


                }
            }
        });

         holder.ic_comment.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(mContext, CommentActivity.class);
                 intent.putExtra("postid",post.getPostid());
                 intent.putExtra("publisherid",post.getPostid());
                 mContext.startActivity(intent);
             }
         });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPostid());
                mContext.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public ImageView post_img , user_dp, likes,ic_comment, inbox,save;
        TextView username,likes_count,post,publisher,description;
        TextView comment;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            post_img = itemView.findViewById(R.id.post_img);
            user_dp = itemView.findViewById(R.id.user_dp);
            ic_comment = itemView.findViewById(R.id.ic_comment);
            inbox = itemView.findViewById(R.id.inbox);
            username = itemView.findViewById(R.id.username);
            likes_count = itemView.findViewById(R.id.likes_count);
            likes = itemView.findViewById(R.id.likes);
            comment = itemView.findViewById(R.id.comment);
            post = itemView.findViewById(R.id.post);
         //   publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);

        }
    }


    private void getComment(String postid, final TextView comment){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String viewComment;
                if(dataSnapshot.getChildrenCount()==1){
                    viewComment="Comment";
                }else{
                    viewComment = "Comments";
                }
                comment.setText("View All "+dataSnapshot.getChildrenCount()+" "+viewComment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void isLike(String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists()){

                    imageView.setImageResource(R.drawable.ic_heart);
                    imageView.setTag("liked");
                }else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void likeCount(final TextView like_count, String postid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                like_count.setText(dataSnapshot.getChildrenCount()+"likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void publisherInfo(final ImageView user_dp, final TextView username,  String userid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImageurl()).into(user_dp);
                username.setText(user.getUsername());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }





}
