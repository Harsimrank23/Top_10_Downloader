package com.example.top10downloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listApps;

    // ===========================group menu=====================
    // as we are altering only limit parameter so base part of url should be stored as a class field rather than private variable in the onOptionItemSelected method so that we can use its value from one call to the next as different limits are chosen.
    // and we also need to store current limit size, so we are going to add two fields.
    private String feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"; // %d is a way of specifying an integer value that will be replaced by actual value using the String.format method
    // string.format method takes a string containing special codes like %d and a number of values that are used to replace the format codes.
    private int feedLimit=10; // initially set to 10

    // challenge sol
    // storing cache to restore the url and feedlimit:
    private String feedCachedUrl="INVALIDATED"; // when force downloading is done eg when refresh we need to set url to some value that isn't url.
    public static final String STATE_URL="feedURl" ; // (short form psfs)
    public static final String STATE_LIMIT="feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listApps=(ListView) findViewById(R.id.xmlListView);

//        Log.d(TAG, "onCreate: starting AsyncTask");
//        DownloadData downloadData=new DownloadData(); //creating instance of DownloadData class
//        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml"); // go to apple rss feed and copy link for top 10 free apps
//        Log.d(TAG, "onCreate: done");
        // we can start background task just by creating an instance and calling its execute method.
        // when AsyncTask classes's execute method is called,it takes care of setting up the multi-threading,then it runs doInBackground on a seperate thread.
        // when the task completes,the AsyncTask gets the return value from the other thread,then calls the onPostExecute method with the return value.

        //=========challenge=====
        if(savedInstanceState!=null) // if bundle is not null it means that the activity's being restarted because of device rotation or something like that so we will restore it.
        {
            feedUrl=savedInstanceState.getString(STATE_URL);
            feedLimit=savedInstanceState.getInt(STATE_LIMIT);
        }

//        //==============while creating menu updation:==========
//        downloadUrl("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");

        //==============while creating menu group updation:==========
        downloadUrl(String.format(feedUrl,feedLimit)); // passing bad address and actual value to %d

    }

    // =========menu==============

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // it is called when it's time to inflate the activities menu. that is create the menu objects from the xml file.
        getMenuInflater().inflate(R.menu.feeds_menu,menu); // so when we tried to inflate a view in the adapter, we had to get the inflater from the context. but an activity or an app CompatActivity is a context. so we can just call getMenuInflater to get an inflater directly and then call its inflate method and give it the resource ID of the XML file containing the menu's XML

        // ====================menu group=================
        if(feedLimit==10){
            menu.findItem(R.id.mnu10).setChecked(true);
        } else{
            menu.findItem(R.id.mnu25).setChecked(true);
        }

        return true; // returning true to tell Android that we've inflated a menu.
    }

    @Override // it is called whenever item is selected from option menu. so when this is called menu passes item that is selected
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // first step is get item id:
        int id=item.getItemId();
        // switch statement to set the url of the feed and store it in a string
//        String feedUrl; // as we have defined it in class while updation for group menu
        switch (id){
            // copy url's from apple rss feed: https://www.apple.com/rss/
            case R.id.mnuFree:
//                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
//                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=10/xml";
                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
//                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=10/xml";
                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            //======================Menu Group=============
            case R.id.mnu10:
            case R.id.mnu25:
                if(!item.isChecked()){
                    item.setChecked(true);
                    feedLimit=35-feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: "+item.getTitle()+" setting feedlimit to "+feedLimit);
                } else{
                    Log.d(TAG, "onOptionsItemSelected: "+item.getTitle()+" feedlimit unchanged");
                }
                break;
            //============challenge=======
            case R.id.mnuRefresh:
                feedCachedUrl="INVALIDATE"; // re-download again
                break;

            default:return super.onOptionsItemSelected(item); // it is very important and should be included if want code to react to menu choices, here it will never be called but it is important while creating sub-menu and if u don't return the default option,then any code after the switch will execute and that could cause problems.
        }
