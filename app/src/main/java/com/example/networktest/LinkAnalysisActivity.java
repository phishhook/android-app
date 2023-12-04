package com.example.networktest;

import android.app.Activity;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.Map;

public class LinkAnalysisActivity extends Activity {

    private String MLApiURL = "http://ec2-34-226-215-44.compute-1.amazonaws.com/?url=";
    private RequestQueue queue;

    private LottieAnimationView animationView;

    String urlToAnalyze;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Volley request queue
        queue = Volley.newRequestQueue(this);
        setContentView(R.layout.link_analysis);

        animationView = findViewById(R.id.animationView);
        // Example URL for link analysis
        urlToAnalyze = getIntent().getStringExtra("url");

        Log.d("Link analysis!!: ", urlToAnalyze);

        // Perform link analysis
        linkAnalysis(urlToAnalyze);
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
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonMLRequest);
    }

    private int fetchUserId() {
        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        return sharedPref.getInt("userId", -1);
    }

    protected void writeResultToDatabase(String result) {
        String LinkApiURL = "http://ec2-18-224-251-242.us-east-2.compute.amazonaws.com:8080/link";
        StringRequest jsonLinkRequest = new StringRequest(Request.Method.POST, LinkApiURL, linkDataListener, errorListener) {
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

                if (!result.equals("N/A")) {
                    float percent = Float.parseFloat(result.replace("%", ""));
                    is_phishing = percent >= 50 ? "safe" : "phishing";
                }
                else {
                    is_phishing = "indeterminate";
                }


                int userId = fetchUserId();
                Log.d("User ID!!!",  String.valueOf(userId));
                try {
                    jsonBody.put("user_id", userId);
                    jsonBody.put("url", urlToAnalyze);
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
            writeResultToDatabase(response);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("resultData", response);
            setResult(Activity.RESULT_OK, resultIntent);

            Log.d("Response!!: ", response);

            animationView.setVisibility(View.GONE);
            // Finish the activity
            finish();
        }

    };

    Response.Listener<String> linkDataListener = new Response.Listener<String>() {

        @Override
        public void onResponse(String response) {

            System.out.println(response.toString());
        }
    };


    Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            // Extract relevant information from the error
            int statusCode = -1; // Default value
            Log.d("Error!!: ", error.toString());

            if (error.networkResponse != null) {
                statusCode = error.networkResponse.statusCode;
            }

            // Pass relevant information to the method
            handleErrorResponse(statusCode);
        }
    };

// ...

    // Method to handle the error response
    private void handleErrorResponse(int statusCode) {
        // Use the status code to determine how to handle the error
        Intent resultIntent = new Intent();
        resultIntent.putExtra("resultData", "N/A");
        setResult(Activity.RESULT_OK, resultIntent);
        // Finish the activity or perform other actions as needed
        finish();
    }

}
