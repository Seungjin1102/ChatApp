package kr.co.hanbit.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Date;

public class Contents  {
    String name;
    String content;
    String time;

    public Contents(String name, String content, String time) {
        this.name = name;
        this.content = content;
        this.time = time;
    }
    public Contents(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
