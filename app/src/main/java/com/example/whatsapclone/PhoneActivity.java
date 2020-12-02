package com.example.whatsapclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {
 private EditText userphone,verifycode;
 private  Button sendcodebtn,verifycodebtn;
 private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
 private  String mVerificationId;
 private  PhoneAuthProvider.ForceResendingToken mResendToken;
 private FirebaseAuth mauth;
 private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        userphone=findViewById(R.id.verifyphone);
        verifycode=findViewById(R.id.verifycode);
        sendcodebtn=findViewById(R.id.sendcode);
        verifycodebtn=findViewById(R.id.verifycodebtn);
         mauth=FirebaseAuth.getInstance();
         dialog=new ProgressDialog(this);

        sendcodebtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.setTitle("Sending the code");
                dialog.setMessage("Please wait we are sending  the code...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
              String  phoneNumber=userphone.getText().toString();

                if(TextUtils.isEmpty( phoneNumber)){
                    Toast.makeText(PhoneActivity.this, "Please enter your valid number ..", Toast.LENGTH_SHORT).show();
                }
                else {
                    userphone.setVisibility(View.INVISIBLE);
                    verifycode.setVisibility(View.VISIBLE);
                    sendcodebtn.setVisibility(View.INVISIBLE);
                    verifycodebtn.setVisibility(View.VISIBLE);
                          // OnVerificationStateChangedCallbacks
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,       // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneActivity.this,               // Activity (for callback binding)
                            callbacks);

                }
            }
        });
         callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
             @Override
             public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                 signInWithPhoneAuthCredential(phoneAuthCredential);
             }

             @Override
             public void onVerificationFailed(FirebaseException e) {
                 Toast.makeText(PhoneActivity.this, "Please enter valid phone number with country code....", Toast.LENGTH_SHORT).show();
             }
             @Override
             public void onCodeSent( String verificationId,
                                     PhoneAuthProvider.ForceResendingToken token) {
                 dialog.dismiss();
                 mVerificationId = verificationId;
                 mResendToken = token;
                 Toast.makeText(PhoneActivity.this, "Congratulation Verification code has been sent successfully", Toast.LENGTH_SHORT).show();
                 // ...
             }
         };
        verifycodebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userphone.setVisibility(View.VISIBLE);
                verifycode.setVisibility(View.INVISIBLE);
                sendcodebtn.setVisibility(View.VISIBLE);
                verifycodebtn.setVisibility(View.INVISIBLE);


                String code=verifycode.getText().toString();
                if(TextUtils.isEmpty(code))
                {
                    Toast.makeText(PhoneActivity.this, "Please enter valid verification code", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.setTitle("Verify the code");
                    dialog.setMessage("Please wait we are verifing the code...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code );
                    signInWithPhoneAuthCredential(credential);

                }

            }
        });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(PhoneActivity.this, "LoggedIn Successfully", Toast.LENGTH_SHORT).show();
                            sendToMain();
                        } else {
                               String mss=task.getException().toString();
                            Toast.makeText(PhoneActivity.this, "Error:"+mss, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendToMain() {
        Intent intent=new Intent(PhoneActivity.this,Setting.class);
        startActivity(intent);
        finish();
    }

}
