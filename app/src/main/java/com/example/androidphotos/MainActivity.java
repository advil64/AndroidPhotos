package com.example.androidphotos;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button createButton;
    private Button renameButton;
    private Button deleteButton;
    private Button openAlbumButton;
    private Button searchButton;
    private ListView listview;

    public static final int ADD_ALBUM_CODE = 1;
    public static final int RENAME_ALBUM_CODE = 2;
    public static final int OPEN_ALBUM_CODE = 3;

    //ArrayList of Albums
    ArrayList<Album> albums= new ArrayList<>();
    int selectedIndex = 0;
    int albumIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = findViewById(R.id.AlbumsList);

        //make data folder if it doesn't exist
        File folder = new File("data/data/com.example.androidphotos/data");
        if (!folder.exists()) {
            folder.mkdir();
        }
        //create file if it doesn't exist
        File file = new File("data/data/com.example.androidphotos/data/albums.dat");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //read all album names
        albums = ReadWrite.readAlbums();
        //read all photos for each album
        for(Album x: albums){
            try {
                ReadWrite.mainReadPhotos(x);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        listview.setAdapter(new ArrayAdapter<Album>(this,android.R.layout.simple_list_item_1, albums));
        listview.setClickable(true);
        //setting the index of the item clicked on
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedIndex = position;
            }
        });


        //setting on action listener for create button
        createButton = findViewById(R.id.Create);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateActivity();
            }
        });

        //setting on action listener for rename button
        renameButton = findViewById(R.id.Rename);
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRenameActivity();
            }
        });

        //setting on action listener for delete button
        deleteButton = findViewById(R.id.Delete);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openDeleteActivity();
            }
        });

        //setting on action listener for open album button
        openAlbumButton = findViewById(R.id.OpenAlbum);
        openAlbumButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openAlbumActivity();
            }
        });

        //setting on action listener for search button
        searchButton = findViewById(R.id.Search);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openSearchActivity();
            }
        });
    }

    //method to start the create activity
    public void openCreateActivity() {
        Intent intent = new Intent(this, CreateAlbum.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable)albums);
        intent.putExtra("BUNDLE",args);
        startActivityForResult(intent,ADD_ALBUM_CODE);
    }

    //method to start the rename activity
    public void openRenameActivity() {
        //list is empty
        if(albums.size() == 0){
            //show pop-up error
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "List is empty, there is nothing to rename");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        //nothing was selected
        if(selectedIndex == -1){
            //show pop-up error
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "Please select an item before renaming");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        Intent intent = new Intent(this, RenameAlbum.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable)albums);
        intent.putExtra("BUNDLE",args);
        String selectedItem = listview.getItemAtPosition(selectedIndex).toString();
        intent.putExtra("ITEM", selectedItem);
        startActivityForResult(intent,RENAME_ALBUM_CODE);
    }

    //method to start the delete album activity
    public void openDeleteActivity(){
        //list is empty
        if(albums.size() == 0){
            //show pop-up error
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "List is empty, there is nothing to rename");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        //nothing was selected
        if(selectedIndex == -1){
            //show pop-up error
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "Please select an item before renaming");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        //show confirmation window
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete this album?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete album from list
                      String album = listview.getItemAtPosition(selectedIndex).toString().trim();
                        for(Album x: albums){
                            if(x.toString().trim().equals(album)){
                                albums.remove(x);
                                ReadWrite.writeAlbumsToFile(albums);
                                update();
                                //delete the directory
                                String albumName = x.getAlbumName();
                                File photoFile = new File("data/data/com.example.androidphotos/data/" + albumName + "/photo.dat");
                                File albumFile = new File("data/data/com.example.androidphotos/data/" + albumName);
                                photoFile.delete();
                                albumFile.delete();
                                break;
                            }
                        }
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //method to start the open album activity
    public void openAlbumActivity() {
        //list is empty
        if(albums.size() == 0){
            //show pop-up error
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "List is empty, there is nothing to open");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        //nothing was selected
        if(selectedIndex == -1){
            //show pop-up error
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "Please select an item before trying to open");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
        Intent intent = new Intent(this, OpenAlbum.class);
        Bundle args = new Bundle();
        args.putSerializable("ALL ALBUMS", (Serializable)albums);
        //args.putSerializable("ALBUM",(Serializable)listview.getItemAtPosition(selectedIndex));
        for(int i=0; i<albums.size(); i++){
            if(listview.getItemAtPosition(selectedIndex).equals(albums.get(i))){
                albumIndex = i;
                break;
            }
        }
        args.putSerializable("ALBUM INDEX", albumIndex);
        intent.putExtra("BUNDLE",args);
        startActivityForResult(intent,OPEN_ALBUM_CODE);
    }

    //method to start the open search activity
    public void openSearchActivity() {
        Intent intent = new Intent(this, Search.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable)albums);
        intent.putExtra("BUNDLE",args);
        startActivity(intent);
    }

    //method is run on return from create and rename activities
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //if user pressed cancel
        if (resultCode != RESULT_OK) {
            return;
        }

        //extract the album name from the bundle
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        //rename album in the list
        if (requestCode == RENAME_ALBUM_CODE) {
            String name = bundle.getString(RenameAlbum.ALBUM_NAME);
            String oldName = bundle.getString("OLD");
            for(Album x: albums){
                if(x.getAlbumName().equals(oldName)){
                    x.setAlbumName(name);
                    //rename album directory
                    File newName = new File("data/data/com.example.androidphotos/data/" + name);
                    File file = new File("data/data/com.example.androidphotos/data/" + oldName);
                    file.renameTo(newName);
                    break;
                }
            }
        }
        else if(requestCode == OPEN_ALBUM_CODE){
            //read all the albums (when returning from open Album)
            for(Album x: albums){
                try {
                    x.getPhotos().clear();
                    ReadWrite.mainReadPhotos(x);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //add album to list (Create)
        else {
            String name = bundle.getString(CreateAlbum.ALBUM_NAME);
            albums.add(new Album(name, new ArrayList<Photo>()));
            //create the album directory with a photo.dat file in it
            File folder = new File("data/data/com.example.androidphotos/data/" + name);
            if (!folder.exists()) {
                folder.mkdir();
            }
            //create file if it doesn't exist
            File file = new File("data/data/com.example.androidphotos/data/" + name + "/photo.dat");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // redo the adapter to reflect change
        ReadWrite.writeAlbumsToFile(albums);
        update();
    }

    //method to update the listview
    public void update(){
        listview.setAdapter(
                new ArrayAdapter<Album>(this,android.R.layout.simple_list_item_1 , albums));
    }
}