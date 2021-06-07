package org.tensorflow.lite.examples.detection;

public  class SaveData {


    String FieldName;
    String Date;
    int VolumeConsumed;
    int ZipCode;
    double Longitude;
    double Latitude;
    String MeterNumber;
    
    public SaveData(String FieldName, String Date, int ZipCode, int Volume, double Longitude, double Latitude,String MeterNumber) {
        this.FieldName = FieldName;
        this.Date = Date;
        this.ZipCode = ZipCode;
        this.VolumeConsumed = Volume;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.MeterNumber = MeterNumber;

    }

    public String getFieldName() {
        return FieldName;
    }

    public int getVolumeConsumed() {
        return VolumeConsumed;
    }

    public String getDate() {
        return Date;
    }

    public int getZipCode() {
        return ZipCode;
    }

    public double getLongitude() {
        return Longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public String getMeterNumber() {
        return MeterNumber;
    }

}
