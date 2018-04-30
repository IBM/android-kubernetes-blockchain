package com.amanse.anthony.fitcoinandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ArticleModel {
    int page;
    String title;
    String subtitle;
    String imageEncoded;
    String subtext;
    String description;
    String link;

    public ArticleModel(int page, String title, String subtitle, String imageEncoded, String subtext, String description, String link) {
        this.page = page;
        this.title = title;
        this.subtitle = subtitle;
        this.imageEncoded = imageEncoded;
        this.subtext = subtext;
        this.description = description;
        this.link = link;
    }

    public Bitmap getBitmap() {
        if (imageEncoded == null || imageEncoded.equals("")) {
            return null;
        }
        byte[] decodedString = Base64.decode(imageEncoded, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public int getPage() {
        return page;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImageEncoded() {
        return imageEncoded;
    }

    public String getSubtext() {
        return subtext;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }
}
