package com.example.whatsapclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
private  String recieverId,curren_status;
private CircleImageView profileimg;
private TextView usrNm,UserStatus;
private DatabaseReference reference,chatref,contactref,notification;
private FirebaseAuth mauth;
private Button sendmss,declinebtn;
private String senderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sendmss=findViewById(R.id.sendbtn);
        declinebtn=findViewById(R.id.declienbtn);
        recieverId=getIntent().getExtras().get("id").toString();
        Toast.makeText(this, ""+recieverId, Toast.LENGTH_SHORT).show();
        mauth=FirebaseAuth.getInstance();
        senderId=mauth.getCurrentUser().getUid();
         profileimg=findViewById(R.id.profile_act);
         usrNm=findViewById(R.id.userNm);
         UserStatus=findViewById(R.id.userstatus);
        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        chatref=FirebaseDatabase.getInstance().getReference().child("Chare Request");
        contactref=FirebaseDatabase.getInstance().getReference().child("Contacts");
        notification= FirebaseDatabase.getInstance().getReference().child("notification");
        curren_status="new";
         retrieveInfo();
        manageChatreq();
    }

    private void manageChatreq() {
        chatref.child(senderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(recieverId)){
                    String rquest_type=dataSnapshot.child(recieverId).child("request-type").getValue().toString();
                    if(rquest_type.equals("sent")){
                        curren_status="request-sent";
                        sendmss.setText("Cancel request");
                    }
                    else if(rquest_type.equals("received")){
                        curren_status="request-received";
                        sendmss.setText("Accept Request");
                        declinebtn.setVisibility(View .VISIBLE);
                        declinebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelRequest();
                            }
                        });
                    }
                }
                else {
                    contactref.child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(recieverId)){
                                curren_status="friends";
                                sendmss.setText("Remove this contact");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       if(!senderId.equals(recieverId)){
           sendmss.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                  if(curren_status.equals("new")){
                      sendmss.setEnabled(false);
                      sendRequest();
                  }
                  else if(curren_status.equals("request-sent")){
                      cancelRequest();
                  }
                  else if(curren_status.equals("request-received")){
                      acceptRequest();
                  }
                  else if(curren_status.equals("friends"))
                  {
                      contactref.child(senderId).child(recieverId)
             .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful())
                             {
                                 contactref.child(recieverId).child(senderId)
                               .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful())
                                   {
                                       curren_status="new";
                                       sendmss.setEnabled(true);
                                       sendmss.setText("Send Message");
                                   }

                                     }
                                 })  ;

                             }
                          }
                      })  ;

                  }
               }
           });
       }

       else {
           sendmss.setVisibility(View.INVISIBLE);
       }
    }

    private void acceptRequest() {
        contactref.child(senderId).child(recieverId).child("contact").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            contactref.child(recieverId).child(senderId).child("contact").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                chatref.child(senderId).child(recieverId).removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful())
                                                                    {
                                                                        chatref.child(senderId).child(recieverId).removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                        if(task.isSuccessful())
                                                                                        {
                                            chatref.child(recieverId).child(senderId).removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            curren_status="friends";
                                                            sendmss.setEnabled(true);
                                                            sendmss.setText("Remove this contact");
                                                            declinebtn.setVisibility(View.INVISIBLE);
                                                            declinebtn.setEnabled(false);
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
                    }
                });
    }

    private void cancelRequest() {
        chatref.child(senderId).child(recieverId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    chatref.child(recieverId).child(senderId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if ((task.isSuccessful()))
                            {
                                sendmss.setText("Send Message");
                                curren_status="new";
                                sendmss.setEnabled(true);
                                declinebtn.setVisibility(View.INVISIBLE);
                                declinebtn.setEnabled(false);
                            }
                        }
                    });

                }
            }
        });
    }

    private void sendRequest() {
        chatref.child(senderId).child(recieverId).child("request-type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            chatref.child(recieverId).child(senderId).child("request-type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                HashMap<String ,String>chatnotification=new HashMap<>();
                                                 chatnotification.put("from",senderId);
                                                 chatnotification.put("type","request");
                                                 notification.child(recieverId).push()
                                                         .setValue(chatnotification)
                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                 if(task.isSuccessful())
                                                                 {
                                                                     sendmss.setEnabled(true);
                                                                     curren_status="request-sent";
                                                                     sendmss.setText("Cancel request");
                                                                     sendmss.setEnabled(true);
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

    private void retrieveInfo() {
        reference.child(recieverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists())&& (dataSnapshot.hasChild("image")))
                {
                    String name=dataSnapshot.child("name").getValue().toString();
                    String userSt=dataSnapshot.child("status").getValue().toString();
                    String img=dataSnapshot.child("image").getValue().toString();
                    usrNm.setText(name);
                    UserStatus.setText(userSt);
                    Picasso.get().load(img).placeholder(R.drawable.profile_image).into(profileimg);
                }
                else {

                    String name=dataSnapshot.child("name").getValue().toString();
                    String userSt=dataSnapshot.child("status").getValue().toString();
                    usrNm.setText(name);
                    UserStatus.setText(userSt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
