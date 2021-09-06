package com.example.top10downloader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
//    we need to store the layout Resource that we'll be given in the constructor and the list that contains our data.
//    another thing we need to do is inflate the xml resource to create the View. inflating an xml resource is just the term used to describe taking the xml representation and producing the actual widgets from it.so xml in out list_record.xml file describes the TextView widgets and their constraints,font sizes etc.
//    a layout takes that xml and inflates it to produce the actual View object that can be displayed on the screen.
//    so we will take the LayoutInflator from context and we'll be give a context in the constructor.
//    so rather than retrieving inflator every time we need it, we'll store in a class field so that we can use it whenever we need it.

    private final int layoutResource; // we had made this field final,yo make sure we don't accidentally change them in our code.
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;

    // make constructor
    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource=resource;
        this.layoutInflater=LayoutInflater.from(context);
        this.applications = applications;
    }

    // Contexts are used extensively throughout the Android framework,especially when dealing with user interface.

    // to make this class work we just need to override two methods of the base class.
    // Now when the ListView scrolls items off the screen,it will ask its adapter for a new view to display.And it does by calling the getView method,so that's the first method we need to override.
    // The list View also need to know how many items there are,so it knows things like how to represent the scroll bar to indicate how through the list we've scrolled. To get the number of items,it calls the getCount method.
    // Now their are other override methods to further customize the adapter,but these two are all that are actually required and all that we need to get our application working.

    @Override
    public int getCount() {
        return applications.size(); // it will return the number of entries that are in the applications list.
    }

    // it will be called everytime by ListView everytime it wants another item to display.
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        // problem is where we inflate a view each time because there is no need of that as listView provide us a view to reuse when it can and thats what convertView is for.
//        // ListView passes a reference to it in convertView.Now until a view's scrolled off the screen, there won't be a view to re-use. so we have to check if convertView is null and only create a new view if it is null.
//        if(convertView==null){
//            convertView =layoutInflater.inflate(layoutResource,parent,false);
//        }
//        // View view=layoutInflater.inflate(layoutResource,parent,false); // creating a view by inflating the layout resource. So it uses the LayoutInflater that we created in the constructor to do that. So this is the view constraint layout that holds the three TextViews.Parent is list_record.xml that is used for this view.
//        // TextView tvName=(TextView) view.findViewById(R.id.tvName); // as we are inflating the view that's why we write view.findViewById that relates to our layoutResource and ultimately that's going to be coming from our list_record, so we are telling to find the ID that's part of this view.
//        // TextView tvArtist=(TextView) view.findViewById(R.id.tvArtist);
//        // TextView tvSummary=(TextView) view.findViewById(R.id.tvSummary);
//
//        TextView tvName=(TextView) convertView.findViewById(R.id.tvName); // as we are inflating the view that's why we write view.findViewById that relates to our layoutResource and ultimately that's going to be coming from our list_record, so we are telling to find the ID that's part of this view.
//        TextView tvArtist=(TextView) convertView.findViewById(R.id.tvArtist);
//        TextView tvSummary=(TextView) convertView.findViewById(R.id.tvSummary);
//
//        FeedEntry currentApp=applications.get(position);
//
//        tvName.setText(currentApp.getName());
//        tvArtist.setText(currentApp.getArtist());
//        tvSummary.setText(currentApp.getSummary());

        // With ViewHolder pattern:
        ViewHolder viewHolder; // creating ViewHolder Variable to hold the ViewHolder object
        if(convertView==null){
            Log.d(TAG, "getView: called wih null convertView");
            convertView =layoutInflater.inflate(layoutResource,parent,false);
            viewHolder=new ViewHolder(convertView); // creating ViewHolder object
            convertView.setTag(viewHolder); // we are storing object in convertView's tag using the setTag method.
        }
        else{
            Log.d(TAG, "getView: Provided a convertView");
            // if we get back the existing view by the ListView then convertVie won't be null
            viewHolder=(ViewHolder) convertView.getTag(); // retrieving ViewHolder from it's tag using getTag method.
        }

        FeedEntry currentApp=applications.get(position); // retrieving the application

        viewHolder.tvName.setText(currentApp.getName()); // setting value from the widgets stored by the ViewHolder
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        // return view;
        return convertView; // now we are reusing the view given back to the adapter by the ListView and we're only creating a new view if and only if list view hasn't given us view to re-use that is when convertView is null.
    }
    private class ViewHolder{
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        // ViewHolder class is very basic as it used findViewById to find the widgets int view v which is passed to constructors and store them in its fields.
        ViewHolder(View v){
            this.tvName=v.findViewById(R.id.tvName);
            this.tvArtist=v.findViewById(R.id.tvArtist);
            this.tvSummary=v.findViewById(R.id.tvSummary);

        }
    }
}
