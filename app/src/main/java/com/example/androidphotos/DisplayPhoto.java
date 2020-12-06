package com.example.androidphotos;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class DisplayPhoto extends AppCompatActivity {

    private Photo currPhoto;
    private ArrayList<Album> albums;
    private Button addTagButton;
    private Button removeTagButton;
    private Spinner tagType;
    private EditText tagText;
    private ListView tagsList;

    int selectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_photo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //retrieving current photo
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
//        currPhoto = (Photo)args.getSerializable("PHOTO");
//
//        //setting image view
//        ImageView myImageView = findViewById(R.id.image);
//        myImageView.setImageURI(currPhoto.getPhotoPath());

        albums = (ArrayList<Album>)args.getSerializable("ALL ALBUMS"); 
        addTagButton = findViewById(R.id.addTag);
        removeTagButton = findViewById(R.id.removeTag);

        tagsList = findViewById(R.id.tagsList);
        tagsList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, currPhoto.getTags()));
        tagsList.setClickable(true);
        //setting the index of the item clicked on
        tagsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedIndex = position;
            }
        });

        //setting on action listener for add tag button
        addTagButton = findViewById(R.id.addTag);
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTag(currPhoto);
            }
        });

        //setting on action listener for delete tag button
        removeTagButton = findViewById(R.id.removeTag);
        removeTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTag(currPhoto);
            }
        });
    }
    
    private void addTag(Photo currPhoto){
        tagType = findViewById(R.id.tagType);
        tagText = findViewById(R.id.tagText);
        String type = tagType.getSelectedItem().toString();
        String tag = tagText.getText().toString().trim();
        String totalTag = type + ": " + tag;
        //check to see if both type and tag text are filled
        if(type.equals("None") || type.equals("") || type == null || tag.equals("") || tag == null){
            //show pop-up error (both fields are required)
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "Tag type and tag fields are both required");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        for(String s: currPhoto.getTags()){
            //check to see if tag already exists
            if(s.equalsIgnoreCase(totalTag)){
                //show pop-up error (tag already exists)
                Bundle bundle = new Bundle();
                bundle.putString(PopupDialog.MESSAGE_KEY, "Tag already exists");
                DialogFragment newFragment = new PopupDialog();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(),"badfields");
                return;
            }
        }
        //if it doesn't already exist add to tags list
        currPhoto.addTag(totalTag);
        //check to see if photo exists in other albums
        for(Album x: albums){
            for(Photo p: x.getPhotos()){
                if(p.getPhotoPath().equals(currPhoto.getPhotoPath())){
                    p = currPhoto;
                }
            }
        }

        //update the listview
        update();
        //write data to all albums' photo.dat

        
    }

    private void removeTag(Photo currPhoto) {
    ////////////////////////////////////////////
        //if nothing is selected in from the tags List
        if(selectedIndex == -1){
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "Please select a tag");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        //if list is empty
        if(currPhoto.getTags().size() == 0){
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "List is empty");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        //removing from tags list from all references
        String remove = tagsList.getItemAtPosition(selectedIndex).toString();
        currPhoto.getTags().remove(remove);
        for(Album x: albums){
            for(Photo p: x.getPhotos()){
                if(p.getPhotoPath().equals(currPhoto.getPhotoPath())){
                    p = currPhoto;
                }
            }
        }
        //update the listview
        update();
        //write data to all albums' photo.dat



    }
    //method to update the listview
    public void update(){
        tagsList.setAdapter(
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , currPhoto.getTags()));
    }
}