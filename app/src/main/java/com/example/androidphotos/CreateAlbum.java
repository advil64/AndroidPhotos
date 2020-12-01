package com.example.androidphotos;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class CreateAlbum extends AppCompatActivity {

    private Button createButton;
    private EditText createText;

    public static final String ALBUM_NAME = "albumName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void cancel(View view){
        setResult(RESULT_CANCELED);
        finish();
    }

    public void create(View view){
        //obtaining the list of albums
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<Album> list = (ArrayList<Album>) args.getSerializable("ARRAYLIST");

        createText = findViewById(R.id.createText);
        String albumName = createText.getText().toString().trim();
        //if album name is empty
        if(albumName == null || albumName.trim().length() == 0){
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "Album name is required before creating");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        //check if album name already exists
        for(Album x: list){
            if(x.getAlbumName().equalsIgnoreCase(albumName)){
                //show pop-up error
                Bundle bundle = new Bundle();
                bundle.putString(PopupDialog.MESSAGE_KEY, "Album name already exists");
                DialogFragment newFragment = new PopupDialog();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(),"badfields");
                return;
            }
        }
        //if album name doesn't exist send album name in bundle to caller
        Bundle bundle = new Bundle();
        bundle.putString(ALBUM_NAME,albumName);
        Intent intent2 = new Intent();
        intent2.putExtras(bundle);
        setResult(RESULT_OK,intent2);
        finish(); // pops activity from the call stack, returns to parent

    }
}