//        downloadUrl(feedUrl); // once the url has changed,is exactly we currently do in the onCreate method.We'll create a new download data object and call its execute method with the new url.

        //==============while creating menu group updation:==========
        downloadUrl(String.format(feedUrl,feedLimit)); // passing bad address and actual value to %d

        return true;
    }

    // ===========challenge part 2 saving instance url and feedlimit=======
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(STATE_URL,feedUrl);
        outState.putInt(STATE_LIMIT,feedLimit);
        super.onSaveInstanceState(outState);
    }


    private void downloadUrl(String feedUrl){

        // ============challenge=====
        if(!feedUrl.equalsIgnoreCase(feedCachedUrl)){
            // same as onCreate method
            Log.d(TAG, "downloadUrl: starting AsyncTask");
            DownloadData downloadData=new DownloadData(); //creating instance of DownloadData class
            downloadData.execute(feedUrl);
            feedCachedUrl=feedUrl;
            Log.d(TAG, "downloadUrl: done");
        }
        else{ // no need to download data again if url is same
            Log.d(TAG, "downloadUrl: URL not changed");
        }
//        // same as onCreate method
//        Log.d(TAG, "downloadUrl: starting AsyncTask");
//        DownloadData downloadData=new DownloadData(); //creating instance of DownloadData class
//        downloadData.execute(feedUrl);
//        Log.d(TAG, "downloadUrl: done");
    }

    // Processing XML data:
    // we are going to parse out the individual fields from the xml.
    // so we are going to start by creating a basic class that we can use to hold the information coming out of that xml. this class is to store the individual entries that we extract from the xml.
    // make new java class in the java>com.example.top10downloader(right click and create new class with name FeedEntry)

    // creating class that extends AsyncTask class (basic definition):
    // so we had to pass three parameters when we create an AsyncTask so that android knows what type of task we're dealing with.
    // First parameter: we are passing parameter of type string.We're going to pass in the URL to RSS feed so we'll store that as string
    // 2nd parameter:Now that's normally used if we want to display a progress bar. In this case our download is quite small,so there actually wouldn't be time for progress bar to be displayed, so we are using void as means of saying that we are not using it.
    // 3rd parameter: it is tetype of the result that we want to get back. Now all our XML which is going to be the format of the information that we're retrieving from our RSS feed, will be in a string.

    private class DownloadData extends AsyncTask<String,Void,String>{
        // Override methods:
        // for updating a progress bar we'll probabily want to do that in the onProgressUpdate.
        // onPreExecute would be used to set up the progress bar but can also be used for setting up anything else that's needed for our task to run.
        // both of these methods run on UI thread ,otherwise they wouldn't be able to display anything.
        // it's important not to do anything like trying to start the download in onPreExecute.
        // The another method we do on here is PostExecute ,this also run on main UI thread,once the background process is completed.
        // so we are going to choose onPostExecute and also the doinBackground, doinbackground is the main method that actually does the processing on the other thread.

        // logt
        private static final String TAG = "DownloadData"; // to checking how the things are called.
        
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is "+s);
            ParseApplications parseApplications=new ParseApplications();
            parseApplications.parse(s); // s is xml that framework has sent after downloading it in the doinBackground method.

//            // set up an array adapter: it means array adapter is using feedEntry object
//            // we need to pass three parameters: first parameter is context, second parameter is R.layout.list_item (it is the resource containing the TextView that the arrayAdapter will use to put the data into), third is: parseApplications.getApplications() (list of objects to display)
//            ArrayAdapter<FeedEntry> arrayAdapter=new ArrayAdapter<FeedEntry>(
//                    MainActivity.this,R.layout.list_item,parseApplications.getApplication());
//            // Linking ListView to adapter:
//            listApps.setAdapter(arrayAdapter); // telling ListView to use this adapter to get its data.

            // Custom Adapter:
            FeedAdapter feedAdapter=new FeedAdapter( MainActivity.this,R.layout.list_record,parseApplications.getApplication());
            listApps.setAdapter(feedAdapter);
        }

        // doInBackground method is the ellipsis- the three dots after String in the parameter list.
        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: Starts with "+strings[0]);
            String rssFeed=downloadXML(strings[0]); // string[0] will be the first parameter.
            if(rssFeed==null){
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        } // so when you use ellipsis form of a parameter the actual value get passed into method as an array- an array of Strings in this case.
        // the one we're interested in will be the first one in the array strings 0, and thats what we are passing in line no 55 to download the XML.

        // normally we use logd for writing our debug information to the log but it would be an actual error.
        // log.d entries generally don't appear in the logcat.the build process removes them from code. but if u want msg to remain use log.e instead of log.d to log the message as an error rather than at debug level.

        // DownloadXml function:
        // we are going to start by opening a HTTP connection which we use to access the stream of data coming over the internet from a URL.
        // the connection provides an InputStream, so we're going to use an InputStream reader to read data from it.
        // Now whenever you're dealing with a stream that's coming from a slow device such as a disk driver or an internet connection, it's usually a good idea to use a BufferedReader.
        // bufferedReader buffers the data coming in from the stream. so instead of repeatedly accessing the hard drive or network, a block of data is read into the buffer in memory and our program can then read from the buffer.
        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder(); // StringBuilder objects are like String objects, except that they can be modified , we are using this because we are going to append a lot as we read characters from the Input stream.

            // we are dealing data from an external source, and that basically means anything that isn't in the computer memory.there's a lot of things that can go potentially wrong;the device may not connected to internet or connection may drop while we're downloading the data and it's also possible that URL passed may be invalid,etc.
            // so a try block is a way to wrap up a section of code and to catch any exceptions that occur,while it's executing.
            try {
                // capitals for URL and then lowercase for variable name.
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // when we open http connection server sends back a response code for the connection and logs it into the logcat.
                int response = connection.getResponseCode(); // we retrieved the response code
                Log.d(TAG, "downloadXML: The response code was " + response); // logging the response ,eg 4o4 is a response code that a web server returns if can't find the page that was requested.
                InputStream inputStream = connection.getInputStream(); // so our connection creates inputstream
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream); // uses inputStream to create an InputStreamReader. the InputStreamReader uses an InputStream object as the source of its data and the inputStream is created from our open HTTP connection.
                BufferedReader reader = new BufferedReader(inputStreamReader); // InputStreamReader is used to create a Bufferedreader.BufferedReader reads the characters not the string so we need to set up a character array.

                int charsRead;
                char[] inputBuffer=new char[500]; // created inputBuffer character array to store 500 characters
                while(true){ // will run until end of the inputStream is reached.
                    charsRead=reader.read(inputBuffer); // charsRead variable will hold a count of the number of characters that we've read from the stream.
                    if(charsRead<0) // there may be nothing at the time we read but might be available later so we are not breaking loop at 0
                        break;
                    if(charsRead>0)
                        xmlResult.append(String.copyValueOf(inputBuffer,0,charsRead)); // appending to xmlResult from inputBuffer until nothing left to read.
                    //when the end of inputStream reached , the loop terminates and then we close the BufferedReader.
                }
                reader.close();

                return xmlResult.toString(); //convert the stringBuilder to string

            } catch (MalformedURLException e) { // to handle URL exception
                Log.e(TAG, "downloadXMl:Invalid URL " + e.getMessage());
            } catch(IOException e){
                Log.e(TAG, "downloadXML: IO Exception reading data: "+e.getMessage());
            } catch(SecurityException e){ // we hadn't give app access to internet so it could be handle by security exception
                Log.e(TAG, "downloadXML: Security Exception. Needs permission? "+e.getMessage() );
                e.printStackTrace(); // to show stackTrace from logcat
            }
            return null;
        }
    }
}

