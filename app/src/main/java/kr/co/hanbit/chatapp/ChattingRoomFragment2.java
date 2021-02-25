package kr.co.hanbit.chatapp;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public class ChattingRoomFragment2 extends Fragment {

    RecyclerView fragment2_recyclerView;
    RequestManager requestManager;

    ArrayList<Photo> photoItems;

    HashSet<String> nameSet;//채팅방 참가자의 이름이 들어간 집합
    DatabaseReference databaseReference;
    public static Fragment2CustomAdapter fragment2CustomAdapter;
    LinearLayoutManager layoutManager;

    String chatroomName = "";//채팅방 이름

    Context context = getContext();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestManager = Glide.with(getActivity());
        photoItems = new ArrayList<>();
        Log.d("Fragment2", "ChattingRoomFragment2 onCreate()호출");
    }

    public ChattingRoomFragment2(HashSet<String> nameSet) {
        this.nameSet = nameSet;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chattingroom_fragment2, container, false);
        fragment2_recyclerView = (RecyclerView)view.findViewById(R.id.fragment2_recyclerView);
        //채팅방 이름 얻기
        ArrayList<String> items = new ArrayList<>();
        Iterator<String> iterator = nameSet.iterator();
        chatroomName = "";
        while (iterator.hasNext()){
            items.add(iterator.next());//items에 nameSet에 있는 원소들을 저장
        }
        Collections.sort(items);//이름을 알파벳 순서에 따라서 정렬

        for(int i = 0; i < items.size(); i++){
            if(i == items.size() - 1){
                chatroomName+=(items.get(i));
            }else{
                chatroomName+=(items.get(i))+(",");
            }
        }

        //데이터베이스에서 Photo정보 가져와서 phtoItems에 저장
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("ChatApp").child("ChatRoom").child(""+chatroomName).child("Gallery").child("Photo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//여기가 안타짐...
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Photo photo = dataSnapshot.getValue(Photo.class);
                    photoItems.add(photo);
                }
                setInit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }

    private void setInit() {
        fragment2CustomAdapter = new Fragment2CustomAdapter(photoItems, requestManager, context, nameSet, chatroomName);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        fragment2_recyclerView.setLayoutManager(layoutManager);
        fragment2_recyclerView.setAdapter(fragment2CustomAdapter);

    }
}

