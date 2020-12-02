package com.example.androidphotos;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //obtaining the list of albums
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        ArrayList<Album> list = (ArrayList<Album>) args.getSerializable("ARRAYLIST");

        listview = findViewById(R.id.searchPhotoList);
        tag1 = findViewById(R.id.tag1);
        tag2 = findViewById(R.id.tag2);
        tagType1 = findViewById(R.id.tagType1);
        tagType2 = findViewById(R.id.tagType2);
        conjunction = findViewById(R.id.conjunctionType);

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
            if(firstTagType.equals("") || firstTagType==null || secondTagType.equals("") || secondTagType==null){
                //show pop-up error (both tags types are required)
                Bundle bundle = new Bundle();
                bundle.putString(PopupDialog.MESSAGE_KEY, "Please choose a tag type for both tags for AND conjunction");
                DialogFragment newFragment = new PopupDialog();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(),"badfields");
                return;
            }
            String totalTag1 = firstTagType + ": " + firstTag;
            String totalTag2 = secondTagType + ": " + secondTag;
            //loop through photos and see if photo has both tags if so add to listview
            int count = 0;
            for(Photo p: photos){
                for(String p1: p.getTags()){
                    if(p1.contains(totalTag1)){
                        for(String p2: p.getTags()){
                            if(p2.contains(totalTag2)){
                                count++;
                                //add to listview
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
            if(firstTag.equals("") || firstTag == null || secondTag.equals("") || secondTag == null){
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
                if(firstTagType.equals("") || firstTagType == null){
                    //show pop-up error (tag type 1 is required)
                    Bundle bundle = new Bundle();
                    bundle.putString(PopupDialog.MESSAGE_KEY, "Please select a tag type for the first tag");
                    DialogFragment newFragment = new PopupDialog();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(),"badfields");
                    return;
                }
                String totalTag1 = firstTagType + ": " + firstTag;
                //loop through photos and see if photo has both tags if so add to listview
                int count = 0;
                for(Photo p: photos){
                    for(String t: p.getTags()){
                        if(t.contains(totalTag1)){
                            //add to list view
                            count++;
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
                if(secondTagType.equals("") || secondTagType == null){
                    //show pop-up error (tag type 2 is required required)
                    Bundle bundle = new Bundle();
                    bundle.putString(PopupDialog.MESSAGE_KEY, "Please select a tag type for the second tag");
                    DialogFragment newFragment = new PopupDialog();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(),"badfields");
                    return;
                }
                String totalTag2 = secondTagType + ": " + secondTag;
                //loop through photos and see if photo has both tags if so add to listview
                int count = 0;
                for(Photo p: photos){
                    for(String t: p.getTags()){
                        if(t.contains(totalTag2)){
                            //add to list view
                            count++;
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
                if(firstTagType.equals("") || firstTagType == null || secondTagType.equals("") || secondTagType == null){
                    //show pop-up error (both tag types are required required)
                    Bundle bundle = new Bundle();
                    bundle.putString(PopupDialog.MESSAGE_KEY, "Please select a tag type for both tags");
                    DialogFragment newFragment = new PopupDialog();
                    newFragment.setArguments(bundle);
                    newFragment.show(getSupportFragmentManager(),"badfields");
                    return;
                }
                String totalTag1 = firstTagType + ": " + firstTag;
                String totalTag2 = secondTagType + ": " + secondTag;
                //loop through photos and see if photo has one of the tags if so add to listview
                int count = 0;
                for(Photo p: photos){
                    for(String p1: p.getTags()){
                        if(p1.contains(totalTag1) || p1.contains(totalTag2)){
                            count++;
                            //add to listview
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
    }
}