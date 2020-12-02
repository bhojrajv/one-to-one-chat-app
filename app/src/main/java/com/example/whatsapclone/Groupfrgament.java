package com.example.whatsapclone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class Groupfrgament extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
   private View layoutView;
   private ListView listView;
  private ArrayAdapter<String> arrayAdapter;
private ArrayList<String> arrayList;
 private DatabaseReference reference;




    public Groupfrgament() {
        // Required empty public constructor

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        reference= FirebaseDatabase.getInstance().getReference().child("Groups");
       layoutView= inflater.inflate(R.layout.fragment_groupfrgament, container, false);
        initialized();
        retrieveData();
        return layoutView;

    }

    private void retrieveData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set=new HashSet<String>();
                Iterator iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                arrayList.clear();
                arrayList.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialized() {
        listView=layoutView.findViewById(R.id.listview);
        arrayList=new ArrayList<String>();
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
              String nm=adapterView.getItemAtPosition(i).toString();
              Intent intent=new Intent(getContext(),GroupchatActivity.class);
              intent.putExtra("GroupName",nm);
              startActivity(intent);
          }
      });
    }


}
