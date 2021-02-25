package kr.co.hanbit.chatapp;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Photo {
    String title;
    String image;
    String time;

    public Photo(String title, String image) {
        this.title = title;
        this.image = image;
        this.time = new SimpleDateFormat("yy/MM/dd HH:mm").format(new Date());
        Log.d("Gallery", "" + this.time);
    }

    public Photo(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
