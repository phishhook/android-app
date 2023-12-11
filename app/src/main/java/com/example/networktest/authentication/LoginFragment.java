package com.example.networktest.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networktest.LinkHistoryActivity;
import com.example.networktest.MainActivity;
import com.example.networktest.R;
import com.example.networktest.fragments.LinkHistoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private TextInputEditText editTextPhoneNumber;
    private TextInputEditText editUsername;
    private Button buttonLogin;
    private TextView signUpText;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        buttonLogin = view.findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editTextPhoneNumber.getText().toString();
                if (isValidPhoneNumber(phoneNumber)) {
                    checkForUser(phoneNumber, new UserCheckCallback() {
                        @Override
                        public void onUserChecked(boolean exists, String username) {
                            // Run the code on the UI thread
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (exists) {
                                        Log.d("User Check", "User exists with username: " + username);
                                        storeLoginDate();
                                        Log.d("User", "User login date stored.");
                                        openHomeScreen(); // Open Home Screen on successful login
                                        Toast.makeText(getActivity(), "Welcome back, " + username, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d("User Check", "User does not exist.");
                                        Toast.makeText(getActivity(), "Credentials do not exist, please sign up.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    // Show error to the user
                    editTextPhoneNumber.setError("Invalid phone number");
                }
            }
        });

        signUpText = view.findViewById(R.id.sign_up_text);
        String fullText = "Need an account? Sign Up Here";
        SpannableString spannableString = new SpannableString(fullText);
        // Set the color for the "Sign Up Here" part
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.secondary));
        int start = fullText.indexOf("Sign Up Here");
        int end = start + "Sign Up Here".length();
        spannableString.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the SpannableString back to the TextView
        signUpText.setText(spannableString);

        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterScreen();
            }
        });

        return view;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        // Add logic to validate the phone number here
        // For example, check if it's not empty and has 10 digits (modify as needed for your requirements)
        return phoneNumber != null && !phoneNumber.isEmpty() && phoneNumber.matches("\\d{10}");
    }

    private void openRegisterScreen() {
        RegisterFragment registerFragment = new RegisterFragment();

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, registerFragment, "RegisterFragment")
                .addToBackStack(null)
                .commit();
    }

    private void openHomeScreen() {
        LinkHistoryFragment linkHistoryFragment = new LinkHistoryFragment();

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, linkHistoryFragment, "LinkHistoryFragment")
                .addToBackStack(null)
                .commit();

        ((MainActivity) getActivity()).setSelectedBottomNavigationItem(R.id.navigation_link_history);
    }

    public interface UserCheckCallback {
        void onUserChecked(boolean exists, String username);
    }

    private void checkForUser(String phoneNumber, UserCheckCallback callback) {
        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Check if phoneNumber starts with 1, if not, prepend it for the USA code.
        if (!phoneNumber.startsWith("1")) {
            phoneNumber = "1" + phoneNumber;
        }

        // Add the phone number as a query parameter to the URL
        String baseUrl = "http://ec2-18-224-251-242.us-east-2.compute.amazonaws.com:8080/users/";
        String apiUrl = baseUrl + phoneNumber;

        // Build the request with the required header
        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("X-API-Key", "phishhookRyJHCenIz97Q5LIDPmHhDyg9eddxaBO29omDuzM1D5BsDRKH5mo3j8pmBehoO2Roj0Z4zWuDHlNW4AJVrSnLZF6lUravmyje13YB1LBriXHxYlxLUDYeXmV")
                .build();

        // Execute the request in an AsyncTask or background thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("API Error", "Call failed", e);
                // Invoke the callback with false since the call failed
                callback.onUserChecked(false, null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("API Success", "Response received successfully");
                    // Parse the responseData to JSON
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String username = jsonObject.optString("username", null);
                        if (username != null && !username.isEmpty()) {
                            storeUserId(jsonObject.optInt("id", -1));
                            // Invoke the callback with true and the username
                            callback.onUserChecked(true, username);
                        } else {
                            // Username not found, invoke the callback with false
                            callback.onUserChecked(false, null);
                        }
                    } catch (JSONException e) {
                        Log.e("API Error", "Failed to parse JSON", e);
                        callback.onUserChecked(false, null);
                    }
                } else {
                    Log.e("API Error", "Response received but request not successful. Response code: " + response.code());
                    // Invoke the callback with false since the request was not successful
                    callback.onUserChecked(false, null);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setBottomNavigationVisibility(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).setBottomNavigationVisibility(true);
    }

    private void storeLoginDate() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("lastLoginDate", System.currentTimeMillis());
        editor.apply();
    }

    private void storeUserId(int user_id) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("userId", user_id);
        editor.apply();
    }

}