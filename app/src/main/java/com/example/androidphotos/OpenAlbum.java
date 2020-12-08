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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class OpenAlbum extends AppCompatActivity {

    private ListView listview;
    private Button removePhotoButton;
    private Button displayPhotoButton;
    private int selectedIndex = -1;

    ArrayList<Photo> photos= new ArrayList<>();
    ArrayList<Album> albums= new ArrayList<>();
    int albumIndex = 0;
    int photoIndex = 0;

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
        albums = (ArrayList<Album>)args.getSerializable("ALL ALBUMS");
        //currAlbum = (Album)args.getSerializable("ALBUM");
        albumIndex = (int)args.getSerializable("ALBUM INDEX");
        photos = albums.get(albumIndex).getPhotos();

        //setting up list of photos
        listview = (ListView) findViewById(R.id.PhotoList);
        listview.setAdapter(new PhotosAdapter(this, R.id.PhotoList, photos));
        listview.setClickable(true);
        listview.setItemsCanFocus(false);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), 1);
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
        args.putSerializable("ALL ALBUMS", (Serializable)albums);
        args.putSerializable("ALBUM INDEX", (Serializable)albumIndex);
        //args.putSerializable("PHOTO INDEX",(Serializable)listview.getItemAtPosition(selectedIndex));
        Photo p = (Photo) listview.getItemAtPosition(selectedIndex);
        for(int i=0; i<albums.size(); i++){
            for(int j=0; j<albums.get(i).getPhotos().size(); j++){
                if(p == (albums.get(i).getPhotos().get(j))){
                    photoIndex = j;
                    break;
                }
            }
        }
        args.putSerializable("PHOTO INDEX", (Serializable)photoIndex);
        intent.putExtra("BUNDLE",args);
        startActivityForResult(intent, 3);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Photo newPhoto = null;
        switch(requestCode) {
            //first two cases are for the camera image returned
            case 0:
            case 1:
                if(resultCode == RESULT_OK) {
                    final int takeFlags = imageReturnedIntent.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    try {
                        getContentResolver().takePersistableUriPermission(imageReturnedIntent.getData(), takeFlags);
                    } catch (Exception e) { }
                    Uri selectedImage = imageReturnedIntent.getData();
                    newPhoto = new Photo("", new ArrayList<>(), selectedImage.toString());
                    Intent intent = new Intent(this, AddPhoto.class);
                    Bundle args = new Bundle();
                    args.putSerializable("PHOTO", (Serializable)newPhoto);
                    args.putSerializable("ALL ALBUMS", (Serializable)albums);
                    intent.putExtra("BUNDLE", args);
                    startActivityForResult(intent, 2);
                }
                break;

            //this case is for the name of the image
            case 2:
                if(resultCode == RESULT_OK){
                    Bundle bundle = imageReturnedIntent.getExtras();
                    //get the most recently added image
                    if(bundle != null){
                        String name = bundle.getString("CAPTION");
                        String photoPath = bundle.getString("PHOTOPATH");
                        newPhoto = new Photo(name, new ArrayList<>(), photoPath);
                        //currAlbum.addPhoto(newPhoto);
                        albums.get(albumIndex).addPhoto(newPhoto);
                        update();
                        try {
                            //writePhotos(currAlbum);
                            ReadWrite.writePhotos(albums.get(albumIndex));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            case 3:
                //read all the albums (when returning from display)
                for(Album x: albums){
                    try {
                        x.getPhotos().clear();
                        try {
                            ReadWrite.readPhotos(x);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    //method to update the list view
    public void update(){
        listview.setAdapter(new PhotosAdapter(this, R.id.PhotoList, photos));
    }

    //method to send back to Main Activity
    @Override
    public void onBackPressed(){
        Bundle bundle = new Bundle();
        bundle.putString("ALBUM_NAME",albums.get(albumIndex).getAlbumName());
        Intent intent2 = new Intent();
        intent2.putExtras(bundle);
        setResult(RESULT_OK,intent2);
        super.onBackPressed();
        finish(); // pops activity from the call stack, returns to parent
    }
}