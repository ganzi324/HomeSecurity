package kr.ac.hansung.homesecurity;

/**
 * Created by sky on 2017-12-02.
 */

public class Video {

    private String filename;
    private String date;
    private int duration;
    private int filesize;
    //private String fileUrl;

    public Video() {
    }

    public Video(String filename, String date, int duration, int filesize) {
        this.filename = filename;
        this.date = date;
        this.duration = duration;
        this.filesize = filesize;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

}