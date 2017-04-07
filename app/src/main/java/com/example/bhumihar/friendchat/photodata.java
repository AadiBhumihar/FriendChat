package com.example.bhumihar.friendchat;

import android.graphics.Bitmap;

/**
 * Created by bhumihar on 30/3/17.
 */

public class photodata {

    String encoded_image ;

    public photodata(String encoded_image) {
        this.encoded_image = encoded_image;
    }

    public void setEncoded_image(String encoded_image) {
        this.encoded_image = encoded_image;
    }

    public String getEncoded_image() {
        return encoded_image;
    }
}
