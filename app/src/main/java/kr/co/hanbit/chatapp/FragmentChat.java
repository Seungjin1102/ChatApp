package kr.co.hanbit.chatapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.HashSet;

public class FragmentChat extends Fragment {

    Context mainContext;//메인액티비티의 context;
    RecyclerView chatRecyclerView;
    LinearLayoutManager layoutManager;
    public RequestManager requestManager;
    ArrayList<HashSet<String>> myChatList;
    ChatCustomAdapter chatCustomAdapter;
    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestManager = Glide.with(getActivity());
    }

    public FragmentChat(Context mainContext) {
        this.mainContext = mainContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("채팅목록");

        view = inflater.inflate(R.layout.fragment_chat, container, false);
        getChatList();
        return view;
    }

    private void getChatList() {
        myChatList = new ArrayList<>();
        for(int i = 0;i < MainActivity.chatRoomList.size();i++){
            if(MainActivity.chatRoomList.get(i).contains(Login.MY_NAME)){//전체 채팅방 목록중 나의 이름이 포함되어 있으면 myChatList에 추가
                myChatList.add(MainActivity.chatRoomList.get(i));
            }
        }
        setInit();//초기설정
    }

    private void setInit() {//초기설정
        chatRecyclerView = (RecyclerView)view.findViewById(R.id.chatRecyclerView);
        chatCustomAdapter = new ChatCustomAdapter(mainContext, myChatList, requestManager);//나의 이름이 들어간 채팅방 집합을 커스텀어댑터로 보냄
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        chatRecyclerView.setAdapter(chatCustomAdapter);
        chatRecyclerView.setLayoutManager(layoutManager);
    }


}
