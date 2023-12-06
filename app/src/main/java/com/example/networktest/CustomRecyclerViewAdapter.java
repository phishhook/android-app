package com.example.networktest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

    // ViewHolder class represents the UI components (i.e., Views) of a single list item in the RecyclerView.
    class ViewHolder extends RecyclerView.ViewHolder {
        // TextView to display the url.
        private TextView url_view;
        // TextView to display the time at which the url was clicked.
        private TextView clicktime_view;
        // TextView to display if the link is safe or not
        private TextView verdict_view;

        // Initializing the TextViews.
        ViewHolder(View itemView) {
            super(itemView);
            url_view = itemView.findViewById(R.id.url);
            clicktime_view = itemView.findViewById(R.id.clicked_at);
            verdict_view = itemView.findViewById(R.id.is_phishing);

        }
    }

    // List of data to be displayed in the RecyclerView.
    private List<itemData> list;
    // Presumably declared elsewhere in the Adapter class.
    private Context context;

    public CustomRecyclerViewAdapter(List<itemData> list, Context context) {
        this.list = list;
        this.context = context;
    }


    // This method is called when a new ViewHolder gets created.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the single_unit layout and initializing a new ViewHolder instance.
        View view = LayoutInflater.from(context).inflate(R.layout.single_unit_history, parent, false);
        return new ViewHolder(view);
    }


    // This method binds the data to the ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.url_view.setText(list.get(position).url);
        holder.clicktime_view.setText(list.get(position).clicktime);
        holder.verdict_view.setText(list.get(position).verdict);
    }

    // Setting the name and value in the TextViews from the itemData instance at this position.

    // This method returns the size of the data set.
    @Override
    public int getItemCount() {
        return list.size();
    }


}