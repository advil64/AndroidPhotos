package com.example.androidphotos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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

    //ArrayList of Albums
    ArrayList<Album> albums= new ArrayList<>();
    int selectedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = findViewById(R.id.AlbumsList);
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

        //TODO add reading from file to listview

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
                openRenameActivity();
            }
        });

        //setting on action listener for delete button
        deleteButton = findViewById(R.id.Delete);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                System.out.println("Delete button was clicked");
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
                searchActivity();
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
        Intent intent = new Intent(this, RenameAlbum.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST",(Serializable)albums);
        intent.putExtra("BUNDLE",args);
        String selectedItem = listview.getItemAtPosition(selectedIndex).toString();
        intent.putExtra("ITEM", selectedItem);
        startActivityForResult(intent,RENAME_ALBUM_CODE);
    }

    //method to start the open album activity
    public void openAlbumActivity() {
        Intent intent = new Intent(this, OpenAlbum.class);
        startActivity(intent);
    }

    //method to start the open album activity
    public void searchActivity() {
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }

    //method is run on return from other activities
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
                    break;
                }
            }
        }
        //add album to list (Create)
        else {
            String name = bundle.getString(CreateAlbum.ALBUM_NAME);
            albums.add(new Album(name));
        }

        // redo the adapter to reflect change
        listview.setAdapter(
                new ArrayAdapter<Album>(this,android.R.layout.simple_list_item_1 , albums));
    }
}