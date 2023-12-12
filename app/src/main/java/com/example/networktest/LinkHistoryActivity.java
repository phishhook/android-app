package com.example.networktest;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkHistoryActivity extends AppCompatActivity {
    //Main Create Function

    private List<itemData> mDataList;

    private RecyclerView mRecyclerView;

    private CustomRecyclerViewAdapter mAdapter;

    private RequestQueue queue;

    private JSONArray links_a;

    private JSONObject links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_history);

        mDataList = new ArrayList<itemData>();
        mRecyclerView = findViewById(R.id.recyclerView);


        this.queue = Volley.newRequestQueue(this);
        String LinkApiURL = "http://ec2-18-224-251-242.us-east-2.compute.amazonaws.com:8080/links";
        JsonArrayRequest jsonLinkRequest = new JsonArrayRequest(Request.Method.GET, LinkApiURL, links_a, linkDataListener, errorListener){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // TODO: store key in env file
                params.put("X-API-KEY", "phishhookRyJHCenIz97Q5LIDPmHhDyg9eddxaBO29omDuzM1D5BsDRKH5mo3j8pmBehoO2Roj0Z4zWuDHlNW4AJVrSnLZF6lUravmyje13YB1LBriXHxYlxLUDYeXmV");
                return params;
            }
        };
        queue.add(jsonLinkRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MyApp", "Fragment/Activity Resumed: " + getClass().getSimpleName());
    }

    protected void updateRecyclerView(){
        mAdapter = new CustomRecyclerViewAdapter(mDataList, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("DATA LIST CONTENTS", mDataList.size() + "");
    }

    Response.Listener<JSONArray> linkDataListener = new Response.Listener<JSONArray>() {

        @Override
        public void onResponse(JSONArray response) {

            try {
                Log.d("JSON OUT", response.toString(1));

                for (int i = 0; i < response.length(); i++) {
                    JSONObject link = response.getJSONObject(i);
                    // TODO: use the actual user's ID instead of just 1
                    if (link.getInt("user_id") == 1){
                        String originalDateTime = link.getString("clicked_at");
                        String formattedDateTime = formatDateTime(originalDateTime);

                        itemData item = new itemData(link.getString("url"), formattedDateTime, link.getString("is_phishing"));
                        mDataList.add(item);
                    }
                }
                updateRecyclerView();

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Log.d("RESPONSE DONE", "done");
        }
    };

    Response.ErrorListener errorListener  = new Response.ErrorListener () {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("JSON ERROR", error.toString());
        }
    };

    private String formatDateTime(String dateTime) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
            Date date = originalFormat.parse(dateTime);
            return targetFormat.format(date);
        } catch (ParseException e) {
            Log.e("DateFormatError", "Error in parsing date", e);
            return dateTime; // Return the original date if parsing fails
        }
    }
}

