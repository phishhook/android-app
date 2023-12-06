package com.example.networktest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkClickReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the URL from the intent
        Uri data = intent.getData();
        if (data != null) {
            // Handle the URL as needed (e.g., open the appropriate activity)
            launch_analysis(data, context);
        }
    }


    public void launch_analysis(@NonNull Uri my_uri, Context context){
        String url = my_uri.toString();
        String extractedUrl = extractUrl(url);
        Uri uri = Uri.parse(extractedUrl);
        Log.d("Received link: ", uri.toString());
        // Launch NotificationSystemActivity
        Intent notificationIntent = new Intent(context, NotificationSystemActivity.class);
        notificationIntent.setData(uri);
        context.startActivity(notificationIntent);
    }

    private static String extractUrl(String inputUrl) {
        String patternString = "q=([^&]+)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(inputUrl);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return inputUrl;
        }
    }
}
