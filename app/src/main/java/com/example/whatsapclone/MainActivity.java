package com.example.whatsapclone;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String FROM_SEETIINGS_KEY = "TAG";
    private Toolbar toobar;
  private ViewPager mypager;
 private   TabLayout mytablayout;
 private   AccesorfrgAddapter accesorfrgAddapter;
   private FirebaseAuth auth;
  private DatabaseReference rootRef;
   private String currentuserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         mypager=findViewById(R.id.viewpage);
         mytablayout=findViewById(R.id.tablayout);
        toobar=findViewById(R.id.main_toobar);
        setSupportActionBar(toobar);
        getSupportActionBar().setTitle("Whatsapp");
        accesorfrgAddapter=new AccesorfrgAddapter(getSupportFragmentManager());
        mypager.setAdapter(accesorfrgAddapter);
        mytablayout.setupWithViewPager(mypager);
         auth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser=auth.getCurrentUser();
        if(currentuser==null)
        {
            sendTologinpage();
        }
        else {
            verifyUserExistance();
            updatestatus("online");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentuser=auth.getCurrentUser();
        if(currentuser!=null)
        {

            updatestatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentuser=auth.getCurrentUser();
        if(currentuser!=null)
        {

            updatestatus("offline");
        }
    }

    private void   verifyUserExistance(){

        String currentid=auth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").exists()){
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendToSetting();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
       getMenuInflater().inflate(R.menu.option_menu,menu);
     return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.logout:
                updatestatus("offline");
               auth.signOut();
               Intent intent=new Intent(MainActivity.this,LoginActivity.class);
               startActivity(intent);
               break;
            case R.id.sett:
                sendToSetting();
                break;
            case R.id.find_frnds:
                sendToMainpage();
                break;
            case R.id.groupNm:
                GroupName();

        }
        return  true;
    }

    private void GroupName() {
        AlertDialog.Builder dialog=new  AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        dialog.setTitle("Create Group");
        final EditText groupNm=new EditText(MainActivity.this);
         groupNm.setHint("Raj tech..");
         dialog.setView(groupNm);
         dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 String nm=groupNm.getText().toString();
                 if(TextUtils.isEmpty(nm)){
                     Toast.makeText(MainActivity.this, "Please Write your group name", Toast.LENGTH_SHORT).show();
                 }
                 else {
                     createGroup(nm);
                 }
             }
         });
         dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 dialogInterface.cancel();
             }
         });
         dialog.show();
    }

    private void createGroup(final String Name) {
        rootRef.child("Groups").child(Name).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, Name+"Group Created Successfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    String ms=task.getException().toString();
                    Toast.makeText(MainActivity.this, "Error"+ms, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendTologinpage() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }
    private void  sendToSetting(){
        Intent intent=new Intent(MainActivity.this,
                Setting.class);
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        //finish();
    }
    private void sendToMainpage() {
        Intent intent=new Intent(MainActivity.this,FindsfrndsActivity.class);
        startActivity(intent);
    }
    private void updatestatus(String state)
    {
        String usertimestate,userdatestate;

       Calendar calender= Calendar.getInstance();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MMM:dd:yyyy");
         userdatestate=simpleDateFormat.format(calender.getTime());
         SimpleDateFormat time=new SimpleDateFormat("hh:mm a");
         usertimestate=time.format(calender.getTime());
        HashMap<String ,Object>userstates=new HashMap<>();
         userstates.put("time",usertimestate);
         userstates.put("date",userdatestate);
         userstates.put("state",state);
        FirebaseUser currentuser=auth.getCurrentUser();
         currentuserId=currentuser.getUid();
         rootRef.child("Users").child(currentuserId).child("userStatus")
                 .updateChildren(userstates);


    }

    @Override
    public void onBackPressed() {

        Intent a=new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
