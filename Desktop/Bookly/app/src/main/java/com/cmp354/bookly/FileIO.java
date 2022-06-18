package com.cmp354.bookly;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class FileIO {
    
    private final String URL_STRING = "https://rewardtheworld.com/rss/toptitles/ebooks/all";
    private final String FILENAME = "book_feed.xml";
    private Context context = null;
    
    public FileIO(Context context) {
        this.context = context;
    }
    
    public void downloadFile() {
        // get NetworkInfo object
        ConnectivityManager cm = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        Network networkInfo = cm.getActiveNetwork();
        
        // if network is connected, download feed
        if (networkInfo != null) {
            
            try{
                // get the URL
                URL url = new URL(URL_STRING);
    
                // get the input stream
                InputStream in = url.openStream();
                
                // get the output stream
                FileOutputStream out = 
                    context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
    
                // read input and write output
                byte[] buffer = new byte[1024];
                int bytesRead = in.read(buffer);
                while (bytesRead != -1)
                {
                    out.write(buffer, 0, bytesRead);
                    bytesRead = in.read(buffer);
                }
                out.close();
                in.close();
            } 
            catch (IOException e) {
                Log.e("News reader", e.toString());
            }
        }
        else
            Toast.makeText(context, "Please connect to the Internet/Data", Toast.LENGTH_SHORT).show();
    }
    
    public RSSFeed readFile() {

        RSSFeed feed;
        RSSFeedHandler theRssHandler = new RSSFeedHandler();
        try {
            // get the XML reader
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();

            // set content handler
            xmlreader.setContentHandler(theRssHandler);

            // read the file from internal storage
            FileInputStream in = context.openFileInput(FILENAME);

            // parse the data
            InputSource is = new InputSource(in);
            xmlreader.parse(is);

            // set the feed in the activity
            feed = theRssHandler.getFeed();
            return feed;
        }
        catch (SAXException E){
            feed = theRssHandler.getFeed();
            return feed;
        }
        catch (Exception e) {
            Log.e("News reader", e.toString());
            return null;
        }
    }
}