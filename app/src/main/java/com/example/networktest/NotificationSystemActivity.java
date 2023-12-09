package com.example.networktest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.networktest.fragments.LinkHistoryFragment;

public class NotificationSystemActivity extends AppCompatActivity {

    private TextView resultTextView;

    private TextView urlView;

    private Button linkHistoryBtn;

    private Button actionButton;

    private ImageView resultImageView;

    private TextView continueAnywayTextView;

    private RequestQueue queue;

    private String url;

    float percent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_system);
        resultImageView = findViewById(R.id.resultImageView);
        resultTextView= findViewById(R.id.resultTextView);
        urlView = findViewById(R.id.linkResultTextView);
        linkHistoryBtn = findViewById(R.id.linkHistoryBtn);
        actionButton = findViewById(R.id.actionButton);
        continueAnywayTextView = findViewById(R.id.continueAnyway);

        // Create a SpannableString with underline
        SpannableString content = new SpannableString("Proceed to Link Anyway...");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        continueAnywayTextView.setText(content);

        continueAnywayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event
                openLinkInBrowser();
            }
        });

        linkHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkHistory();
            }
        });

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(percent >= 50){
                    openLinkInBrowser();
                }
                else {
                    openEmailClient();
                }
            }
        });

        this.queue = Volley.newRequestQueue(this);


        // Receive the URL from the intent
        url = getIntent().getDataString();

        continueAnywayTextView.setVisibility(View.GONE);

        // Simulate link analysis (replace with your actual implementation)
        Intent linkAnalysisIntent = new Intent(this, LinkAnalysisActivity.class);
        linkAnalysisIntent.putExtra("url", url);
        startActivityForResult(linkAnalysisIntent, 1);
    }

    private void openLinkInBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        browserIntent.setPackage("com.android.chrome");
        startActivity(browserIntent);
    }
    private void openEmailClient() {
        try {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        } catch (android.content.ActivityNotFoundException e) {
            System.out.println("Error opening email client");
        }
    }

    private void openLinkHistory() {
        Intent intent = new Intent(NotificationSystemActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // Link analysis succeeded
                // Retrieve the result data
                String resultData = data.getStringExtra("resultData");
                Log.d("Received resultData: ", resultData);
                String[] responseArr = resultData.split(",");
                String percent = responseArr[0];
                url = responseArr[1].trim();
                displayResult(percent);
                // Handle the result data accordingly
                // You can now use the 'resultData' in NotificationSystemActivity
            } else {
                // Link analysis failed or was canceled
                // Handle the result accordingly
                displayResult("N/A");
            }
        }
    }

    // Update this method in NotificationSystemActivity
    private void displayResult(String result) {
        resultImageView.setVisibility(View.VISIBLE);
        resultTextView.setVisibility(View.VISIBLE);
        urlView.setVisibility(View.VISIBLE);
        urlView.setText(url);


        // Customize the logic based on your result values
        // Customize the logic based on your result values
        if (!result.equals("N/A")) {
            // Parse the result as a percentage
            percent = Float.parseFloat(result.replace("%", ""));

            // Display logic based on the percentage
            if (percent > 50) {
                // Display a message and image for a safe link
                resultTextView.setText("We have determined this link is " + result + " safe.");
                resultImageView.setImageResource(R.drawable.legitimate);
                actionButton.setText("Continue to Default Browser");
            } else {
                // Display a message and image for an unsafe link
                resultTextView.setText("We have determined this link is only " + result + " safe. "+
                        "We recommend not visiting this webpage");
                setResultTextMargin(25);  // Set the desired margin
                resultImageView.setImageResource(R.drawable.phishing);
                actionButton.setText("Back to Email Client");
                continueAnywayTextView.setVisibility(View.VISIBLE);
            }
        } else {
            // Display a message and image for an undetermined link
            resultTextView.setText("We were not able to determine the safety of this link. Please " +
                    "proceed with caution");
            percent = -1;
            setResultTextMargin(25);  // Set the desired margin
            actionButton.setText("Back to Email Client");
            resultImageView.setImageResource(R.drawable.question);
            continueAnywayTextView.setVisibility(View.VISIBLE);
        }
    }

    private int dpToPixels(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void setResultTextMargin(int margin) {
        // Get the layout params of the "Outcome" text
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) resultTextView.getLayoutParams();

        // Set the top margin
        layoutParams.topMargin = dpToPixels(margin);

        // Apply the updated layout params
        resultTextView.setLayoutParams(layoutParams);
    }


}
