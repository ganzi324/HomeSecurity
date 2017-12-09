package kr.ac.hansung.homesecurity;

/**
 * Created by sky on 2017-12-07.
 */

public class Event {

    private String filename;
    private String date;
    private int level;
    private int type;

    public Event() {
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
