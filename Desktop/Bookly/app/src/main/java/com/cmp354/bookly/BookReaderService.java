package com.cmp354.bookly;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class BookReaderService extends Service {

    private BookReaderApp app;
    private Timer timer;
    private FileIO io;
    private int count;
    
    @Override
    public void onCreate() {
        Log.d("News reader", "Service created");
        app = (BookReaderApp) getApplication();
        count = 0;
        io = new FileIO(getApplicationContext());
        startTimer();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("News reader", "Service started");
        return START_STICKY;
    }
    
    @Override 
    public IBinder onBind(Intent intent) {
        Log.d("News reader", "Service bound - not used!");
        return null;
    }
    
    @Override
    public void onDestroy() {
        Log.d("News reader", "Service destroyed");
        stopTimer();
    }
    
    private void startTimer() {
        TimerTask task = new TimerTask()
        {

            @Override
            public void run() {
                Log.d("News reader", "Timer task started");

                //always in the background - no need for async
                io.downloadFile();
                Log.d("News reader", "File downloaded");
                
                RSSFeed newFeed = io.readFile();
                Log.d("News reader", "File read");
                
                // if new feed is newer than old feed
                if (newFeed != null)
                {
                    if (true || newFeed.getPubDateMillis() > app.getFeedMillis())
                    {
                        Log.d("News reader", "Updated feed available.");

                        // update app object
                        app.setFeedMillis(newFeed.getPubDateMillis());

                        // display notification
                        sendNotification("Select to view updated feed.");
                    } else {
                        Log.d("News reader", "Updated feed NOT available.");
                    }
                }
                
            }
        };
        
        timer = new Timer(true);
        int delay = 1000;//1000 * 60 * 60;      // 1 hour
        int interval = 1000;//1000 * 60 * 60;   // 1 hour
        timer.schedule(task, delay, interval);
    }
    
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
	
private void sendNotification(String text)
    {

        // 1. create the intent for the notification
        // use the flag FLAG_ACTIVITY_CLEAR_TOP as is - this is for the running stack - clear activities above it
        Intent notificationIntent = new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // 2. create the pending intent
        // not caused by your app - it is caused by android outside the app
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, flags);

        // 3. create the variables for the notification
        int icon = R.drawable.ic_launcher_background;
        CharSequence tickerText = "News update available!";
        CharSequence contentTitle = getText(R.string.app_name);
        CharSequence contentText = text;

        /*
        //or display it with a timestamp
        Date date = new Date(System.currentTimeMillis());
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        String dateFormatted = formatter.format(date);
        CharSequence contentText = text + " @: " + dateFormatted;
        */

        NotificationChannel notificationChannel =
                new NotificationChannel("Channel_ID", "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);


        // create the notification and set its data
        Notification notification = new NotificationCompat
						.Builder(this, "Channel_ID")
                        .setSmallIcon(icon)
                        .setTicker(tickerText)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setChannelId("Channel_ID")
                        .build();

        final int NOTIFICATION_ID = 1;
        manager.notify(++count/*NOTIFICATION_ID*/, notification);
    }

}