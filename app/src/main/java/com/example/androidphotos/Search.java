package com.example.androidphotos;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.Button;
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
    private Button searchButton;

    ArrayList<Album> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //obtaining the list of albums
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        list = (ArrayList<Album>) args.getSerializable("ARRAYLIST");

        ArrayList<Photo> toDisplay = new ArrayList<>();

        listview = findViewById(R.id.searchPhotoList);
        tag1 = findViewById(R.id.tag1);
        tag2 = findViewById(R.id.tag2);
        tagType1 = findViewById(R.id.tagType1);
        tagType2 = findViewById(R.id.tagType2);
        conjunction = findViewById(R.id.conjunctionType);

        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(toDisplay);
            }
        });
    }
    public void search(ArrayList<Photo> toDisplay) {
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

//        ArrayList<String> t1 = new ArrayList<>();
//        t1.add("Location: tag1");
//        t1.add("Person: tag");
//        photos.add(new Photo("Hello1", t1, "path1"));
//        photos.add(new Photo("Hello2", t1, "path2"));

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
            if(firstTagType.equals("") || firstTagType==null || firstTagType.equals("None") || secondTagType.equals("") || secondTagType==null || secondTagType.equals("None")){
                //show pop-up error (both tags types are required)
                Bundle bundle = new Bundle();
                bundle.putString(PopupDialog.MESSAGE_KEY, "Please choose a tag type for both tags for AND conjunction");
                DialogFragment newFragment = new PopupDialog();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(),"badfields");
                return;
            }
            //loop through photos and see if photo has both tags if so add to listview
            int count = 0;
            boolean present = false;
            for(Photo p: photos){
                for(String t: p.getTags()){
                    if(t.contains(firstTagType)){
                        int firstSize = firstTagType.length();
                        if(t.substring(firstSize+2).contains(firstTag)){
                            for(String t2: p.getTags()){
                                if(t2.contains(secondTagType)){
                                    int secondSize = secondTagType.length();
                                    if(t2.substring(secondSize+2).contains(secondTag)){
                                        //check for duplicates
                                        for (Photo x : toDisplay) {
                                            if (x.getPhotoPath().equals(p.getPhotoPath())) {
                                                present = true;
                                                count++;
                                                break;
                                            }
                                        }
                                        if (present == false) {
                                            toDisplay.add(p);
                                            count++;
                                            //ADD TO LIST
                                        }
                                        present = false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(count == 0){
                //show pop-up error (no photo with provided tags)
                Bundle bundle = new Bundle();
                bundle.putString(PopupDialog.MESSAGE_KEY, "There are no photos with the provided tags");
                DialogFragment newFragment = new PopupDialog();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(),"badfields");
                return;
            }

        }
        //OR CONJUNCTION
        else if (conjunctionType.equals("OR")){
            String firstTag = tag1.getText().toString().trim();
            String secondTag = tag2.getText().toString().trim();
            if((firstTag.equals("") || firstTag == null) && (secondTag.equals("") || secondTag == null)){
                //show pop-up error (at least one tag is required)
                Bundle bundle = new Bundle();
                bundle.putString(PopupDialog.MESSAGE_KEY, "Tag fields are empty, fill in at least one tag field");
                DialogFragment newFragment = new PopupDialog();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(),"badfields");
                return;
            }
            String firstTagType = tagType1.getSelectedItem().toString();
            String secondTagType = tagType2.getSelectedItem().toString();
            //if only first tag is filled
            if(!firstTag.equals("") && (secondTag.equals("") || secondTag == null)){
                if(firstTagType.equals("") || firstTagType == null || firstTagType.equals("None")) {
                    //show pop-up error (tag type 1 is required)
                    Bundle bundle = new Bundle();
                    bundle.putString(PopupDialog.MESSAGE_KEY, "Please select a tag type for the first tag");
                    DialogFragment newFragment = new PopupDialog();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(),"badfields");
                    return;
                }
                //loop through photos and see if photo has first tag if so add to listview
                int count = 0;
                boolean present = false;
                for(Photo p: photos){
                    for(String t: p.getTags()){
                        if(t.contains("Location: ") && firstTagType.equals("Location")){
                            if(t.substring(10).contains(firstTag)){
                                //check for duplicates
                                for (Photo x : toDisplay) {
                                    if (x.getPhotoPath().equals(p.getPhotoPath())) {
                                        present = true;
                                        count++;
                                        break;
                                    }
                                }
                                if (present == false) {
                                    toDisplay.add(p);
                                    count++;
                                    //ADD TO LIST
                                }
                                present = false;
                            }
                        }
                        else if(t.contains("Person: ") && firstTagType.equals("Person")){
                            if(t.substring(8).contains(firstTag)){
                                //check for duplicates
                                for (Photo x : toDisplay) {
                                    if (x.getPhotoPath().equals(p.getPhotoPath())) {
                                        present = true;
                                        count++;
                                        break;
                                    }
                                }
                                if (present == false) {
                                    toDisplay.add(p);
                                    count++;
                                    //ADD TO LIST
                                }
                                present = false;
                            }
                        }
                    }
                }
                if(count == 0){
                    //show pop-up error (no photo with provided tags)
                    Bundle bundle = new Bundle();
                    bundle.putString(PopupDialog.MESSAGE_KEY, "There are no photos with the provided tags");
                    DialogFragment newFragment = new PopupDialog();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(),"badfields");
                    return;
                }
            }
            //if only second tag is filled
            else if(!secondTag.equals("") && (firstTag.equals("") || firstTag == null)){
                if(secondTagType.equals("") || secondTagType == null || secondTagType.equals("None")){
                    //show pop-up error (tag type 2 is required required)
                    Bundle bundle = new Bundle();
                    bundle.putString(PopupDialog.MESSAGE_KEY, "Please select a tag type for the second tag");
                    DialogFragment newFragment = new PopupDialog();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(),"badfields");
                    return;
                }
                //loop through photos and see if photo has both tags if so add to listview
                int count = 0;
                boolean present = false;
                for(Photo p: photos){
                    for(String t: p.getTags()){
                        if(t.contains("Location: ") && secondTagType.equals("Location")){
                            if(t.substring(10).contains(secondTag)){
                                //check to see if photo is already in the list
                                for(Photo x: toDisplay){
                                    if(x.getPhotoPath().equals(p.getPhotoPath())){
                                        present = true;
                                        count++;
                                        break;
                                    }
                                }
                                if(present == false){
                                    toDisplay.add(p);
                                    count++;
                                    //ADD TO LIST
                                }
                                present = false;
                            }
                        }
                        else if(t.contains("Person: ")){
                            if(t.substring(8).contains(secondTag) && secondTagType.equals("Person")){
                                //check to see if photo is already in the list
                                for(Photo x: toDisplay){
                                    if(x.getPhotoPath().equals(p.getPhotoPath())){
                                        present = true;
                                        count++;
                                        break;
                                    }
                                }
                                if(present == false){
                                    toDisplay.add(p);
                                    count++;
                                    //ADD TO LIST
                                }
                                present = false;
                            }
                        }
                    }
                }
                if(count == 0){
                    //show pop-up error (no photo with provided tags)
                    Bundle bundle = new Bundle();
                    bundle.putString(PopupDialog.MESSAGE_KEY, "There are no photos with the provided tags");
                    DialogFragment newFragment = new PopupDialog();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(),"badfields");
                    return;
                }
            }
            //if both tags are filled
            else if(!firstTag.equals("") && !secondTag.equals("")){
                if(firstTagType.equals("") || firstTagType == null || firstTagType.equals("None") || secondTagType.equals("") || secondTagType == null || secondTagType.equals("None")){
                    //show pop-up error (both tag types are required required)
                    Bundle bundle = new Bundle();
                    bundle.putString(PopupDialog.MESSAGE_KEY, "Please select a tag type for both tags");
                    DialogFragment newFragment = new PopupDialog();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(),"badfields");
                    return;
                }
                //loop through photos and see if photo has one of the tags if so add to listview
                int count = 0;
                boolean present = false;
                for(Photo p: photos){
                    for(String p1: p.getTags()){
                        if(p1.contains("Location: ")){
                            if(firstTagType.equals("Location") && p1.substring(10).contains(firstTag)){
                                //check to see if photo is already in the list
                                for(Photo x: toDisplay){
                                    if(x.getPhotoPath().equals(p.getPhotoPath())){
                                        present = true;
                                        count++;
                                        break;
                                    }
                                }
                                if(present == false){
                                    toDisplay.add(p);
                                    count++;
                                    //ADD TO LIST
                                }
                                present = false;
                            }
                            if(secondTagType.equals("Location") && p1.substring(10).contains(secondTag)){
                                //check to see if photo is already in the list
                                for(Photo x: toDisplay){
                                    if(x.getPhotoPath().equals(p.getPhotoPath())){
                                        present = true;
                                        count++;
                                        break;
                                    }
                                }
                                if(present == false){
                                    toDisplay.add(p);
                                    count++;
                                    //ADD TO LIST
                                }
                                present = false;
                            }
                        }
                        if(p1.contains("Person: ")){
                            if(firstTagType.equals("Person") && p1.substring(8).contains(firstTag)){
                                //check to see if photo is already in the list
                                for(Photo x: toDisplay){
                                    if(x.getPhotoPath().equals(p.getPhotoPath())){
                                        present = true;
                                        count++;
                                        break;
                                    }
                                }
                                if(present == false){
                                    toDisplay.add(p);
                                    count++;
                                    //ADD TO LIST
                                }
                                present = false;
                            }
                            if(secondTagType.equals("Person") && p1.substring(8).contains(secondTag)){
                                //check to see if photo is already in the list
                                for(Photo x: toDisplay){
                                    if(x.getPhotoPath().equals(p.getPhotoPath())){
                                        present = true;
                                        count++;
                                        break;
                                    }
                                }
                                if(present == false){
                                    toDisplay.add(p);
                                    count++;
                                    //ADD TO LIST
                                }
                                present = false;
                            }
                        }
                    }
                }
                if(count == 0){
                    //show pop-up error (no photo with provided tags)
                    Bundle bundle = new Bundle();
                    bundle.putString(PopupDialog.MESSAGE_KEY, "There are no photos with the provided tags");
                    DialogFragment newFragment = new PopupDialog();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(),"badfields");
                    return;
                }
            }
        }
        else{
            //show pop-up error (conjunction is required)
            Bundle bundle = new Bundle();
            bundle.putString(PopupDialog.MESSAGE_KEY, "Please choose a conjunction");
            DialogFragment newFragment = new PopupDialog();
            newFragment.setArguments(bundle);
            newFragment.show(getSupportFragmentManager(),"badfields");
            return;
        }
//        System.out.println(toDisplay);
    }
}