//About :
//RSS: Rich Site Summary or Really Simple Syndication but neither of those are particularly descriptive. So RSS is a way to present web data in a standard format so that users can subscribe to the RSS feed and receive updates automatically.
//
//        Now data isn't pushed out from other web sites,it is the responsibility of the feed reader program to monitor the site and pull down updated info as it changes.
//
//        For eg:Search apple RSS feed.
//
//        RSS itself is not the full info.it generally contains a summary and was a way to get notification of changes to the content of sites that you might be interested without having to keep visiting the sites in your browser to see if something's changed.
//
//        So we are going to make app that is going to read the data from top 10 free Apps feed.
//
// so open link top 10 free aps from apple RSS feed , it will contain raw xml file.
// in this we are going to extract the data from some of these fields or tags of the xml file,and display it in the app.
// Now we are going to use entry tags which contains names of the app and a summary,We're also going to display the artist and release date and we are also going to load image url as well.


//AsyncTask CLASS:
// Now when we do something like downloading data over the internet,we don't really have any major control over how long the process will take.
// Sometime's the internet can be very slow,and sometimes even sites such as Apple's can go down and become unavailable.
// now this will definitly take long downloading time and cause our app to freeze.
// The way to cope with this is to run the download on a seperate thread.
// Now writing multithreaded application is extremely difficult, but fortunately Android provides a class that takes care of all the complexity for us.
// So we can use what's called an AsyncTask class to perform a download on a seperate thread and get notified when the download is finished.
// so download will happen on the background and UI(user interface) thread will recieve the results of the downloads, so there is no freezing waiting for that to happen.
// Download will happen asynchronously that is on background on a seperate thread.
// we can switch to another app and do something else while the AsyncTask is running.

