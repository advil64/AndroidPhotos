package com.example.androidphotos;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Class that defines an Album
 * @author Advith Chegu
 * @author Banty Patel
 */
public class Album implements Serializable {
    public String albumName;
    public ArrayList<Photo> albumPhotos;

    /**
     * Creates a new Album object
     * @param albumName name of the new album
     */
    public Album(String albumName){
        this.albumName = albumName;
        this.albumPhotos = null;
    }

    /**
     * Add a new photo to the album and sets the date range
     * @param newPhoto photo to be added to the album
     */
    public void addPhoto(Photo newPhoto){
        this.albumPhotos.add(newPhoto);
    }

    /**
     * Removes a photo from the list and set the new date range
     * @param oldPhoto - photo to be removed
     */
    public void removePhoto(Photo oldPhoto){
        this.albumPhotos.remove(oldPhoto);
    }

    /**
     * Returns an ArrayList of the user's photos
     * @return album's photos
     */
    public ArrayList<Photo> getPhotos() {
        return this.albumPhotos;
    }

    /**
     * sets the photos list for the album
     * @param photos - list of photos
     */
    public void setPhotos(ArrayList<Photo> photos) {
        this.albumPhotos = photos;
    }

    /**
     * method to get the name of the album
     * @return - albumname
     */
    public String getAlbumName() {
        return albumName;
    }

    /**
     * method to set the name of the album
     * @param albumName - name of album
     */
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    /**
     * To string method used for serializing album data
     */
    @Override
    public String toString() {
        return "Album name: " + this.albumName;
    }

    public String toFile(){
        return this.albumName + "|";
    }
}

