package com.example.whatsapclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Placeholder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivateChat extends AppCompatActivity {
   private EditText sendmess;
  private ImageView chatbtn;
  private DatabaseReference rootRef;
 private TextView name,lastseen;
 private CircleImageView proimage;
 private String usrname,receieverid;
 private String img;
 private String senderid;
 private FirebaseAuth auth;
  private Toolbar toolbar;
  private MessageAddapter messageAddapter;
  private RecyclerView mesageRec;
  private List<Message>listmsg;
  private LinearLayoutManager linearLayoutManager;
  private String userdate,userTime;
  private ImageView sendfilebtn;
  private static final int SELECT_CODE=2;
  private String chacker="",Myurl;
  private Uri uripath;
  private ProgressDialog progressDialog;
  private StorageTask uploadtask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);
        sendmess=findViewById(R.id.privateChat);
        chatbtn=findViewById(R.id.privatesendmsg);
       mesageRec=findViewById(R.id.privateChateRec);
       sendfilebtn=findViewById(R.id.privatefilesbtn);
        auth=FirebaseAuth.getInstance();
        senderid=auth.getCurrentUser().getUid();
        rootRef=FirebaseDatabase.getInstance().getReference();
           listmsg=new ArrayList<>();
           linearLayoutManager=new LinearLayoutManager(this);
        usrname=getIntent().getExtras().get("current_userName").toString();
       receieverid=getIntent().getStringExtra("current_user");
        img=  getIntent().getExtras().get("current_usrimg").toString();
       // Toast.makeText(this, ""+receieverid, Toast.LENGTH_SHORT).show();

        toolbar=findViewById(R.id.custom_tool);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("MMM/dd/yyyy");
        userdate= dateFormat.format(calendar.getTime());
        SimpleDateFormat time=new SimpleDateFormat("hh:mm");
        userTime=time.format(calendar.getTime());

        LayoutInflater layoutInflater= (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.custom_layout,null,false);
         name=view.findViewById(R.id.toolUserName);
         lastseen=view.findViewById(R.id.toollastseen);
         proimage=view.findViewById(R.id.toolProfileimg);
         name.setText(usrname);
        Picasso.get().load(img).placeholder(R.drawable.profile_image).into(proimage);
        actionBar.setCustomView(view);
chatbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Inialize();
    }
});
messageAddapter=new MessageAddapter(PrivateChat.this,listmsg);
  mesageRec.setLayoutManager(linearLayoutManager);
  mesageRec.setAdapter(messageAddapter);
 diaplaylastseen();

  sendfilebtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

           CharSequence[] charSequence=new CharSequence[]
                   {
                      "image" ,
                      "pdf"   ,
                      "doc"
                   };
          AlertDialog.Builder builder=new AlertDialog.Builder(PrivateChat.this);
          builder.setTitle("Sending file");
          builder.setItems(charSequence, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  if(which==0)
                  {  chacker="image";
                      Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                      intent.setType("image/*");
                      startActivityForResult(Intent.createChooser(intent,"Select Image"),SELECT_CODE);
                  }
                  else if(which==1)
                  {
                    chacker="pdf";
                    Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(Intent.createChooser(intent,"Select pdf file"),SELECT_CODE);
                  }
                  else if(which==2)
                  {
                      chacker="doc";
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/msword");
                startActivityForResult(Intent.createChooser(intent,"Select ms word file"), SELECT_CODE);
                  }
              }
          });builder.show();
      }
  });
    }

    @Override
    protected void onStart() {
        super.onStart();

        rootRef.child("Message").child(senderid).child(receieverid).
                addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            Message message=dataSnapshot.getValue(Message.class);
                            listmsg.add(message);
                            messageAddapter.notifyDataSetChanged();
                            mesageRec.smoothScrollToPosition(mesageRec.getAdapter().getItemCount());
                            mesageRec.smoothScrollToPosition(mesageRec.computeVerticalScrollExtent());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void Inialize() {

                String text=sendmess.getText().toString().trim();
                if(TextUtils.isEmpty(text))
                {
                    Toast.makeText(PrivateChat.this, "please type something here", Toast.LENGTH_SHORT).show();
                }
                else {

                    String senderRef="Message/"+senderid+"/"+receieverid;
                    String receiverRef="Message/"+receieverid+"/"+senderid;

                    DatabaseReference messageRef=rootRef.child("Message")
                            .child(senderid).child(receieverid).push();
                    String messagepushid=messageRef.getKey();

                    Map messageBody=new HashMap();
                    messageBody.put("message",text);
                    //messageBody.put("username",name);
                    messageBody.put("from",senderid);
                    messageBody.put("type","text");
                    messageBody.put("to",receieverid);
                    messageBody.put("messageid",messagepushid);
                    messageBody.put("time",userTime);
                    messageBody.put("date",userdate);

                    Map messagebodyDetails=new HashMap();
                         messagebodyDetails.put(senderRef + "/" + messagepushid,messageBody);
                    messagebodyDetails.put(receiverRef + "/" + messagepushid,messageBody);
                    rootRef.updateChildren(messagebodyDetails)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(PrivateChat.this, "sent Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(PrivateChat.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                    sendmess.setText("");
                                }
                            });
                     }

    }
    private  void diaplaylastseen()
    {
        rootRef.child("Users").child(receieverid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("userStatus").hasChild("state"))
                        {
                            String state=dataSnapshot.child("userStatus").child("state").getValue().toString();
                            String date=dataSnapshot.child("userStatus").child("date").getValue().toString();
                            String time=dataSnapshot.child("userStatus").child("time").getValue().toString();
                            if(state.equals("online"))
                            {
                                lastseen.setText("online");
                            }
                            else {
                                lastseen.setText("Last seen:"+" "+date+" "+time);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECT_CODE && resultCode==RESULT_OK
         && data!=null &&data.getData()!=null)
        {
            progressDialog=new ProgressDialog(PrivateChat.this);
            progressDialog.setTitle("File sending");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please wait until sending the file");
            progressDialog.show();

                uripath=data.getData();
                if(!chacker.equals("image"))
                {
               StorageReference storageReference=FirebaseStorage.getInstance().getReference().child("Document files");
                    final String senderRef="Message/"+senderid+"/"+receieverid;
                    final String receiverRef="Message/"+receieverid+"/"+senderid;

                    DatabaseReference messageRef=rootRef.child("Message")
                            .child(senderid).child(receieverid).push();
                    final String messagepushid=messageRef.getKey();
                    //final StorageReference filepath=storageReference.child(messagepushid+"."+"jpg");
                 StorageReference filepath=storageReference.child(messagepushid +"."+chacker);
                 filepath.putFile(uripath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                         if (task.isSuccessful()){

                             HashMap messageBody=new HashMap();
                              messageBody.put("message",task.getResult().getStorage().getDownloadUrl().toString());
                              messageBody.put("name",uripath.getLastPathSegment());
                              messageBody.put("type",chacker);
                              messageBody.put("from",senderid);
                              messageBody.put("to",receieverid);
                              messageBody.put("time",userTime);
                              messageBody.put("date",userdate);
                              messageBody.put("messageid",messagepushid);

                              HashMap messagedetails =new HashMap();
                              messagedetails.put(senderRef+"/"+messagepushid,messageBody);
                              messagedetails.put(receiverRef+"/"+messagepushid,messageBody);
                              rootRef.updateChildren(messagedetails);
                              progressDialog.dismiss();
                         }
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         progressDialog.dismiss();
                         Toast.makeText(PrivateChat.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                           double p=(100.0* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                           progressDialog.setMessage((int)p+"%"+"Uploading.....");
                     }
                 });
                }
                else if(chacker.equals("image"))
                {
                   // progressDialog.show();
                    StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Files");
                    final String senderRef="Message/"+senderid+"/"+receieverid;
                    final String receiverRef="Message/"+receieverid+"/"+senderid;

                    DatabaseReference messageRef=rootRef.child("Message")
                            .child(senderid).child(receieverid).push();
                    final String messagepushid=messageRef.getKey();
                    final StorageReference filepath=storageReference.child(messagepushid +"."+"jpg");
                   uploadtask= filepath.putFile(uripath);
                   uploadtask.continueWithTask(new Continuation() {
                       @Override
                       public Object then(@NonNull Task task) throws Exception {
                           if(!task.isSuccessful())
                           {
                               throw task.getException();
                           }
                           return filepath.getDownloadUrl();
                       }
                   }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                       @Override
                       public void onComplete(@NonNull Task<Uri> task) {
                           if(task.isSuccessful())
                           {
                              Uri uri=task.getResult();
                           Myurl=uri.toString();
                               Map messageBody=new HashMap();
                               messageBody.put("message",Myurl);
                              messageBody.put("name",uripath.getLastPathSegment());
                               messageBody.put("from",senderid);
                               messageBody.put("type",chacker);
                               messageBody.put("to",receieverid);
                               messageBody.put("messageid",messagepushid);
                               messageBody.put("time",userTime);
                               messageBody.put("date",userdate);

                               Map messagebodyDetails=new HashMap();
                               messagebodyDetails.put(senderRef + "/" + messagepushid,messageBody);
                               messagebodyDetails.put(receiverRef + "/" + messagepushid,messageBody);
                               rootRef.updateChildren(messagebodyDetails)
                                       .addOnCompleteListener(new OnCompleteListener() {
                                           @Override
                                           public void onComplete(@NonNull Task task) {
                                               if(task.isSuccessful())
                                               {
                                                   progressDialog.dismiss();
                                                   Toast.makeText(PrivateChat.this, "sent Successfully", Toast.LENGTH_SHORT).show();
                                               }
                                               else {
                                                   progressDialog.dismiss();
                                                   Toast.makeText(PrivateChat.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                               }
                                               sendmess.setText("");
                                           }
                                       });
                           }
                       }
                   });

                }
                else {
                    Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
                }

        }
    }
}
