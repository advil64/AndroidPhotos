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
    private Album currAlbum;
    private Button removePhotoButton;
    private Button displayPhotoButton;
    private int selectedIndex = -1;

    ArrayList<Photo> photos= new ArrayList<>();
    ArrayList<Album> albums= new ArrayList<>();

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
        currAlbum = (Album)args.getSerializable("ALBUM");
        photos = currAlbum.getPhotos();

        //setting up list of photos
        listview = (ListView) findViewById(R.id.PhotoList);
        listview.setAdapter(new PhotosAdapter(this, R.id.PhotoList, photos));
        listview.setClickable(true);
        listview.setItemsCanFocus(false);
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
        args.putSerializable("ALL ALBUMS", (Serializable)albums);
        args.putSerializable("PHOTO",(Serializable)listview.getItemAtPosition(selectedIndex));
        intent.putExtra("BUNDLE",args);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Photo newPhoto = null;
        switch(requestCode) {

            //first two cases are for the camera image returned
            case 0:
            case 1:
                if(resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    newPhoto = new Photo("", new ArrayList<>(), selectedImage.toString());
                    Intent intent = new Intent(this, AddPhoto.class);
                    Bundle args = new Bundle();
                    args.putSerializable("PHOTO", (Serializable)newPhoto);
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
                        currAlbum.addPhoto(newPhoto);
                        update();
                    }
                }
                break;
        }
    }

    //method to update the list view
    public void update(){
        listview.setAdapter(new PhotosAdapter(this, R.id.PhotoList, photos));
    }

    //method to read photos from file
    private ArrayList<Photo> readPhotos(Album currAlbum) throws IOException, ClassNotFoundException {
        //create the file if it doesn't exist
        File temp = new File("data/data/com.example.androidphotos/data/" + currAlbum.getAlbumName() + "/photo.dat");
        try {
            temp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> combo = new ArrayList<>();
        combo.add("Location");
        combo.add("Person");
        ArrayList<Photo> photos = new ArrayList<>();
        ObjectInputStream ois;
        try{
            ois = new ObjectInputStream(new FileInputStream("data/data/com.example.androidphotos/data/" + currAlbum.getAlbumName() + "/photo.dat"));
        } catch(EOFException e) {
            return photos;
        }
        //read the .dat file and populate the observable list (list of albums)
        while(true) {
            try {
                String temp1 = (String) ois.readObject();
                //find substrings of caption, tags, datetime, photoPath
                int delimeter1 = temp1.indexOf("\\|");
                //getting the captions
                String caption = temp1.substring(0, delimeter1);
                int delimeter2 = temp1.lastIndexOf("\\|");
                //getting the tags
                String tagTemp = temp1.substring(delimeter1+2, delimeter2-1);
                String[] arr = tagTemp.split(" ");
                ArrayList<String> tags = new ArrayList<>();
                String tag = "";
                int i = 1;
                boolean boo = false;
                for(String s: arr) {
                    //to avoid the null pointer exception in next if statement
                    if(s.equals("")) {
                        continue;
                    }
                    //if it's in combo append s to tag
                    for(String c: combo) {
                        if(c.equalsIgnoreCase(s.substring(0, s.length()-1))) {
                            boo = true;
                        }
                    }
                    if(boo == true) {
                        boo = false;
                        //check to see if tag is empty (new starting)
                        if(tag.equals("")) {
                            tag = tag + s + " ";
                        }
                        //not empty add tag to list and start tag over
                        else {
                            //check for commas at the end
                            if(tag.charAt(tag.length()-1) == ',') {
                                tags.add(tag.substring(0,tag.length()-1).trim());
                                tag = "";
                                tag = tag + s + " ";
                            }
                            else {
                                tags.add(tag.trim());
                                tag = "";
                                tag = tag + s + " ";
                            }
                        }
                    }
                    //not a tag type
                    else {
                        //if we reach the end
                        if(i == arr.length) {
                            tag = tag + s + " ";
                            tags.add(tag.trim());
                        }
                        //append s to tag because type is already there and continue
                        else {
                            if(s.charAt(s.length()-1) == ',') {
                                tag = tag + s.substring(0, s.length()-1);
                            }
                            else {
                                tag = tag + s + " ";
                            }
                        }
                    }
                    //increment i to keep count of number of words to know when we are ending
                    i++;
                }
                //getting the location
                String location = temp1.substring(delimeter2+1);
                Photo toAdd = new Photo(caption,tags,location);
                if(!currAlbum.getPhotos().contains(toAdd)){
                    currAlbum.addPhoto(toAdd);
                }
                photos.add(new Photo(caption,tags,location));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return photos;
        }
    }

    //method to write photos to file
    public static void writePhotos(Album currAlbum) throws IOException{
        FileOutputStream fos = new FileOutputStream("data/data/com.example.androidphotos/data/" + currAlbum.getAlbumName() + "/photo.dat");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        for(Photo x : currAlbum.getPhotos()) {
            oos.writeObject(x.toString());
        }
    }

}