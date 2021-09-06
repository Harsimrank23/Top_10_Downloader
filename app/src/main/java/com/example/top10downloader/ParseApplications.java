package com.example.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplications {

    // So it is going to store all the apps-the applications that it finds in the XML data,and it is going to store them in the class.And it'll be actually created as we parse each of those entry tags in XML,and will be stored in a ArrayList.

    // creating logTag to see whats going on:
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> application; // it will contain all our FeedEntry objects -one per application-that we've sort of grabbed out of the xml


    // create constructor to initialize array list amd set it up so its ready to start having entries added to it.:
    public ParseApplications() {
        this.application=new ArrayList<>();
    }

    // getter so that mainActivity gets the arrayList:
    public ArrayList<FeedEntry> getApplication() {
        return application;
    }

    //method that will be going to actually parse or manipulate the xml data string and create that list of applications that we're going to be storing in our applications ArrayList.
    // making this method to return a boolean value, so if there's any problem we're going to return false to indicate that data couldn't be parsed.
    public boolean parse(String xmlData){ // it takes single argument which is the string to parse means xml data that has been downloaded will be sent to this method and thats we're going to be using to extract entries out of.
        boolean status=true; // true initially but will be set to false if an exception is thrown when we're parsing the data.
        FeedEntry currentRecord=null; // each time we get a new entry, we need to create a new FeedEntry object to store the details, so we're going to store those details in current record.
        // the code has to make sure that we're processing the data inside an entry tag and we're using the variable inEntry for that..
        boolean inEntry=false;
        String textValue=""; // it is used to store the value of the current tag, so we're going to assign that to the appropriate field of our FeedEntry object.

        try{
            // line no 39 to 41 are responsible for setting up the java XML parser that will do all the hardwork of making sense if the xml for us.
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance(); // so u can't just go creating instances of PullParser objects as you need them.So instead the API provides a factory that will produce a PullParser object for you.
            factory.setNamespaceAware(true);
            XmlPullParser xpp= factory.newPullParser(); // we get PullParser object
            // class factories are often used when u don't know the actual class that will be used. So we don't really care what class,or which class will provide the parsing functionality that we need.All we're interested in is that it can actually parse xml for us.
            xpp.setInput(new StringReader(xmlData)); // at that time we've got a valid PullParser object, so we need to tell it to what to parse,by giving it a string reader that's using the xmlData string.
            // now as the pullParser processes its input ,various events will happen,there'll be things like when it enters a tag or its reaching the end of the document,and so on. we can get those events and respond to them in our code.
            // so first thing we do is check the event and make sure that we haven't reached the end of the document(XML), if we haven't then the while loop will actually keep looping until we get to the end.
            int eventType=xpp.getEventType();
            while(eventType!=XmlPullParser.END_DOCUMENT){
                // start extracting xml data.
                String tagName=xpp.getName(); // getting current tag
                // getting actions depending on the type of events that's happening inside the parser.
                switch (eventType){
                    case XmlPullParser.START_TAG: // if it is starting then we are only interested if it it's an entry tag because we're only doing anything with the data in the individual entries.
                        Log.d(TAG, "parse: Starting tag for "+tagName);
                        if("entry".equalsIgnoreCase(tagName)){
                            inEntry=true;
                            currentRecord=new FeedEntry(); // creating new instance of FeedEntry class and ready to start putting the data into-and again
                        }
                        break;
                    case XmlPullParser.TEXT: //if now event is text , so we need to store that in String variable textValue
                        textValue=xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d(TAG, "parse: Ending tag for "+tagName);
                        if(inEntry){ // these name,artist,entry,etc fields are from xml
                            // test the tag name and assign variable to the correct field of the current/FeedEntry object.
                            if("entry".equalsIgnoreCase(tagName)){
                                application.add(currentRecord); // it means if we had reached the end of all the data for our currentRecord because we have actually reached the next one. so that means we could add currentRecord to the list of applications and set inEntry to false because this is end tag for entry.
                                inEntry=false;
                            } else if("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(textValue);
                            } else if("artist".equalsIgnoreCase(tagName)){
                                currentRecord.setArtist(textValue);
                            } else if("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(textValue);
                            } else if("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            } else if("image".equalsIgnoreCase(tagName)) {
                                currentRecord.setImageURL(textValue);
                            }
                        }
                        break;
                    default://nothing else to do
                }
                eventType=xpp.next(); // checking the next event by calling the PullParser's next method
            }
            for (FeedEntry app:application){
                Log.d(TAG, "**********************");
                Log.d(TAG, app.toString());
            }
        } catch (Exception e){ // catching all the exceptions
            status=false;
            e.printStackTrace();
        }
        return status;
    }
}
