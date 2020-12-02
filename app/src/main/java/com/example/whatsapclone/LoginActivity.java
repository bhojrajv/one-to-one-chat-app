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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseIndexArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.IOException;


public class LoginActivity extends AppCompatActivity  {
    private static final String TAG = "Token";
    private FirebaseUser user;
private Button loginbtn,phonelogin;
private TextInputEditText email,password;
private TextView forgt,createnewAc;
 private FirebaseAuth auth;
 private ProgressDialog progressDialog;
  private DatabaseReference uerRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        uerRef=FirebaseDatabase.getInstance().getReference().child("Users");
         Iniailized();
    }

    private void Iniailized() {
        loginbtn=findViewById(R.id.loginbtn);
        phonelogin=findViewById(R.id.phone);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        forgt=findViewById(R.id.frgotpass);
        createnewAc=findViewById(R.id.newAccount);
        progressDialog=new ProgressDialog(this);

         phonelogin.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent=new Intent(LoginActivity.this,PhoneActivity.class);
                 startActivity(intent);
             }
         });

        forgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,Register.class);
                startActivity(intent);
            }
        });

        createnewAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,Register.class);
                startActivity(intent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String logeamil=email.getText().toString();
                String logpass=password.getText().toString();
                if (TextUtils.isEmpty(logeamil))
                {
                    Toast.makeText(LoginActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(logpass))
                {
                    Toast.makeText(LoginActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                }
                else {
                     progressDialog.setTitle("Sign In");
                     progressDialog.setMessage("Please wait..");
                     progressDialog.setCanceledOnTouchOutside(true);
                     progressDialog.show();
                    auth.signInWithEmailAndPassword(logeamil,logpass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                          String currentid=auth.getCurrentUser().getUid();
                                       //String recieverId=getIntent().getExtras().get("id").toString();
                                        String deviceToken =    FirebaseInstanceId.getInstance().getToken();

                                                           uerRef.child(currentid).child("device-token")
                                                                   .setValue(deviceToken)
                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Void> task) {
                                                                           if(task.isSuccessful())
                                                                           {
                                                                               sendtoMain2();
                                                                               Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                                                               progressDialog.dismiss();
                                                                           }
                                                                       }
                                                                   });


                                    }
                                    else {
                                        String mess=task.getException().toString();
                                        Toast.makeText(LoginActivity.this, "Error"+mess, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                }

            }
        });
    }



    private void sendtoMain2() {

            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);



    }
}
