package kr.co.hanbit.chatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ChattingRoom extends AppCompatActivity {
    
    ActionBar actionBar;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    HashSet<String> nameSet;
    ArrayList<String> items;
    Iterator<String> iterator;

    public static Context chattingRoomContext;

    ChattingRoomFragment1 chattingRoomFragment1;
    ChattingRoomFragment2 chattingRoomFragment2;

    //public static Activity activity;

    int num = 0;//테스트용변수



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chattingroom_activity);
        Log.d("Fragment2", "ChattingRoom onCreate()호출");

        //chattingRoomContext = getApplicationContext();
      //  activity = this;
        setInit();//HashSet데이터를 받아서

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment2_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.fragment2_plus){//상단 플러스버튼 누르면 사진추가 화면으로 이동
            Intent intent = new Intent(getApplicationContext(), AddGallery.class);
            intent.putExtra("nameSet", nameSet);
            startActivity(intent);
        }
        return true;
    }



    private void setInit() {
        //인텐트 받기
        Intent getIntent = getIntent();
        items = new ArrayList<>();
        nameSet = (HashSet<String>) getIntent.getSerializableExtra("nameSet");
        Log.d("Friends", "nameSet의 사이즈 = " + nameSet.size());
        iterator = nameSet.iterator();

        while (iterator.hasNext()){
            //Log.d("Friends", "nameSet에서 가져온 원소 : " + iterator.next());//items에 잘 들어가는지 확인
            items.add(iterator.next());//items에 nameSet에 있는 원소들을 저장
            Log.d("Friends", "items에 들어간값" + items.get(num));
            num++;//여기 약간 수상

        }
        //상단바에 채팅에 참가한 사용자 이름 출력
        String temp = "";
        for(int i = 0; i < items.size(); i++){
            if(i == items.size() - 1){
                temp+=(items.get(i));
            }else{
                temp+=(items.get(i))+(", ");
            }
        }
        actionBar = getSupportActionBar();
        actionBar.setTitle(""+temp);


        //뷰페이저 설정
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        chattingRoomFragment1 = new ChattingRoomFragment1(items, nameSet);//채팅방에 참가한 사용자 이름을 전송, HashSet으로도 전송

        pagerAdapter.addItem(chattingRoomFragment1);
        //추가한거
        chattingRoomFragment2 = new ChattingRoomFragment2(nameSet);
        pagerAdapter.addItem(chattingRoomFragment2);
        //추가한거
        viewPager.setAdapter(pagerAdapter);

    }




    class PagerAdapter extends FragmentStatePagerAdapter{

        ArrayList<Fragment> items = new ArrayList<>();

        public void addItem(Fragment fragment){//
            items.add(fragment);
        }

        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getCount() {
            return items.size();
        }
    }
}
