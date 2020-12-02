package com.example.whatsapclone;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chatsfragment extends Fragment {
    private RecyclerView recyclerView;
   private View contatcview;
    private DatabaseReference charRef,userRef;
  private static  String currentid="";
    private FirebaseAuth mauth;
    //private String date,time;
    public Chatsfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    contatcview=inflater.inflate(R.layout.fragment_chatsfragment, container, false);
       recyclerView=contatcview.findViewById(R.id.chatreqRec);
        mauth=FirebaseAuth.getInstance();
        try {
            FirebaseUser firebaseUser =mauth.getCurrentUser();
            if(firebaseUser==null)
            {
                Intent intent=new Intent(getContext(),LoginActivity.class);
                getContext().startActivity(intent);
            }
            else {
                currentid=firebaseUser.getUid();
            }
        }catch (Exception e)
        {
            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


       charRef=FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentid);
       userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
        // Inflate the layout for this fragment
         // retreiveInfo();
        return contatcview;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts>options=new FirebaseRecyclerOptions.Builder()
                .setQuery(charRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,PrivatechatClass>adapter=
                new FirebaseRecyclerAdapter<Contacts, PrivatechatClass>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final PrivatechatClass holder, int position, @NonNull Contacts model) {
           final String list_ui_id=getRef(position).getKey();
                        final String[] image = {"default_img"};
                  userRef.child(list_ui_id).addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if (dataSnapshot.exists())
                         {
                             if(dataSnapshot.hasChild("image"))
                             {
                                 image[0] =dataSnapshot.child("image").getValue().toString();
                                 Picasso.get().load(image[0]).placeholder(R.drawable.profile_image).into(holder.userImage);
                             }
                             final String name=dataSnapshot.child("name").getValue().toString();
                             String usrstatus=dataSnapshot.child("status").getValue().toString();
                             holder.UserName.setText(name);

                             holder.Userstatus.setText("Last seen:"+"\n"+"date:"+"time");
                             if(dataSnapshot.child("userStatus").hasChild("state"))
                             {
                                String status=dataSnapshot.child("userStatus").child("state").getValue().toString();
                               String date=dataSnapshot.child("userStatus").child("date").getValue().toString();
                               String  time=dataSnapshot.child("userStatus").child("time").getValue().toString();
                                 if(status.equals("online"))
                                 {
                                     holder.Userstatus.setText(status);
                                 }
                                 else if(status.equals("offline"))
                                 {
                                    // holder.Userstatus.setText("offline");
                                     holder.Userstatus.setText("Last seen:"+" "+date+" "+time);
                                 }

                             }
                             else {
                                 holder.Userstatus.setText("offline");

                             }
                             holder.itemView.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     Intent intent=new Intent(getContext(),PrivateChat.class);
                                     intent.putExtra("current_user",list_ui_id);
                                     intent.putExtra("current_userName",name);
                                     intent.putExtra("current_usrimg",image);
                                     startActivity(intent);
                                 }
                             });
                         }
                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });

                    }

                    @NonNull
                    @Override
                    public PrivatechatClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                      View   contatcview2=LayoutInflater.from(getContext()).inflate(R.layout.finds_frndlayout,parent,false);
                        return new PrivatechatClass(contatcview2);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();;

    }

    private static class PrivatechatClass extends RecyclerView.ViewHolder {
        TextView Userstatus,UserName;
        CircleImageView userImage;
        public PrivatechatClass(@NonNull View itemView) {
            super(itemView);
            UserName=itemView.findViewById(R.id.Nm);
            Userstatus=itemView.findViewById(R.id.showstatus);
            userImage=itemView.findViewById(R.id.profileimg);
        }
    }
}
