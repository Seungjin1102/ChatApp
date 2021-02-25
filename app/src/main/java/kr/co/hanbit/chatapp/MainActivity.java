package kr.co.hanbit.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fragmentManager;//프래그먼트 매니저
    private FragmentTransaction fragmentTransaction;
    private FragmentFriends fragmentFriends;
    private FragmentChat fragmentChat;
    private FragmentUsers fragmentUsers;

    public static ArrayList<HashSet<String>> chatRoomList = new ArrayList<>();//서버에 저장된 전체 채팅방 목록을 담음


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentFriends = new FragmentFriends(this);//메인액티비티 context를 보낸다
        fragmentChat = new FragmentChat(this);
        fragmentUsers = new FragmentUsers();
        setFragment(0);//초기에 친구목록으로 화면 띄우기

        getChattingRoom();

        //내비게이션바 설정
        bottomNavigationView = findViewById(R.id.bottom_navigationbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_friends:
                        setFragment(0);
                        break;
                    case R.id.bottom_chat:
                        setFragment(1);
                        break;
                    case R.id.bottom_users:
                        setFragment(2);
                        break;
                }
                return true;
            }


        });
        


    }

    private void getChattingRoom() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("ChatApp").child("ChatRoom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String temp = dataSnapshot.getKey();
                    HashSet<String> tempSet = new HashSet<>();

                        String[] res = temp.split(",");

                        for(int i =0; i < res.length;i++){
                            tempSet.add(res[i]);
                            Log.d("Friends", "res = " + res[i]);
                        }

                    MainActivity.chatRoomList.add(tempSet);
                }
                Log.d("Friends", "getChattingRoom() 종료 ChatRoomList size = " + chatRoomList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //내비게이션 선택버튼으로 프래그먼트 설정
    private void setFragment(int i) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (i){
            case 0:
                fragmentTransaction.replace(R.id.fragment, fragmentFriends);
                fragmentTransaction.commit();
                break;
            case 1:
                fragmentTransaction.replace(R.id.fragment, fragmentChat);
                fragmentTransaction.commit();
                break;
            case 2:
                fragmentTransaction.replace(R.id.fragment, fragmentUsers);
                fragmentTransaction.commit();
                break;
        }
    }

    public void setCurrentItem(){
        fragmentTransaction.replace(R.id.fragment, fragmentUsers);
        fragmentTransaction.commit();
    }
}