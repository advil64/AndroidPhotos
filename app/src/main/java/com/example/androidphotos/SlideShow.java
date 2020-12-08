package com.example.androidphotos;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class SlideShow extends AppCompatActivity {

    private Button next;
    private Button prev;
    private ImageView imageView;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //retrieving current photo
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");

        ArrayList<Photo> list = (ArrayList<Photo>)args.getSerializable("PHOTO LIST");
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(list.get(0).getPhotoPath());

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next(list);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev(list);
            }
        });
    }

    private void next(ArrayList<Photo> list){
        index++;
        if(index == list.size()){
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "This is the last image");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            index--;
            return;
        }
        imageView.setImageURI(list.get(index).getPhotoPath());
    }

    private void prev(ArrayList<Photo> list){
        index--;
        if(index == -1){
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "This is the first image");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            index = 0;
            return;
        }
        imageView.setImageURI(list.get(index).getPhotoPath());
    }
}