package kr.co.hanbit.chatapp;

import androidx.annotation.NonNull;

public class MyData {//로그인할 때 정보를 담고있는 데이터
    private String name;
    private String password;


    public MyData(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public MyData(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
