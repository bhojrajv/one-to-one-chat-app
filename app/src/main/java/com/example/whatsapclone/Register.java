package com.example.whatsapclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;

public class Register extends AppCompatActivity {
 private Button Regbtn;
 private EditText email,pass;
 private TextView haveAc;
 private FirebaseAuth mauth;
 private DatabaseReference firebaseDatabase;
 private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mauth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance().getReference();
        initialized();
    }

    private void initialized() {
        Regbtn=findViewById(R.id.Regbtn);
        email=findViewById(R.id.Regemail);
        pass=findViewById(R.id.Regpassword);
        haveAc=findViewById(R.id.newAccount);
          progressDialog=new ProgressDialog(this);

        Regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String regemail=email.getText().toString();
                String password=pass.getText().toString();
                if(TextUtils.isEmpty(regemail)){
                    Toast.makeText(Register.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                }
                else {
                      progressDialog.setTitle("Create new Account");
                      progressDialog.setMessage("Please wait While creating an account for you....");
                      progressDialog.setCanceledOnTouchOutside(true);
                      progressDialog.show();
                    mauth.createUserWithEmailAndPassword(regemail,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                         String currentId=mauth.getCurrentUser().getUid();
                                       // String recieverId=getIntent().getExtras().get("id").toString();
                                        String devicetoken = FirebaseInstanceId.getInstance().getToken();

                                                        firebaseDatabase.child("Users").child(currentId).setValue("");
                                                        firebaseDatabase.child("Users")
                                                                .child(currentId)
                                                                .child("device-token")
                                                                .setValue(devicetoken);
                                                        sendtoMain();
                                                        Toast.makeText(Register.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();


                                    }
                                    else {
                                        String message=task.getException().toString();
                                        Toast.makeText(Register.this, "Error"+message, Toast.LENGTH_SHORT).show();
                                        Log.d("Result",message);
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });
    }
    private  void sendtoMain(){
        Intent intent=new Intent(Register.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
