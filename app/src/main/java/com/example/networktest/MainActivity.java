package com.example.networktest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.networktest.authentication.LoginFragment;
import com.example.networktest.fragments.LinkHistoryFragment;
import com.example.networktest.fragments.UserProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String MLApiURL = " http://ec2-34-226-215-44.compute-1.amazonaws.com/?url=";

    private RequestQueue queue;

    protected int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.queue = Volley.newRequestQueue(this);
        setContentView(R.layout.activity_main);

        // Check if savedInstanceState is null to avoid recreating the fragment on orientation changes
        FragmentManager fragmentManager = getSupportFragmentManager();
        LoginFragment loginFragment = new LoginFragment();
        if (savedInstanceState == null) {
            if (needsReAuthorization()) {
                // Start with the Login Fragment, user can switch to Register Fragment if needed.
                fragmentManager.beginTransaction()
                        .add(R.id.fragment_container, loginFragment)
                        .commit();
            } else {
                BottomNavigationView navView = findViewById(R.id.navigation);
                navView.setOnItemSelectedListener(item -> {
                    Fragment selectedFragment = null;
                    int itemId = item.getItemId();
                    if (itemId == R.id.navigation_link_history) {
                        selectedFragment = new LinkHistoryFragment();
                    }
                    if (itemId== R.id.navigation_user_profile) {
                        selectedFragment = new UserProfileFragment();
                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    }
                    return true;
                });
            }
        }

        Uri my_uri= getIntent().getData();

        if (my_uri != null) {
            //launch_analysis(my_uri);
//            String url = my_uri.toString();
//            String extractedUrl = extractUrl(url);
//            Uri uri = Uri.parse(extractedUrl);
//            Log.d("Received link: ", uri.toString());
//            // Launch NotificationSystemActivity
//            Intent notificationIntent = new Intent(MainActivity.this, NotificationSystemActivity.class);
//            notificationIntent.setData(uri);
//            startActivity(notificationIntent);
        }
        //Intent historyIntent = new Intent(MainActivity.this, LinkHistoryFragment.class);
        //startActivity(historyIntent);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,LinkHistoryFragment.newInstance(null,null));
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void launch_analysis(@NonNull Uri my_uri){
        String url = my_uri.toString();
        String extractedUrl = extractUrl(url);
        Uri uri = Uri.parse(extractedUrl);
        Log.d("Received link: ", uri.toString());
        // Launch NotificationSystemActivity
        Intent notificationIntent = new Intent(MainActivity.this, NotificationSystemActivity.class);
        notificationIntent.setData(uri);
        startActivity(notificationIntent);
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

    @Override
    protected void onStart() {
        super.onStart();
        //System.out.println("TEST #######");
    }

    protected void linkAnalysis(String url) {
        StringRequest jsonMLRequest = new StringRequest (Request.Method.POST, MLApiURL + url, MLDataListener, errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // TODO: store key in env file
                params.put("X-API-KEY", "phishhookRyJHCenIz97Q5LIDPmHhDyg9eddxaBO29omDuzM1D5BsDRKH5mo3j8pmBehoO2Roj0Z4zWuDHlNW4AJVrSnLZF6lUravmyje13YB1LBriXHxYlxLUDYeXmV");
                return params;

            }
        };
        // ML model can take around 10 seconds to process a url; timeout set to 15 seconds just to be safe
        jsonMLRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonMLRequest);
    }

    Response.Listener<String> linkDataListener = new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {

            //System.out.println(response.toString());
        }
    };

    Response.Listener<String> MLDataListener = new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {

            //System.out.println(response.toString() + "&&&&&&&&&&&&&&&");
        }
    };

    Response.ErrorListener errorListener  = new Response.ErrorListener () {

        @Override
        public void onErrorResponse(VolleyError error) {
            //System.out.println(error + "#########");
        }
    };

    private boolean needsReAuthorization() {
        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        long lastLoginDate = sharedPref.getLong("lastLoginDate", 0);
        long currentTime = System.currentTimeMillis();
        long sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000; // 7 days in milliseconds

        return (currentTime - lastLoginDate) > sevenDaysInMillis;
    }

    // In your main activity
    public void setBottomNavigationVisibility(boolean visible) {
        BottomNavigationView navView = findViewById(R.id.navigation);
        if (navView != null) {
            navView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

}