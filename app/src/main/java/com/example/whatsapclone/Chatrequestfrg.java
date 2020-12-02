package com.example.whatsapclone;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chatrequestfrg extends Fragment {
    private RecyclerView recyclerView;
    private View contatcview;
   private DatabaseReference charReference,userRef,contactRef;
  private   String currentuserId;
   private FirebaseAuth mauth;


    public Chatrequestfrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_chatrequestfrg, container, false);
        // Inflate the layout for this fragment
        recyclerView=view.findViewById(R.id.chatfrg);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mauth= FirebaseAuth.getInstance();
        currentuserId=mauth.getCurrentUser().getUid();
        contactRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
        charReference= FirebaseDatabase.getInstance().getReference().child("Chare Request");
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts>options= new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(charReference.child(currentuserId),Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, FirbasechatViewholder> adapter
                =new FirebaseRecyclerAdapter<Contacts, FirbasechatViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FirbasechatViewholder holder, int position, @NonNull Contacts model) {
               // holder.itemView.findViewById(R.id.Acceptbtn).setVisibility(View.VISIBLE);
               // holder.itemView.findViewById(R.id.canceltbtn).setVisibility(View.VISIBLE);
                final String listid=getRef(position).getKey();
                DatabaseReference getref=getRef(position).child("request-type");
                getref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists());
                        {
                            String type= (String) dataSnapshot.getValue();
                            if(type.equals("received"))
                            {
                                userRef.child(listid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild("image"))
                                        {

                                           final String img=dataSnapshot.child("image").getValue().toString();

                                           Picasso.get().load(img).placeholder(R.drawable.profile_image).into(holder.img);
                                        }
                                        final String userNm2=dataSnapshot.child("name").getValue().toString();
                                        final String showstatus2=dataSnapshot.child("status").getValue().toString();
                                        //final String img=dataSnapshot.child("image").getValue().toString();
                                        holder.name.setText(userNm2);
                                        holder.usrstatus.setText(showstatus2);
                                        final CharSequence[]options=new CharSequence[]{"Accept","Cancel"};
                                         holder.itemView.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                                                         .setTitle( userNm2+ "Chat Request")
                                                         .setItems(options, new DialogInterface.OnClickListener() {
                                                             @Override
                                                             public void onClick(DialogInterface dialog, int which) {
                                                                 if(which==0)
                                                                 {
                                                                     contactRef.child(currentuserId).child(listid).child("Contacts")
                                                                             .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                         @Override
                                                                         public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        contactRef.child(listid).child(currentuserId).child("Contacts")
                                                                                .setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful())
                                                                                {
                                                                           charReference.child(currentuserId).child(listid)
                                                                                   .removeValue()
                                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                       @Override
                                                                                       public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful())
                                                                            {
                                                                                charReference.child(listid).child(currentuserId)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if(task.isSuccessful())
                                                                                                {
                                                                                                    Toast.makeText(getContext(), "Contact saved", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                                       }
                                                                                   });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                         }
                                                                     });
                                                                 }
                                                                 else if(which==1)
                                                                 {
                                                                     charReference.child(currentuserId).child(listid)
                                                                             .removeValue()
                                                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                 @Override
                                                                                 public void onComplete(@NonNull Task<Void> task) {
                                                                                     if(task.isSuccessful())
                                                                                     {
                                                                                         charReference.child(listid).child(currentuserId)
                                                                                                 .removeValue()
                                                                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                     @Override
                                                                                                     public void onComplete(@NonNull Task<Void> task) {
                                                                                                         if(task.isSuccessful())
                                                                                                         {
                                                                                                             Toast.makeText(getContext(), "Contact deleted", Toast.LENGTH_SHORT).show();
                                                                                                         }
                                                                                                     }
                                                                                                 });
                                                                                     }
                                                                                 }
                                                                             });
                                                                 }
                                                             }
                                                         });
                                                  builder.show();
                                             }
                                         });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else if(type.equals("sent"))
                            {
                                Button sent_rquest=holder.itemView.findViewById(R.id.Acceptbtn);
                                sent_rquest.setText("Request sent");
                             holder.itemView.findViewById(R.id.canceltbtn).setVisibility(View.INVISIBLE);
                                userRef.child(listid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild("image"))
                                        {

                                            final String img=dataSnapshot.child("image").getValue().toString();

                                            Picasso.get().load(img).placeholder(R.drawable.profile_image).into(holder.img);
                                        }
                                        final String userNm2=dataSnapshot.child("name").getValue().toString();
                                        final String showstatus2=dataSnapshot.child("status").getValue().toString();
                                        //final String img=dataSnapshot.child("image").getValue().toString();
                                        holder.name.setText(userNm2);
                                        holder.usrstatus.setText(showstatus2);
                                        final CharSequence[]options=new CharSequence[]{"Cancel Chate Request"};
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                                                        .setTitle("Request has Already sent")
                                                        .setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {

                                                                if(which==0)
                                                                {
                                                                    charReference.child(currentuserId).child(listid)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        charReference.child(listid).child(currentuserId)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if(task.isSuccessful())
                                                                                                        {
                                                                                                            Toast.makeText(getContext(), "You have chat request cancelled", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                                builder.show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public FirbasechatViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.finds_frndlayout,parent,false);
                FirbasechatViewholder firbasechatViewholder=new FirbasechatViewholder(view);
                return firbasechatViewholder;
            }

        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    private  static class FirbasechatViewholder extends RecyclerView.ViewHolder{
        Button accept,cancel;
        TextView name,usrstatus;
        CircleImageView img;
        public FirbasechatViewholder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.Nm);
            usrstatus=itemView.findViewById(R.id.showstatus);
            img=itemView.findViewById(R.id.profileimg);
            accept=itemView.findViewById(R.id.Acceptbtn);
            cancel=itemView.findViewById(R.id.canceltbtn);
        }
    }
}
