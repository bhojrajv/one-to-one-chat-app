package com.example.whatsapclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupchatActivity extends AppCompatActivity {
private TextView textView;
private Toolbar toolbar;
private EditText chatsend;
 private ImageButton imageButton;
 private ScrollView scrollView;
private FirebaseAuth mauth;
private String CurrentId,Currentname,currentdate,currentTime,groupkey;
private DatabaseReference groupReference,userRef,groupRefkey;
    String nm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);
        scrollView=new ScrollView(this);
        nm=getIntent().getExtras().get("GroupName").toString();
        mauth=FirebaseAuth.getInstance();
        CurrentId=mauth.getCurrentUser().getUid();
        userRef=FirebaseDatabase.getInstance().getReference().child("Users");
        groupReference=FirebaseDatabase.getInstance().getReference().child("Groups").child(nm);
        initialized();
        groupInfo();

    }

    @Override
    protected void onStart() {
        super.onStart();
        retreiveGroupInfo();
    }

    private void retreiveGroupInfo() {
        groupReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                 getdata(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getdata(dataSnapshot);
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

    private void getdata(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            String name = (String) ((DataSnapshot) iterator.next()).getValue();
            String mess = (String) ((DataSnapshot) iterator.next()).getValue();
            String date = (String) ((DataSnapshot) iterator.next()).getValue();
            String time = (String) ((DataSnapshot) iterator.next()).getValue();
            textView.append(name+"\n"+mess+"\n"+time+"\n"+date+"\n \n");
            scrollView.fullScroll(ScrollView.FOCUS_UP);

        }
    }

    private void initialized() {
        textView=findViewById(R.id.chatmsg);
        chatsend=findViewById(R.id.chattext);
        toolbar=findViewById(R.id.groupChat);
        imageButton=findViewById(R.id.sendmsg2);
        //scrollView=findViewById(R.id.groupscro);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(nm);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 savegroupInfo();
                chatsend.setText("");
            }
        });
    }


    private void groupInfo() {

       userRef.child(CurrentId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                Currentname =dataSnapshot.child("name").getValue().toString();
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }
    private void savegroupInfo() {
        String message=chatsend.getText().toString();
        groupkey=groupReference.push().getKey();
        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please enter the text message....", Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat dateFormat=new SimpleDateFormat("MMM dd,yyyy");
            currentdate=dateFormat.format(calendar.getTime());
            SimpleDateFormat timeformat=new SimpleDateFormat("hh:mm");
            currentTime =timeformat.format(calendar.getTime());
            HashMap<String,Object>groupMsgkey=new HashMap<>();
            groupReference.updateChildren(groupMsgkey);
            groupRefkey=groupReference.child(groupkey);
            HashMap<String ,Object>groumsginfo=new HashMap<>();
            groumsginfo.put("name",Currentname);
            groumsginfo.put("msg",message);
            groumsginfo.put("date",currentdate);
            groumsginfo.put("time",currentTime);
            groupRefkey.updateChildren(groumsginfo);
        }
    }
}
