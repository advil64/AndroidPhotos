package com.example.androidphotos;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class Search extends AppCompatActivity {

    private ListView listview;
    private EditText tag1;
    private EditText tag2;
    private Spinner tagType1;
    private Spinner tagType2;
    private Spinner conjunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //obtaining the list of albums
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<Album> list = (ArrayList<Album>) args.getSerializable("ARRAYLIST");

        listview = findViewById(R.id.searchPhotoList);
        tag1 = findViewById(R.id.tag1);
        tag2 = findViewById(R.id.tag2);
        tagType1 = findViewById(R.id.tagType1);
        tagType2 = findViewById(R.id.tagType2);
        conjunction = findViewById(R.id.conjunctionType);

        //loop through all the albums' photos and put in arraylist (no duplicates)
        ArrayList<Photo> photos = new ArrayList<>();
        boolean duplicate = false;
        for(Album x: list){
            for(Photo y: x.getPhotos()){
                //checking for duplicate
                for(Photo z: photos){
                    if(y.getPhotoPath().equals(z.getPhotoPath())){
                        duplicate = true;
                        break;
                    }
                }
                //if not a duplicate add to arrayList
                if(duplicate == false){
                    photos.add(y);
                }
                duplicate = false;
            }
        }

        //obtaining conjunction type
        String conjunctionType = conjunction.getSelectedItem().toString();
        //AND CONJUNCTION
        if(conjunctionType.equals("AND")){
            String firstTag = tag1.getText().toString().trim();
            String secondTag = tag2.getText().toString().trim();
            if(firstTag.equals("") || firstTag == null || secondTag.equals("") || secondTag == null){
                //show pop-up error (both tags are required)
                Bundle bundle = new Bundle();
                bundle.putString(PopupDialog.MESSAGE_KEY, "Tag fields are empty, fill in both fields");
                DialogFragment newFragment = new PopupDialog();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(),"badfields");
                return;
            }
            String firstTagType = tagType1.getSelectedItem().toString();
            String secondTagType = tagType2.getSelectedItem().toString();
        }
        //OR CONJUNCTION
        else if (conjunctionType.equals("OR")){

        }
        else{
            //display error to choose conjunction type
            //show pop-up error (conjunction is required)
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "Please choose a conjunction type");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
    }
}