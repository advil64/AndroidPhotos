package com.example.androidphotos;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class OpenAlbum extends AppCompatActivity {

    private ListView listView;
    private Button removePhotoButton;
    private Button displayPhotoButton;
    private Button addPhotoButton;

    ArrayList<Photo> photos= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.PhotoList);
        listView.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, photos));
        listView.setClickable(true);
    }
}