// app's got no permissions to anything at all.an app has to get permissions from the user to access resources that it needs, and this was usually done when an app was installed.
// to give permission to app we need to declare in manifest file in app>manifests>AndroidManifest.xml
// after this we have raw xml data now we need to process that XML data


// steps:
// create a class that extends AsyncTask
// crate object for it to do Background tasks and write override methods for that.
// give security permissions in manifest file.
// process XML data by making new class.
// make another class ParseApplication to make list of feedEntries : it is going to store all the apps-the applications that it finds in the XML data,and it is going to store them in the class.And it'll be actually created as we parse each of those entry tags in XML,and will be stored in a ArrayList
// PostExecute is called automatically by android framework after doinBackground has completed.So in onPostExecute create a new object for ParseApplications

// Design:
// widget:listview and its job is to display views in a list.it will display as many views as will fit on the screen. As the screen scrolls it adds more views to the bottom or top if we're scrolling down so that the screen's always full.
// now these views can contain more than one widget, so we could have a TextView and an ImageView widget to display the photo in description.
// to make the listView work,we have to put an adapter between the data and ListView.
// whenever the ListView needs to display more data,it asks the adapter for a view that it can display.
// the adapter takes care of putting the values of the data into the correct widgets in the view,then returns the view to the ListView for displaying.
// for more see video 102
// we are going to create our own adapter later,but for now we can use the basic ArrayAdapter that's part of the Android framework.
// ArrayAdapter is very basic and can only put data into a single TextView widget.We have very little control over how the data's presented,the ArrayAdapter just uses the object's toString method, and puts the returned string into the textView(that's why we created toString method).
// next layout we need to create isn't really isn't really a layout its just going to be TextView,stored in an XML layout file.
// create a layout file which will be used to find the layout resources in our code,and we gonna call it list_item as it is going to display each item in the list. type root element as TextView.

// create adapter so that listView can use it.
// create listView variable in main.activity
// connect the data to the ListView by using an adapter.
// Now our data is stored in an Array List which we can get from the ParseApplications class by calling its get applications method.
// so put code in postExecute method.

// challenge :create a ConstraintLayout containing three TextView widgets,arrange one below the other . call the layout "list_record".
// the textview shoulds have ids 1.tvName 2.tvArtist 3.tvSummary
// for more see video 104

