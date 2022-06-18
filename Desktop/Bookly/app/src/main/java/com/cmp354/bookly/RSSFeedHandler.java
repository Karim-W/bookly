package com.cmp354.bookly;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RSSFeedHandler extends DefaultHandler {
    RSSFeed feed;
    RSSItem item;
    
    boolean feedTitleHasBeenRead = false;
    boolean feedLinkHasBeenRead = false;
    boolean feedDescriptionHasBeenRead = false;
    boolean feedPubDateHasBeenRead = false;
    boolean feedCategoryHasBeenRead = false;

    boolean isTitle = false;
    boolean isDescription = false;
    boolean isLink = false;
    boolean isPubDate = false;
    boolean isCategory = false;
    boolean isCost = false;

    RSSFeedHandler() {}
    
    public RSSFeed getFeed() {
        return feed;
    }
        
    public void startDocument() throws SAXException {
        feed = new RSSFeed();
        item = new RSSItem(); // an item to temporarily store feed data
    }
    
    public void endDocument() throws SAXException { }
    
    public void startElement(String namespaceURI, String localName, 
            String qName, Attributes atts) throws SAXException {
        if (qName.equals("item")) {
            // create a new item
            item = new RSSItem();
            return;
        }
        else if (qName.equals("title")) {
            isTitle = true;
            return;
        }
        else if (qName.equals("description")) {
            isDescription = true;
            return;
        }
        else if (qName.equals("link")) {
            isLink = true;
            return;
        }
        else if (qName.equals("enclosure")) {
            item.setImageURL(atts.getValue("url"));
            return;
        }
        else if(qName.equals("cost")){
            isCost = true;
            return;
        }
        else if(qName.equals("category")){
            isCategory = true;
            return;
        }
        else if(qName.equals("pubDate")){
            isPubDate = true;
            return;
        }
    }
    
    public void endElement(String namespaceURI, String localName, 
            String qName) throws SAXException
    {
        if (qName.equals("item")) {
            feed.addItem(item);
            if(feed.getItemCount() > 10){
                throw new SAXException();
            }
            return;
        }
    }
     
    public void characters(char ch[], int start, int length)
    {
        String s = new String(ch, start, length);
        //Log.e("RSSHandler", "s: " + s);

        if(feed.getItemCount() > 10){
            return;
        }

        if (isTitle) {
            if (feedTitleHasBeenRead == false) {
                feedTitleHasBeenRead = true;
            } 
            else {
                item.setTitle(s);
            }
            isTitle = false;
        }
        else if (isLink) {
            if (feedLinkHasBeenRead == false) {
                feedLinkHasBeenRead = true;
            }
            else {
                item.setLink(s);
            }
            isLink = false;
        }
        else if (isDescription) {
            if (feedDescriptionHasBeenRead == false) {
                feedDescriptionHasBeenRead = true;
            }else {
                item.setDescription(s);
            }
            isDescription = false;
        }
        else if (isCategory) {
            if (feedCategoryHasBeenRead == false) {
                feedCategoryHasBeenRead = true;
            }else {
                item.setCategory(s);
            }
            isCategory = false;
        }
        else if (isPubDate) {
            if (feedPubDateHasBeenRead == false) {
                feed.setPubDate(s);
                feedPubDateHasBeenRead = true;
            }else {
                item.setPubDate(s);
            }
            isPubDate = false;
        }
        else if(isCost){
            item.setCost(s);
            isCost = false;
        }

    }
}