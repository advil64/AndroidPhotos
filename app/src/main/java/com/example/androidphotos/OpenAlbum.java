package com.example.androidphotos;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;

public class OpenAlbum extends AppCompatActivity {

    private ListView listview;
    private Album currAlbum;
    private Button removePhotoButton;
    private Button displayPhotoButton;
    private int selectedIndex = -1;

    ArrayList<Photo> photos= new ArrayList<>();

    public static final int TAGS_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setting up display
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setting up buttons
        Button addPhoto;

        //retrieving current album
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        currAlbum = (Album)args.getSerializable("ALBUM");
        photos = currAlbum.getPhotos();

        //setting up list of photos
        listview = findViewById(R.id.PhotoList);
        listview.setAdapter(new ArrayAdapter<>(this,R.layout.image_list_item, photos));
        listview.setClickable(true);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedIndex = position;
            }
        });

        //setting on action listener add photo button
        addPhoto = findViewById(R.id.addPhotoButton);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhotoActivity();
            }
        });

        //setting on action listener for display button
        displayPhotoButton = findViewById(R.id.displayPhotoButton);
        displayPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPhotoActivity();
            }
        });
    }

    public void pickPhotoActivity(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);
    }

    public void displayPhotoActivity(){
        //list is empty
        if(photos.size() == 0){
            //show pop-up error
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "List is empty, there is nothing to display");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        //nothing was selected
        if(selectedIndex == -1){
            //show pop-up error
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "Please select an item before displaying");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        Intent intent = new Intent(this, DisplayPhoto.class);
        Bundle args = new Bundle();
        args.putSerializable("PHOTO",(Serializable)listview.getItemAtPosition(selectedIndex));
        intent.putExtra("BUNDLE",args);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
            case 1:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    Photo newPhoto = new Photo("", new ArrayList<>(), selectedImage.toString());
                    currAlbum.addPhoto(newPhoto);
                    update();
                    Intent intent = new Intent(this, AddPhoto.class);
                    Bundle args = new Bundle();
                    args.putSerializable("PHOTO", (Serializable)newPhoto);
                    intent.putExtra("BUNDLE", args);
                    startActivity(intent);
                }
                break;
        }
    }

    //method to update the list view
    public void update(){
        PhotosAdapter customAdapter = new PhotosAdapter(this, photos);
        listview.setAdapter(customAdapter);
    }

}