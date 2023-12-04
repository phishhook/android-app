package com.example.networktest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.networktest.MainActivity;
import com.example.networktest.R;
import com.example.networktest.authentication.LoginFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_profile_fragment, container, false);

        // Assuming you have a log out button in your layout
        Button logOutButton = view.findViewById(R.id.logOutBtn);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear authentication data
                clearAuthenticationData();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
            }
        });

        return view;
    }

    // Method to clear authentication data (i.e., logout)
    private void clearAuthenticationData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Reset lastLoginDate to 0 so you must reauthorize
        editor.putLong("lastLoginDate", 0);
        editor.apply();
    }
}