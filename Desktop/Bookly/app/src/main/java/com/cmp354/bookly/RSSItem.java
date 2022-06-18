package com.cmp354.bookly;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class RSSItem {
    
    private String title = null;
    private String description = null;
    private String link = null;
    private String imageURL = null;
    private String category = null;
    private String pubDate = null;
    private String cost = null;

    private SimpleDateFormat dateOutFormat =
            new SimpleDateFormat("yyyy");

    private SimpleDateFormat dateInFormat =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

    public RSSItem() {}
    
    public void setTitle(String title)     {
        this.title = title;
    }
    
    public String getTitle()     {
        return title;
    }
    
    public void setDescription(String description)     {

        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getLink() {
        return link;
    }

    
    public String toString() {
        return title;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPubDateFormatted() {
        try {
            if (pubDate != null) {
                Date date = dateInFormat.parse(pubDate.trim());
                String pubDateFormatted = dateOutFormat.format(date);
                return pubDateFormatted;
            }
            else
                return "No date found!";
        }
        catch (ParseException e) {
            //throw new RuntimeException(e);
            Log.e ("newsreader",e.toString());
            return "Date format not supported!";
        }
    }
}