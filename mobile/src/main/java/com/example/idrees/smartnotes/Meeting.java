package com.example.idrees.smartnotes;

/**
 * Created by idrees on 7/8/18.
 */

public class Meeting {

    private String meetingName;
    private String timeStamp;

    @Override
    public String toString() {
        return "Meeting{" +
                "meetingName='" + meetingName + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", place='" + place + '\'' +
                '}';
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Meeting(String meetingName, String timeStamp, String place) {

        this.meetingName = meetingName;
        this.timeStamp = timeStamp;
        this.place = place;
    }

    private String place;

}
