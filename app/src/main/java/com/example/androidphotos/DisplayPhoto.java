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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DisplayPhoto extends AppCompatActivity {

    private ArrayList<Album> albums;
    private Button addTagButton;
    private Button removeTagButton;
    private Spinner tagType;
    private EditText tagText;
    private ListView tagsList;

    int albumIndex = 0;
    int photoIndex = 0;
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

        //retrieving all albums
        albums = (ArrayList<Album>)args.getSerializable("ALL ALBUMS");
        albumIndex = (int)args.getSerializable("ALBUM INDEX");
        photoIndex = (int)args.getSerializable("PHOTO INDEX");

        //setting image view
        ImageView myImageView = findViewById(R.id.image);
        myImageView.setImageURI(albums.get(albumIndex).getPhotos().get(photoIndex).getPhotoPath());

        addTagButton = findViewById(R.id.addTag);
        removeTagButton = findViewById(R.id.removeTag);

        tagsList = findViewById(R.id.tagsList);
        tagsList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, albums.get(albumIndex).getPhotos().get(photoIndex).getTags()));
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
                addTag(albums.get(albumIndex).getPhotos().get(photoIndex));
            }
        });

        //setting on action listener for delete tag button
        removeTagButton = findViewById(R.id.removeTag);
        removeTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeTag(albums.get(albumIndex).getPhotos().get(photoIndex));
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
        //check to see if photo exists in other albums
        for(int i=0; i<albums.size(); i++){
            for(int j=0; j<albums.get(i).getPhotos().size(); j++){
                if(albums.get(i).getPhotos().get(j).getPhotoPath().equals(currPhoto.getPhotoPath())){
                    albums.get(i).getPhotos().get(j).addTag(totalTag);
                }
            }
            try {
                writePhotos(albums.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //update the listview
        update();
    }

    private void removeTag(Photo currPhoto){
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
        //check to see if photo exists in other albums
        for(int i=0; i<albums.size(); i++){
            for(int j=0; j<albums.get(i).getPhotos().size(); j++){
                if(albums.get(i).getPhotos().get(j).getPhotoPath().equals(currPhoto.getPhotoPath())){
                    albums.get(i).getPhotos().get(j).addTag(remove);
                }
            }
            try {
                writePhotos(albums.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //update the listview
        update();
    }
    //method to update the listview
    public void update(){
        tagsList.setAdapter(
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , albums.get(albumIndex).getPhotos().get(photoIndex).getTags()));
    }

    //method to write photos to file
    public static void writePhotos(Album currentAlbum) throws IOException{
        FileOutputStream fos = new FileOutputStream("data/data/com.example.androidphotos/data/" + currentAlbum.getAlbumName() + "/photo.dat");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        for(Photo x : currentAlbum.getPhotos()) {
            oos.writeObject(x.toString());
        }
    }

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