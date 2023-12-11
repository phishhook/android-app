package com.example.networktest;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.content.SharedPreferences;
import android.content.Context;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class LinkAnalysisActivity extends Activity {

    private String MLApiURL = "http://ec2-34-226-215-44.compute-1.amazonaws.com/?url=";
    RequestQueue queue;

    private LottieAnimationView animationView;

    String urlToAnalyze;

    // This is the URL that is initially clicked on
    // and not the final url that link analysis returns
    String rawUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Volley request queue
        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.link_analysis);

        animationView = findViewById(R.id.animationView);
        // Example URL for link analysis
        urlToAnalyze = getIntent().getStringExtra("url");
        rawUrl = urlToAnalyze;

        //linkAnalysis(rawUrl);
        checkIfLinkInDatabase(rawUrl);

        // Perform link analysis

    }

    protected void linkAnalysis(String url) {
        StringRequest jsonMLRequest = new StringRequest(Request.Method.POST, MLApiURL + url, MLDataListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // TODO: store key in env file
                params.put("X-API-KEY", "phishhookRyJHCenIz97Q5LIDPmHhDyg9eddxaBO29omDuzM1D5BsDRKH5mo3j8pmBehoO2Roj0Z4zWuDHlNW4AJVrSnLZF6lUravmyje13YB1LBriXHxYlxLUDYeXmV");
                return params;

            }
        };
        // ML model can take around 10 seconds to process a URL; timeout set to 15 seconds just to be safe
        jsonMLRequest.setRetryPolicy(new DefaultRetryPolicy(
                22000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonMLRequest);
    }

    private int fetchUserId() {
        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        return sharedPref.getInt("userId", -1);
    }

    protected void checkIfLinkInDatabase(String urlToAnalyze) {
        String linkApiUrl = "http://ec2-18-224-251-242.us-east-2.compute.amazonaws.com:8080/links/analyze?url=" + urlToAnalyze;
        StringRequest jsonLinkRequest = new StringRequest(Request.Method.GET, linkApiUrl, existingLinkDataListener, existingLinkErrorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("X-API-KEY", "phishhookRyJHCenIz97Q5LIDPmHhDyg9eddxaBO29omDuzM1D5BsDRKH5mo3j8pmBehoO2Roj0Z4zWuDHlNW4AJVrSnLZF6lUravmyje13YB1LBriXHxYlxLUDYeXmV");
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        // Timeout set to 15 seconds just to be safe
        jsonLinkRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonLinkRequest);
    }

    protected void writeResultToDatabase(String result) {

        String LinkApiURL = "http://ec2-18-224-251-242.us-east-2.compute.amazonaws.com:8080/link";
        StringRequest jsonLinkRequest = new StringRequest(Request.Method.POST, LinkApiURL, linkDataListener, writeToDatabaseErrorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-API-KEY", "phishhookRyJHCenIz97Q5LIDPmHhDyg9eddxaBO29omDuzM1D5BsDRKH5mo3j8pmBehoO2Roj0Z4zWuDHlNW4AJVrSnLZF6lUravmyje13YB1LBriXHxYlxLUDYeXmV");
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject jsonBody = new JSONObject();
                String is_phishing;

                System.out.println("What is our result ?" + result);

                if (result.equals("N/A")) {
                    is_phishing = "indeterminate";
                    System.out.println("invalid reeult?");
                }
                else {
                    float percent = Float.parseFloat(result);
                    is_phishing = percent >= 50 ? "safe" : "phishing";
                    System.out.println("valid reeult?");
                }

                String urlWithoutProtocol = "";
                int userId = fetchUserId();
                try {
                    // Parse the URL
                    URL url = new URL(rawUrl);

                    urlWithoutProtocol = rawUrl.replace(url.getProtocol() + "://", "");


                    Log.d("Host part: ", urlWithoutProtocol);
                } catch (MalformedURLException e) {
                    // Handle MalformedURLException
                    e.printStackTrace();
                }

                Log.d("User ID!!!",  String.valueOf(userId));
                try {
                    jsonBody.put("user_id", userId);
                    jsonBody.put("url", rawUrl);
                    jsonBody.put("is_phishing", is_phishing);
                    jsonBody.put("percentage", result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    return jsonBody.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public String getBodyContentType() {
                 return "application/json; charset=utf-8";
            }
        };

        // Timeout set to 15 seconds just to be safe
        jsonLinkRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonLinkRequest);
    }

    Response.Listener<String> MLDataListener = new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {
            // Set the result with data before finishing
            String[] responseArr = response.split(",");
            String percent = responseArr[0].trim().replace("%", "");
            urlToAnalyze = responseArr[1].trim();
            writeResultToDatabase(percent);
            Intent resultIntent = new Intent();
            String newResponse = responseArr[0]  + "," + rawUrl;
            resultIntent.putExtra("resultData", newResponse);
            setResult(Activity.RESULT_OK, resultIntent);

            Log.d("Percent!!: ", percent);

            animationView.setVisibility(View.GONE);
            // Finish the activity
            finish();
        }

    };

    Response.Listener<String> linkDataListener = new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {

            Log.d("linkDataListener??", response);
        }
    };

    Response.Listener<String> existingLinkDataListener = new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {

            Log.d("exists??", response.toString());

            // Parse the JSON string
            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(response);
                String clicked_at =  jsonObject.getString("clicked_at");

                // Convert the clicked_at string to LocalDateTime
                LocalDateTime clickedAtDateTime = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    clickedAtDateTime = LocalDateTime.parse(clicked_at);
                    // If link was last clicked longer than one week
                    // prior to today, perform link analysis still
                    if (!isClickedWithinOneWeek(clickedAtDateTime)) {
                        linkAnalysis(urlToAnalyze);
                    }
                }
                else {
                    String percentage = jsonObject.getString("percentage");
                    System.out.println("Retrieved percentage: " + percentage);
                    String responseForIntent = percentage + ", " + urlToAnalyze;
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("resultData", responseForIntent);
                    setResult(Activity.RESULT_OK, resultIntent);
                    animationView.setVisibility(View.GONE);
                    // Finish the activity
                    finish();
                }


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // Extract the percentage value


        }
    };

    private static boolean isClickedWithinOneWeek(LocalDateTime clickedAtDateTime) {
        LocalDateTime oneWeekAgo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return clickedAtDateTime.isAfter(oneWeekAgo);
        }
        return false;
    }


    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            // Extract relevant information from the error
            int statusCode = -1; // Default value
            Log.d("Error in link analysis!!: ", error.toString());

            if (error.networkResponse != null) {
                statusCode = error.networkResponse.statusCode;
            }

            // Pass relevant information to the method
            handleErrorResponse(statusCode);
        }
    };

    Response.ErrorListener writeToDatabaseErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            // Extract relevant information from the error
            int statusCode = -1; // Default value
            Log.d("Error in database write!!: ", error.toString());

            if (error.networkResponse != null) {
                statusCode = error.networkResponse.statusCode;
            }


        }
    };

    Response.ErrorListener existingLinkErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            // Extract relevant information from the error
            int statusCode = -1; // Default value
            Log.d("Link is not in database?!!: ", error.toString());
            linkAnalysis(urlToAnalyze);

            if (error.networkResponse != null) {
                statusCode = error.networkResponse.statusCode;
            }

            // Pass relevant information to the method
        }
    };

// ...

    // Method to handle the error response
    private void handleErrorResponse(int statusCode) {
        // Use the status code to determine how to handle the error
        String response = "N/A, " + urlToAnalyze;
        writeResultToDatabase("N/A");
        Intent resultIntent = new Intent();
        resultIntent.putExtra("resultData", response);
        setResult(Activity.RESULT_OK, resultIntent);
        // Finish the activity or perform other actions as needed
        finish();
    }



}
