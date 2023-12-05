package com.example.networktest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NotificationSystemActivity extends Activity {

    private TextView resultTextView;

    private TextView urlView;

    private Button actionButton;

    private ImageView resultImageView;

    private RequestQueue queue;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_system);
        resultImageView = findViewById(R.id.resultImageView);
        resultTextView= findViewById(R.id.resultTextView);
        urlView = findViewById(R.id.linkResultTextView);
        actionButton = findViewById(R.id.actionButton);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkInBrowser();  // Call the method to open the link in the browser
            }
        });

        this.queue = Volley.newRequestQueue(this);


        // Receive the URL from the intent
        url = getIntent().getDataString();

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
            float percent = Float.parseFloat(result.replace("%", ""));

            // Display logic based on the percentage
            if (percent > 50) {
                // Display a message and image for a safe link
                resultTextView.setText("We have determined this link is " + result + " safe.");
                resultImageView.setImageResource(R.drawable.legitimate);
            } else {
                // Display a message and image for an unsafe link
                resultTextView.setText("We have determined this link is only " + result + " safe. "+
                        "We recommend not visiting this webpage");
                setResultTextMargin(150);  // Set the desired margin
                resultImageView.setImageResource(R.drawable.phishing);
            }
        } else {
            // Display a message and image for an undetermined link
            resultTextView.setText("We were not able to determine the safety of this link. Please " +
                    "proceed with caution");
            setResultTextMargin(150);  // Set the desired margin

            resultImageView.setImageResource(R.drawable.question);
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