// creating Custom Adapter:
// using ListView to display a bit more complex than just a single text widget.
// array adapter only uses single widget to display.
// in list view when we talk about adapters we can send a single widget or we can send views containing other views.
// so we are gonna do with custom adapter is change the text in each of the TextViews and send them all to the ListView packages inside constraintLayout View of list_record.

// so to make custom adapter make a new class FeedAdapter(make super class ArrayAdapter) that we will actually extend the ArrayAdapter class, it'll have all the functionality of an ArrayAdapter but we can change some methods to deal with more complicated view that we want to use.
// and add code in postExecute accordingly.

// now our adapter isn't very efficient, if we look at getView method it inflates a new view every time it's called. now if we used it thousands of times to display items,it would create one thousand views and that's very costly in terms of both time and memory.
// now the findViewById method is very slow because it has to scan the layout from the start each time it's called checking to see which widget is one we want.
// we can se memory/cpu etc from profiler.
// now we will improve efficiency of our adapter so we will modify our feedAdapter file.
// problem is where we inflate a view each time because there is no need of that as listView provide us a view to reuse when it can and thats what convertView is for.

// ViewHolder pattern:
// now we get much more efficient adapter but we can improve it much further.
// we are going to employ ViewHolder pattern to do that. in getView method we use findViewById to get a reference to each of the widgets in the view, now even we are re-using the view we had to find the individual TextView widgets, in order to change their text.
// we are using finViewById every time for the views we have re-used. in order to avoid this we should store reference to widgets somewhere, and use the stored references to the TextView widgets rather than searching for them everytime. and that's what ViewHolder pattern does.
// it is called ViewHolderPattern because it uses a small class to hold the views that we found the last time. So we're going to create a class that has a field for each of the widgets that we need to find.
// The other thing we need is some way to store the instance of this class,and View objects have a tag field that can be used for that.
// So create class ViewHolder in the FeedAdapter class.
// and make changes in getView method of FeedAdapter class.

//===================Android Menus==============================
// Adding a menu: to choose from the feed to display when we wanted.
// Creating menus before AS version 2.2 was typing in xml but after that google introduced menu designer.
// so now res>right click>new>android resource directory> change resource type to menu
// now right click on menu in res folder >new >Menu resource file
// this menu will be list of available feeds. so name it as feeds_menu.
// there are few things different in menu from the layouts.
// first is there is small choice to drag from palette.
// another difference is that there's no blueprint option.
// Menu's don't have constraints now all the buttons to do with constraints are also not present.
// we make use of component tree's much when creating menus
// now go to feed_menu.xml
// now add code int MainActivity.java to make menu working in the app.
// add override method of OptionsMenu , 1.onCreateOptionsMenu 2.onOptionsItemSelected.

// =================================Menu Groups==================================
// we will add another two options to the menu so that we can choose between top 10 and top 25
// edit feed_menu.xml (prefer working with component tree while working with menus)
// select menu Group checkableBehaviour as single (so that any one between top 10 and top 25 should be selected) it allows only one of the option to be selected.
// make top 10 item as checkable (tick) if want to select many options .
// but here we keep it false because we want radio buttons and want to select one at a time.(make checked tick to make top 10 as default selected).
// now add code accordingly in mainActivity.java in optionsItemSelected ,etc.

// challenge:
// modify the app so that it doesn't download data from the same url a second time unless the user specifically chooses to refresh the data.
// also store the current url and limit so that they aren't reset when the device orientation changes - the data will still be downloaded again following a change in orientation,but it should download from the same URL that the user was looking at before they rotated the device.

// Sol: add refresh option to the menu.
// make variables in mainActivity.java and modify code in downloadUrl when new URL is going to be downloaded.
// after making OnSaveInstance we need to restore it but if we see app life cycle on create method is called before onRestore method, so we need to retrieve the saved Bundle in the onCreate method rather than in onRestoreInstanceState.

// For generic Adapters(used when we made more than one adapter in same app) See video 114...
// generic adapter accepts data of particular type