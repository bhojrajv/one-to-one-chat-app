package com.example.whatsapclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindsfrndsActivity extends AppCompatActivity {
private Toolbar toolbar;
private RecyclerView recyclerView;
private ArrayList<String> contacts;
private FirebaseAuth mauth;
private Contacts contacts2;
private String currentId;
private DatabaseReference profileref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findsfrnds);
        toolbar=findViewById(R.id.finds_frntool);
        recyclerView=findViewById(R.id.findRecycle);
        setSupportActionBar(toolbar);
        mauth=FirebaseAuth.getInstance();
        currentId=mauth.getCurrentUser().getUid();
        profileref= FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions <Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
        .setQuery(profileref, Contacts.class)
        .build();
        FirebaseRecyclerAdapter<Contacts,FirebaseViewholder>adapter=new FirebaseRecyclerAdapter<Contacts, FirebaseViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FirebaseViewholder holder, final int position, @NonNull Contacts model) {
                holder.name.setText(model.getName());
                holder.status.setText(model.getStatus());
                Picasso.get().load(model.getImgStatus()).placeholder(R.drawable.profile_image).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       String usrId=getRef(position).getKey();
                        Intent intent=new Intent(FindsfrndsActivity.this,ProfileActivity.class);
                        intent.putExtra("id",usrId);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FirebaseViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.finds_frndlayout,parent,false);
                FirebaseViewholder firebaseViewholder=new FirebaseViewholder(view);
                return firebaseViewholder;
            }
        };
        recyclerView.setAdapter(adapter);
         adapter.startListening();
    }
 public  static class FirebaseViewholder extends RecyclerView.ViewHolder{
     CircleImageView imageView;
     TextView name,status;
     public FirebaseViewholder(@NonNull View itemView) {
         super(itemView);
         imageView=itemView.findViewById(R.id.profileimg);
         name=itemView.findViewById(R.id.Nm);
         status=itemView.findViewById(R.id.showstatus);

     }
 }

}
