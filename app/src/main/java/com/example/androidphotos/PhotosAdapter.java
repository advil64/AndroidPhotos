package com.example.androidphotos;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class PhotosAdapter extends ArrayAdapter<Photo> {
    List<Photo> myPhotos;
    Context context;
    public PhotosAdapter(Context context, int resourceId, List<Photo> items) {
        super(context, resourceId, items);
        this.myPhotos=items;
        this.context=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Photo myPhoto = myPhotos.get(position);
        if(convertView == null) {
            LayoutInflater listRow = LayoutInflater.from(context);
            convertView = listRow.inflate(R.layout.image_list_item, null);
            TextView tittle = convertView.findViewById(R.id.title);
            ImageView myImage = convertView.findViewById(R.id.upImage);
            tittle.setText(myPhoto.getCaption());
            myImage.setImageURI(myPhoto.getPhotoPath());
        }
        return convertView;
    }
}