package com.example.top10downloader;

public class FeedEntry {
    // create fields to the basic feedClass entries
    // we're going to add a field for each one of those key pieces of info.
    private String name;
    private String artist;
    private String releaseDate;
    private String summary;
    private String imageURL;

    // getter and setter for this field.

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    // creating method that just returns info about each one of the key fields so that we can see glance what they are set to.(generator>toString())

    @Override
    public String toString() { // it is going to show us the name and the associated title
        return "name='" + name + '\n' + //remove all the unnecessary apostrophe
                ", artist=" + artist + '\n' +
                ", releaseDate=" + releaseDate + '\n' +
                ", imageURL=" + imageURL + '\n';
    } // every java object has a toString method that's used to provide some sort of textual representation of the object instances.

    // code to parse the XML-means to sort of extract out the various fields that we're interested in,and manipulate it so that those values can be extracted and we can store them in the individual applications object like a feed entry .
    // ok so that's FeedEntryClass created,and every time we get a new entry in the data, we're going to create a new FeedEntry object and set the fields to the values that we find in that XML data.
    // the library in java deals with working out where the tags are,and which bits of the xml are the actual values and so on.so we can use the library to read the values for the tags that we want.
}
