package org.tensorflow.lite.examples.detection;

import com.google.firebase.database.Exclude;

public class Upload {

    private String mName;
    private String mImageUrl;
    private String mKey;
    private String mZip;
    private String mNumber;

    public Upload() {
        //empty constructor needed
    }


    public Upload(String name, String imageUrl, String zip, String number) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mName = name;
        mImageUrl = imageUrl;
        mZip =  zip;
        mNumber = number;
    }
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }
    public String getImageUrl() {
        return mImageUrl;
    }
    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
    public String getZip() { return mZip; }
    public void setZip(String zip) { this.mZip = zip; }
    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
    public String getNumber() { return mNumber; }
    public void setNumber(String number) { this.mNumber = number; }
}
