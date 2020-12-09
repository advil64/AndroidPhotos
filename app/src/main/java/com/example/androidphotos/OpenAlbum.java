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
import android.widget.Spinner;

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
    private int selectedIndex = -1;
    private Album currAlbum;

    ArrayList<Photo> photos= new ArrayList<>();
    ArrayList<Album> albums= new ArrayList<>();
    int albumIndex = 0;
    int photoIndex = 0;
    boolean isInitialized = false;

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
        albumIndex = (int)args.getSerializable("ALBUM INDEX");
        photos = albums.get(albumIndex).getPhotos();
        currAlbum = albums.get(albumIndex);

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
        Button displayPhotoButton = findViewById(R.id.displayPhotoButton);
        displayPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPhotoActivity();
            }
        });

        //setting up on click listener for remove button
        Button removePhotoButton = findViewById(R.id.removePhotoButton);
        removePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePhotoActivity();
            }
        });

        //make a albums array list of just their names
        ArrayList<String> albumNames = new ArrayList<>();
        albums.forEach(a -> albumNames.add(a.albumName));
        albumNames.add(0, "Move Photo");

        //populating spinner options
        Spinner movePhotoButton = findViewById(R.id.movePhotoButton);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, albumNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        movePhotoButton.setAdapter(arrayAdapter);

        //setting on action listener for display button
        movePhotoButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(isInitialized) {
                    movePhotoActivity(albumNames.get(position));
                } else{
                    isInitialized = true;
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                showError("Album Not Selected");
            }
        });
    }

    public void removePhotoActivity(){

        //list is empty
        if (photos.size() == 0) {
            //show pop-up error
            showError("List is empty, there is nothing to moving");
            return;
        } else if (selectedIndex == -1) {
            //nothing was selected
            showError("Please select an item before moving");
            return;
        }

        //retrieve photo and remove accordingly from the selected album
        Photo p = (Photo) listview.getItemAtPosition(selectedIndex);
        currAlbum.removePhoto(p);
        try {
            ReadWrite.writeAlbumsToFile(albums);
            ReadWrite.writePhotos(currAlbum);
        } catch (Exception e){
            //Same album or the default album was selected
            showError("An error occurred while trying to move the photo, please try again");
            return;
        }
        update();
    }

    public void movePhotoActivity(String albumName) {

        //list is empty
        if (photos.size() == 0) {
            //show pop-up error
            showError("List is empty, there is nothing to moving");
            return;
        } else if (selectedIndex == -1) {
            //nothing was selected
            showError("Please select an item before moving");
            return;
        } else if(albumName.equals("Move Photo") || albumName.equals(currAlbum.albumName)){
            //Same album or the default album was selected
            showError("Please select a different album to move to");
            return;
        }

        //retrieve photo and move accordingly to the selected album
        Photo p = (Photo) listview.getItemAtPosition(selectedIndex);
        Album toMove = null;
        for(Album a: albums){
            if(a.albumName.equals(albumName)){
                toMove = a;
            }
        }
        if(toMove != null){
            if(toMove.getPhotos().contains(p)){
                showError("The selected picture already exists in the album");
            } else{
                currAlbum.removePhoto(p);
                toMove.addPhoto(p);
            }
        }
        try {
            ReadWrite.writeAlbumsToFile(albums);
            ReadWrite.writePhotos(currAlbum);
            ReadWrite.writePhotos(toMove);
        } catch (Exception e){
            //Same album or the default album was selected
            showError("An error occurred while trying to move the photo, please try again");
            return;
        }
        update();
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
            showError("List is empty, there is nothing to display");
        } else if(selectedIndex == -1){
            //nothing was selected
            showError("Please select an item before displaying");
            return;
        }

        //otherwise display the selected photo
        Intent intent = new Intent(this, DisplayPhoto.class);
        Bundle args = new Bundle();
        args.putSerializable("ALL ALBUMS", (Serializable)albums);
        args.putSerializable("ALBUM INDEX", (Serializable)albumIndex);
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

                    //check if the photo being added already exists in another album
                    for(Album a: albums){
                        for(Photo p: a.getPhotos()){
                            if(p.equals(newPhoto)){
                                if(a.albumName.equals(currAlbum.albumName)){
                                    showError("Duplicate photos not allowed, pick a different photo");
                                } else{
                                    newPhoto = p;
                                    currAlbum.addPhoto(newPhoto);
                                    update();
                                    try {
                                        ReadWrite.writePhotos(currAlbum);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                return;
                            }
                        }
                    }

                    //if the photo does not exist already, ask for a caption
                    Intent intent = new Intent(this, AddPhoto.class);
                    Bundle args = new Bundle();
                    args.putSerializable("PHOTO", (Serializable)newPhoto);
                    args.putSerializable("ALL ALBUMS", (Serializable)albums);
                    args.putSerializable("ALBUM INDEX", albumIndex);
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
                        newPhoto = (Photo)bundle.getSerializable("NEW PHOTO");
                        currAlbum.addPhoto(newPhoto);
                        update();
                        try {
                            ReadWrite.writePhotos(currAlbum);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            //returning from display activity
            case 3:
                //read all the albums
                for(Album x: albums){
                    try {
                        x.getPhotos().clear();
                        try {
                            ReadWrite.readPhotos(x);
                        } catch (ClassNotFoundException e) {
                            showError("An error occurred while trying to overwrite data, please try again");
                        }
                    } catch (IOException e) {
                        //Same album or the default album was selected
                        showError("An error occurred while trying to overwrite data, please try again");
                        return;
                    }
                }
                break;
        }
    }

    //method to raise the error dialog
    public void showError(String err){
        Bundle bundle = new Bundle();
        bundle.putString(PopupDialog.MESSAGE_KEY, err);
        DialogFragment newFragment = new PopupDialog();
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "badfields");
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