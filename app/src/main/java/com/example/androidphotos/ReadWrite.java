package com.example.androidphotos;

import android.util.Log;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ReadWrite {

    //method to write back to file
    public static void writeAlbumsToFile(ArrayList<Album> data) {
        try {
            FileOutputStream fos = new FileOutputStream("data/data/com.example.androidphotos/data/albums.dat");
            OutputStreamWriter output = new OutputStreamWriter(fos);
            String text = "";
            for(Album x: data){
                text += x.toFile();
            }
            output.write(text);
            output.close();
        }
        catch (IOException e) {
            Log.e("Exception", "Write to file failed");
        }
    }

    //method to read albums from albums.dat
    public static ArrayList<Album> readAlbums(){
        ArrayList<Album> albums = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream("data/data/com.example.androidphotos/data/albums.dat");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(fis));
            String albumInfo = null;
            albumInfo = br.readLine();
            if(albumInfo!= null) {
                String[] tokens = albumInfo.split("\\|");
                for (String s : tokens) {
                    albums.add(new Album(s, new ArrayList<Photo>()));
                }
            }
        } catch (IOException e) {}
        return albums;
    }

    public static void mainReadPhotos(Album currAlbum) throws IOException {
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
        ObjectInputStream ois;
        try{
            ois = new ObjectInputStream(new FileInputStream("data/data/com.example.androidphotos/data/" + currAlbum.getAlbumName() + "/photo.dat"));
        } catch(EOFException e) {
            return;
        }
        //read the .dat file and populate the observable list (list of albums)
        while(true) {
            try {
                String temp1 = (String) ois.readObject();
                //find substrings of caption, tags, datetime, photoPath
                int delimeter1 = temp1.indexOf("|");
                //getting the captions
                String caption = temp1.substring(0, delimeter1);
                int delimeter2 = temp1.lastIndexOf("|");
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
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    //method to read photos from file
    public static ArrayList<Photo> readPhotos(Album currAlbum) throws IOException, ClassNotFoundException {
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
                int delimeter1 = temp1.indexOf("|");
                //getting the captions
                String caption = temp1.substring(0, delimeter1);
                int delimeter2 = temp1.lastIndexOf("|");
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
                return photos;
            }

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
