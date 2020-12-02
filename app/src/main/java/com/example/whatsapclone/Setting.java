package com.example.whatsapclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setting extends AppCompatActivity {
    Toolbar toolbar;
  private Button udatebtn;
  private TextInputEditText username,editStatus;
  private FirebaseAuth auth;
  private DatabaseReference reference;
  private CircleImageView imageView;
  private int gallerypic=1;
  private  String currentid;
  private StorageReference UserprofileRef;
  private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
       initialize();
        ritrieveInfo();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,gallerypic);
            }
        });
        udatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name2=username.getText().toString();
                final String status=editStatus.getText().toString();
                final String currentid=auth.getCurrentUser().getUid();
                if(TextUtils.isEmpty(name2))
                {
                    Toast.makeText(Setting.this, "Please enter your name....", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(status))
                {
                    Toast.makeText(Setting.this, "Please write your status....", Toast.LENGTH_SHORT).show();
                }
                else {
                    HashMap<String ,Object>mapv=new HashMap<>();
                    mapv.put("name",name2);
                    mapv.put("uid",currentid);
                    mapv.put("status",status);
                    reference.child("Users").child(currentid).updateChildren(mapv).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(Setting.this, "profile udated successfully", Toast.LENGTH_LONG).show();
                                sendTomain();
                            }
                            else {
                                String msg=task.getException().toString();
                                Toast.makeText(Setting.this, "Error"+msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            }
        });
    }

    private void initialize() {
        udatebtn=findViewById(R.id.updbtn);
        username=findViewById(R.id.userName);
        editStatus=findViewById(R.id.editStatus);
        imageView=findViewById(R.id.image2);
        username.setVisibility(View.INVISIBLE);
        toolbar=findViewById(R.id.settintoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings Activity");
        auth=FirebaseAuth.getInstance();
        currentid =auth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference();
        UserprofileRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
        dialog=new ProgressDialog(this);
    }

    private void ritrieveInfo() {
        String id=auth.getCurrentUser().getUid();
        reference.child("Users").child(currentid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")) && (dataSnapshot.hasChild("name"))){
                   String retrievusname=dataSnapshot.child("name").getValue().toString();
                   String retrievstatus=dataSnapshot.child("status").getValue().toString();
                   String img=dataSnapshot.child("image").getValue().toString();
                    Log.i("Res",img+retrievstatus);
                   username.setText(retrievusname);
                   editStatus.setText(retrievstatus);
                    Picasso.get().load(img).into(imageView);
                }
                else  if(dataSnapshot.exists() && dataSnapshot.hasChild("name")){
                    String retrievusname=dataSnapshot.child("name").getValue().toString();
                    String retrievstatus=dataSnapshot.child("status").getValue().toString();
                    username.setText(retrievusname);
                    editStatus.setText(retrievstatus);
//                    String Image=dataSnapshot.child("image").getValue().toString();
//                    Picasso.get().load(Image).into(imageView);
                }
                else {
                    username.setVisibility(View .VISIBLE);
                    Toast.makeText(Setting.this, "set and Upadate your profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void sendTomain(){
        Intent intent=new Intent(Setting.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==gallerypic && resultCode==RESULT_OK && data!=null){
            Uri image=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                dialog.setTitle("Set your image profile");
                dialog.setMessage("Please wait we are updation your profile image....");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                Uri resultUri = result.getUri();
                StorageReference filepath=UserprofileRef.child(currentid+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                         if(task.isSuccessful()){
                             dialog.dismiss();

                             Toast.makeText(Setting.this, "Image upload successfully", Toast.LENGTH_SHORT).show();

                             final String retreiveimg=task.getResult().getStorage().getPath();
                             reference.child("Users").child(currentid).child("image").setValue(retreiveimg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful())
                                   {
                                       dialog.dismiss();
                                       Toast.makeText(Setting.this, "Image store in database Successfully", Toast.LENGTH_SHORT).show();
                                   }
                                   else {
                                       dialog.dismiss();
                                       String msg=task.getException().toString();
                                       Toast.makeText(Setting.this, "Error"+msg, Toast.LENGTH_SHORT).show();
                                   }
                                 }
                             });
                         }
                         else {
                             dialog.dismiss();
                             String msg=task.getException().toString();
                             Toast.makeText(Setting.this, "Error:"+msg, Toast.LENGTH_SHORT).show();
                         }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
