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
import android.widget.EditText;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;

public class AddPhoto extends AppCompatActivity {

    private Photo currPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //retrieving current photo
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        currPhoto = (Photo)args.getSerializable("PHOTO");

        //setting image view
        ImageView myImageView = findViewById(R.id.mainImage);
        myImageView.setImageURI(currPhoto.getPhotoPath());

        //setting up buttons
        Button createPhoto;
        Button cancelAction;

        //setting on action listener for create photo button
        createPhoto = findViewById(R.id.photoCaption);
        createPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create(v);
            }
        });

        //setting on action listener for cancel photo button
        cancelAction = findViewById(R.id.photoCancel);
        cancelAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(v);
            }
        });
    }

    public void cancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void create(View view){
        EditText createText = findViewById(R.id.createText);
        String photoName = createText.getText().toString().trim();
        //if album name is empty
        if(photoName == null || photoName.trim().length() == 0){
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "Photo caption is required before creating");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        //if album name doesn't exist send album name in bundle to caller
        currPhoto.setCaption(photoName);
        Bundle args = new Bundle();
        args.putSerializable("CAPTION",(Serializable)photoName);
        args.putSerializable("PHOTOPATH", (Serializable)currPhoto.getPhotoPath().toString());
        Intent intent = new Intent();
        intent.putExtras(args);
        setResult(RESULT_OK, intent);
        finish(); // pops activity from the call stack, returns to parent
    }
}