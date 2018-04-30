package com.amanse.anthony.fitcoinandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;

public class UserInfoModel {
    int steps;
    String name;
    String png;

    public UserInfoModel(int steps, String name, String image) {
        this.steps = steps;
        this.name = name;
        this.png = image;
    }

    public String getName() {
        return name;
    }

    public int getSteps() {
        return steps;
    }

    public String getPng() {
        return png;
    }

    public Bitmap getBitmap() {
        if (png == null || png.equals("")) {
            return null;
        }
        byte[] decodedString = Base64.decode(png, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
