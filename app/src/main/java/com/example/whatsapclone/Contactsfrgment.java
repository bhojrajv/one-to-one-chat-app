package com.example.whatsapclone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class Contactsfrgment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView recyclerView;
    private View contatcview2;
    private String currentid;
    private DatabaseReference reference,userref;
    private FirebaseAuth mauth;

    public Contactsfrgment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       contatcview2= inflater.inflate(R.layout.fragment_contactsfrgment,container,false);
       recyclerView=contatcview2.findViewById(R.id.contactRec);
       LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
       recyclerView.setLayoutManager(linearLayoutManager);
        mauth=FirebaseAuth.getInstance();
        currentid=mauth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentid);
        userref= FirebaseDatabase.getInstance().getReference().child("Users");
        // Inflate the layout for this fragment
        return contatcview2;

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(reference,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, FirebaasecontView> adapter=
                new FirebaseRecyclerAdapter<Contacts, FirebaasecontView>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FirebaasecontView holder, int position, @NonNull final Contacts model) {
                        String userid=getRef(position).getKey();
                        userref.child(userid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists())
                                {
                                    if(dataSnapshot.child("userStatus").hasChild("state"))
                                    {
                                        String status2=dataSnapshot.child("userStatus").child("state").getValue().toString();
                                        String date=dataSnapshot.child("userStatus").child("date").getValue().toString();
                                        String time=dataSnapshot.child("userStatus").child("time").getValue().toString();
                                        if(status2.equals("online"))
                                        {
                                            holder.userofOnstatus.setVisibility(View.VISIBLE);
                                        }
                                        else if(status2.equals("offline"))
                                        {
                                            holder.userofOnstatus.setVisibility(View.INVISIBLE);
                                        }

                                    }
                                    if(dataSnapshot.hasChild("image")){
                                        final String nm=dataSnapshot.child("name").getValue().toString();
                                        final String status=dataSnapshot.child("status").getValue().toString();
                                        final String img=dataSnapshot.child("image").getValue().toString();
                                        holder.contName.setText(nm);
                                        holder.contstatus.setText(status);
                                        Picasso.get().load(img).placeholder(R.drawable.profile_image).into(holder.imageView);

                                    }
                                    else {
                                        String nm=dataSnapshot.child("name").getValue().toString();
                                        String status=dataSnapshot.child("status").getValue().toString();
                                        holder.contName.setText(nm);
                                        holder.contstatus.setText(status);
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
                    public FirebaasecontView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        LayoutInflater layoutInflater= (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                       View  view=layoutInflater.inflate(R.layout.finds_frndlayout,parent,false);
                        FirebaasecontView firebaasecontView=new FirebaasecontView(view);
                        return firebaasecontView;
                    }
                };
      recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    public static class FirebaasecontView extends  RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView contName,contstatus;
        ImageView userofOnstatus;
        public FirebaasecontView(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.profileimg);
            contName=itemView.findViewById(R.id.Nm);
            contstatus=itemView.findViewById(R.id.showstatus);
            userofOnstatus=itemView.findViewById(R.id.statusimg);
        }
    }
}
