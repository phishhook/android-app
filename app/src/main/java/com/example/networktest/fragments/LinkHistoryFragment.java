package com.example.networktest.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.networktest.CustomRecyclerViewAdapter;
import com.example.networktest.MainActivity;
import com.example.networktest.R;
import com.example.networktest.itemData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LinkHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LinkHistoryFragment extends Fragment {

    private List<itemData> mDataList;
    private RecyclerView mRecyclerView;
    private CustomRecyclerViewAdapter mAdapter;
    private RequestQueue queue;
    private JSONArray links_a;
    private JSONObject links;
    private SwipeRefreshLayout swipeRefreshLayout;

    public LinkHistoryFragment() {
        // Required empty public constructor
    }

    public static LinkHistoryFragment newInstance(String param1, String param2) {
        LinkHistoryFragment fragment = new LinkHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity parent = (MainActivity) getActivity();
        Uri my_uri = parent.getIntent().getData();
        if (my_uri != null){
            parent.launch_analysis(my_uri);
        }

        String LinkApiURL = "http://ec2-18-224-251-242.us-east-2.compute.amazonaws.com:8080/links";
        JsonArrayRequest jsonLinkRequest = new JsonArrayRequest(Request.Method.GET, LinkApiURL, links_a, linkDataListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("X-API-KEY", "phishhookRyJHCenIz97Q5LIDPmHhDyg9eddxaBO29omDuzM1D5BsDRKH5mo3j8pmBehoO2Roj0Z4zWuDHlNW4AJVrSnLZF6lUravmyje13YB1LBriXHxYlxLUDYeXmV");
                return params;
            }
        };
        queue.add(jsonLinkRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.link_history_fragment, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDataList = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recyclerView);

        this.queue = Volley.newRequestQueue(getContext());
        String LinkApiURL = "http://ec2-18-224-251-242.us-east-2.compute.amazonaws.com:8080/links";
        JsonArrayRequest jsonLinkRequest = new JsonArrayRequest(Request.Method.GET, LinkApiURL, links_a, linkDataListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("X-API-KEY", "phishhookRyJHCenIz97Q5LIDPmHhDyg9eddxaBO29omDuzM1D5BsDRKH5mo3j8pmBehoO2Roj0Z4zWuDHlNW4AJVrSnLZF6lUravmyje13YB1LBriXHxYlxLUDYeXmV");
                return params;
            }
        };
        queue.add(jsonLinkRequest);

        initSwipeRefreshLayout();
    }

    protected void updateRecyclerView(){
        mAdapter = new CustomRecyclerViewAdapter(mDataList, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("DATA LIST CONTENTS", mDataList.size() + "");
    }

    Response.Listener<JSONArray> linkDataListener = new Response.Listener<JSONArray>() {

        @Override
        public void onResponse(JSONArray response) {

            try {
                Log.d("JSON OUT", response.toString(1));
                int user_id = getUserId();
                mDataList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    JSONObject link = response.getJSONObject(i);
                    // TODO: use the actual user's ID instead of just 1

                    if (link.getInt("user_id") == user_id){
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

    private int getUserId() {
        if (getActivity() != null) {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
            return sharedPref.getInt("userId", 0);
        } else {
            return 0;
        }
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Call the method to refresh data
                refreshData();
            }
        });
    }

    private void refreshData() {
        updateRecyclerView();
        swipeRefreshLayout.setRefreshing(false); // This will hide the spinner
    }
}