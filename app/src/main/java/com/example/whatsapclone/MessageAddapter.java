package com.example.whatsapclone;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAddapter extends RecyclerView.Adapter<MessageAddapter.MessageViewholder> {
   private Context context;
    private List<Message> messages;
   private FirebaseAuth auth;
   private DatabaseReference userRef;
  public MessageAddapter(PrivateChat privateChat,List<Message> messages){
      context=privateChat;
      this.messages=messages;
  }


    @NonNull
    @Override
    public MessageViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View view=layoutInflater.inflate(R.layout.message_layout,parent,false);
        return new MessageViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewholder holder, final int position) {
        auth=FirebaseAuth.getInstance();
        final String senderId=auth.getCurrentUser().getUid();
           Message usermsg= messages.get(position);
           final String from=usermsg.getFrom();
            final String messagetype=usermsg.getType();
            userRef= FirebaseDatabase.getInstance().getReference().child("Users");
            userRef.child(from).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if(dataSnapshot.hasChild("image"))
                    {
                        String userproimg=dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(userproimg).placeholder(R.drawable.profile_image).into(holder.circleImageView);
                    }
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
        holder.receiverTxtmsg.setVisibility(View.GONE);
        holder.circleImageView.setVisibility(View.GONE);
        holder.senderTextmsg.setVisibility(View.GONE);
        holder.imagsender.setVisibility(View.GONE);
        holder.imagereceiver.setVisibility(View.GONE);
            if(messagetype.equals("text"))
            {

                if(from.equals(senderId))
                {
                    holder.senderTextmsg.setVisibility(View.VISIBLE);
                    holder.senderTextmsg.setBackgroundResource(R.drawable.sender_layout);
                    holder.senderTextmsg.setText(usermsg.getMessage()+"\n"+messages.get(position).getTime()
                       +"\n"+messages.get(position).getDate());
                    holder.senderTextmsg.setTextColor(Color.BLACK);
                }

                else {
                    holder.receiverTxtmsg.setVisibility(View.VISIBLE);
                    holder.circleImageView.setVisibility(View.VISIBLE);
                   // holder.circleImageView.setImageResource(R.drawable.profile_image);

                    holder.receiverTxtmsg.setTextColor(Color.BLACK);
                    holder.receiverTxtmsg.setBackgroundResource(R.drawable.receiver_layout);
                    holder.receiverTxtmsg.setText(usermsg.getMessage()+"\n"+messages.get(position).getTime()
                    +"\n"+messages.get(position).getDate());
                }

            }
            else if(messagetype.equals("image"))
            {
                if (from.equals(senderId))
                {
                    holder.imagsender.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.get(position).getMessage()).into(holder.imagsender);
                }
                else {
                    holder.circleImageView.setImageResource(View.VISIBLE);
                    holder.imagereceiver.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.get(position).getMessage()).into(holder.imagereceiver);
                }
            }
            else {
                if (messagetype.equals("pdf") || messagetype.equals("doc")|| messagetype.equals("docx"))
                {
                    if(from.equals(senderId))
                    {
                        holder.imagsender.setVisibility(View.VISIBLE);
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/whatsapclone-acc59.appspot.com/o/file.png?alt=media&token=b3c1c2e0-e7c7-4bff-9d5e-209bf665aa5a").into(holder.imagsender);


                    }
                    else {
                        holder.circleImageView.setVisibility(View.VISIBLE);
                        holder.imagereceiver.setVisibility(View.VISIBLE);
                         Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/whatsapclone-acc59.appspot.com/o/file.png?alt=media&token=b3c1c2e0-e7c7-4bff-9d5e-209bf665aa5a")
                                 .into(holder.imagereceiver);

                    }
                }


            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[]charSequences
                            =new CharSequence[]{
                            "Delete from me",
                            "Download and View",
                            "Cancel",
                            "Delete from everyone"
                    };
                    if(from.equals(senderId))
                    {

                        if (messagetype.equals("pdf")|| messagetype.equals("doc"))
                        {
                            final AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Deleting file")
                                    .setItems(charSequences, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which==0)
                                            {
                                                messageDeletedFromsdener(position,holder);
                                                Intent intent=new Intent(holder.itemView.getContext(),MainActivity.class);
                                                holder.itemView.getContext().startActivity(intent);
                                            }
                                            else if(which==1)
                                            {
                                               Intent intent=new Intent(context,ImagefullView.class);
                                               intent.putExtra("url",messages.get(position).getMessage());
                                               holder.itemView.getContext().startActivity(intent);
                                            }
                                            else if(which==2)
                                            {
                                            }
                                            else if(which==3)
                                            {
                                                messageDeletedfroEveryone(position,holder);
                                            }
                                        }
                                    });
                                 builder.show();
                        }
                        else if (messagetype.equals("image"))
                        {
                            final AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Deleting file")
                                    .setItems(charSequences, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which==0)
                                            {
                                             messageDeletedFromsdener(position,holder);
                                            }
                                            else if(which==1)
                                            {
                                                Intent intent=new Intent(context,ImagefullView.class);
                                                intent.putExtra("url",messages.get(position).getMessage());
                                                holder.itemView.getContext().startActivity(intent);
                                            }
                                            else if(which==2)
                                            {

                                            }
                                            else if(which==3)
                                            {
                                              messageDeletedfroEveryone(position,holder);
                                            }
                                        }
                                    });
                            builder.show();
                        }
                        else if(messagetype.equals("text"))
                        {
                            final AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Deleting file")
                                    .setItems(charSequences, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which==0)
                                            {
                                               messageDeletedFromsdener(position,holder);
                                            }

                                            else if(which==2)
                                            {
                                            }
                                            else if(which==3)
                                            {
                                               messageDeletedfroEveryone(position,holder);
                                            }
                                        }
                                    });
                            builder.show();
                        }
                    }
                    else {
                        final CharSequence[]charSequences2
                                =new CharSequence[]{
                                "Delete from me",
                                "Download and View",
                                "Cancel",

                        };
                        if (messagetype.equals("pdf")|| messagetype.equals("doc"))
                        {
                            final AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Deleting file")
                                    .setItems(charSequences2, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which==0)
                                            {
                                                messageDeletedFromReceiver(position,holder);
                                            }
                                            else if(which==1)
                                            {
                                                Intent i=new Intent(context,ImagefullView.class);
                                                String url= messages.get(position).getMessage();
                                                i.putExtra("url",url);
                                                holder.itemView.getContext().startActivity(i);
                                            }
                                            else if(which==2)
                                            {
                                            }
                                            else if(which==3)
                                            {
                                               // messageDeletedfroEveryone(position,holder);
                                            }
                                        }
                                    });
                            builder.show();
                        }
                        else if (messagetype.equals("image"))
                        {
                            final AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Deleting file")
                                    .setItems(charSequences, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which==0)
                                            {
                                                messageDeletedFromReceiver(position,holder);
                                            }
                                            else if(which==1)
                                            {
                                                Intent intent=new Intent(context,ImagefullView.class);
                                                intent.putExtra("url",messages.get(position).getMessage());
                                                holder.itemView.getContext().startActivity(intent);
                                            }
                                            else if(which==2)
                                            {
                                            }
                                            else if(which==3)
                                            {
                                               // messageDeletedfroEveryone(position,holder);
                                            }
                                        }
                                    });
                            builder.show();
                        }
                        else if(messagetype.equals("text"))
                        {
                            final AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Deleting file")
                                    .setItems(charSequences, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which==0)
                                            {
                                                messageDeletedFromReceiver(position,holder);
                                            }

                                            else if(which==2)
                                            {
                                            }
                                            else if(which==3)
                                            {
                                            // messageDeletedfroEveryone(position,holder);
                                            }
                                        }
                                    });
                            builder.show();
                        }
                    }
                }
            });

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageViewholder extends RecyclerView.ViewHolder {
      CircleImageView circleImageView;
      ImageView imagsender,imagereceiver;
      TextView senderTextmsg,receiverTxtmsg;
        public MessageViewholder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.privatechatProfile);
             senderTextmsg=itemView.findViewById(R.id.sendermsg);
             receiverTxtmsg=itemView.findViewById(R.id.recevermsg);
             imagereceiver=itemView.findViewById(R.id.image_receiver);
             imagsender=itemView.findViewById(R.id.image_sender);
        }
    }
    private void messageDeletedFromsdener(final int postion ,final MessageViewholder messageViewholder)
    {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

      String getkeyid=databaseReference.getKey();
        databaseReference.child("Message")
                .child(messages.get(postion).getFrom())
                .child(messages.get(postion).getTo())
                .child(messages.get(postion).getMessageId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void messageDeletedFromReceiver(int postion ,MessageViewholder messageViewholder)
    {
         String currentid;
         FirebaseAuth auth = null;
          auth=FirebaseAuth.getInstance();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser=auth.getCurrentUser();
        currentid=firebaseUser.getUid();
        databaseReference.child("Message")
                .child(messages.get(postion).getTo())
                .child(messages.get(postion).getFrom())
                .child(messages.get(postion).getMessageId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void messageDeletedfroEveryone(final int postion , MessageViewholder messageViewholder)
    {
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Message")
                .child(messages.get(postion).getTo())
                .child(messages.get(postion).getFrom())
                .child(messages.get(postion).getMessageId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {

                            databaseReference.child("Message")
                                    .child(messages.get(postion).getFrom())
                                    .child(messages.get(postion).getTo())
                                    .child(messages.get(postion).getMessageId())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}
