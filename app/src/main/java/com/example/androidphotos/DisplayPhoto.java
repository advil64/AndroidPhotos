package com.example.androidphotos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class DisplayPhoto extends AppCompatActivity {

    private ArrayList<Album> albums;
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

        Button addTagButton = findViewById(R.id.addTag);
        Button removeTagButton = findViewById(R.id.removeTag);
        Button slideshowButton = findViewById(R.id.slideshow);

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

        //setting on action listener for slideshow button
        slideshowButton = findViewById(R.id.slideshow);
        slideshowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideshow(albums.get(albumIndex).getPhotos());
            }
        });
    }
    
    private void addTag(Photo currPhoto){
        Spinner tagType = findViewById(R.id.tagType);
        EditText tagText = findViewById(R.id.tagText);
        String type = tagType.getSelectedItem().toString();
        String tag = tagText.getText().toString().trim();
        String totalTag = type + ": " + tag;
        //check to see if both type and tag text are filled
        if(type.equals("None") || type.equals("") || type == null || tag.equals("") || tag == null){
            //show pop-up error (both fields are required)
            showError("Tag type and tag fields are both required");
            return;
        }
        for(String s: currPhoto.getTags()){
            //check to see if tag already exists
            if(s.equalsIgnoreCase(totalTag)){
                //show pop-up error (tag already exists)
                showError("Tag already exists");
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
            showError("Please select a tag");
            return;
        }
        //if list is empty
        if(currPhoto.getTags().size() == 0){
            showError("List is empty");
            return;
        }
        //removing from tags list from all references
        String remove = tagsList.getItemAtPosition(selectedIndex).toString();
        //check to see if photo exists in other albums
        for(Album a: albums){
            for(Photo p: a.getPhotos()){
                if(p.getPhotoPath().equals(currPhoto.getPhotoPath()) && p.getCaption().equals(currPhoto.getCaption())){
                    p.removeTag(remove);
                }
            }
            try {
                writePhotos(a);
            } catch (IOException e) {
                showError("An error occurred while trying to save data, please try again");
            }
        }
        //update the list view
        update();
    }

    //method to update the list view
    public void update(){
        tagsList.setAdapter(
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , albums.get(albumIndex).getPhotos().get(photoIndex).getTags()));
    }

    //method to raise the error dialog
    public void showError(String err){
        Bundle bundle = new Bundle();
        bundle.putString(PopupDialog.MESSAGE_KEY, err);
        DialogFragment newFragment = new PopupDialog();
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "badfields");
    }

    //method to send to slideshow page
    private void slideshow(ArrayList<Photo> list){
        Intent intent = new Intent(this, SlideShow.class);
        Bundle args = new Bundle();
        args.putSerializable("PHOTO LIST", (Serializable)list);
        intent.putExtra("BUNDLE",args);
        startActivity(intent);
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