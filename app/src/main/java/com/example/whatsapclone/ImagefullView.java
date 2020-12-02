package com.example.whatsapclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImagefullView extends AppCompatActivity {
private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagefull_view);
        imageView=findViewById(R.id.imageview);
        String img=getIntent().getStringExtra("url");
        Picasso.get().load(img).into(imageView);
    }
}
