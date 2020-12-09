package com.example.androidphotos;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class that defines a Photo
 * @author Advith Chegu
 * @author Banty Patel
 */
public class Photo implements Serializable {
    private String caption;
    private ArrayList<String> tags;
    public String photoPath;

    /**
     * Creates a new photo object
     * @param caption caption of the photo
     * @param photoPath filepath of the photo on user's computer
     * @param tags tags of the photo in tag name: tag value format
     */
    public Photo(String caption, ArrayList<String> tags, String photoPath){
        this.caption = caption;
        this.tags = tags;
        this.photoPath = photoPath;
    }

    /**
     * method sets the caption of the photo
     * @param caption - caption of photo
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * method to add a tag to a photo
     * @param tag - tag to be added
     */
    public void addTag(String tag){
        tags.add(tag);
    }
    /**
     * method to remove a tag from a photo
     * @param tag - tag to be removed
     */
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    /**
     * method to get the caption of a photo
     * @return - the caption of the photo
     */
    public String getCaption() {
        return caption;
    }

    /**
     * method to get the list of tags for a photo
     * @return - arraylist of tags
     */
    public ArrayList<String> getTags() {
        return tags;
    }

    /**
     * method to get the photo path of a photo
     * @return - path of photo
     */
    public Uri getPhotoPath() {
        return Uri.parse(photoPath);
    }

    /**
     * method used to display the caption of a photo
     * @return - caption string
     */
    public String toString2(){
        return "Caption: " + this.caption;
    }

    /**
     * method used to serialize the photo data
     * @return - string to be serialized
     */
    @Override
    public String toString() {
        return this.caption + "|" + this.tags + "|" + this.photoPath;
    }

    /**
     * method to produce a hashcode of a caption
     * @return - hashcode
     */
    @Override
    public int hashCode() {
        return this.caption.hashCode();
    }

    /**
     * method to check equality of two photos
     * @return - bool used for comparison
     */
    @Override
    public boolean equals(Object o) {

        //convert to a photo object
        if(o == null){
            return false;
        } else if(!(o instanceof Photo)){
            return false;
        }
        Photo toCompare = (Photo)o;
        return toCompare.photoPath.equals(this.photoPath);
    }
}
