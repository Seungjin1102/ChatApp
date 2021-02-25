package kr.co.hanbit.chatapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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

public class FragmentFriends extends Fragment {

    ArrayList<String> items;
    DatabaseReference databaseReference;
    RecyclerView friendsRecyclerView;
    Context context = getContext();
    LinearLayoutManager layoutManager;
    public RequestManager requestManager;
    FriendsCustomAdapter customAdapter;
    EditText friends_search_input_editText;
    Context mainContext;//메인액티비티의 context

    public FragmentFriends(Context mainContext) {//메인액티비티의 context
        this.mainContext = mainContext;
    }

    View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        requestManager = Glide.with(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("친구");

        items = new ArrayList<>();
        getFriendsData();
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        return view;
    }


    //친구목록의 이름을 가져오는 함수
    private void getFriendsData() {
        databaseReference.child("ChatApp").child("LoginData").child(""+Login.MY_NAME).child("Friends_" + Login.MY_NAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(!dataSnapshot.getKey().equals(Login.MY_NAME)){//나의 이름과 같지 않으면 동작
                        items.add(dataSnapshot.getKey());

                    }
                }

                friendsRecyclerView = (RecyclerView)view.findViewById(R.id.friends_recyclerView);
                customAdapter = new FriendsCustomAdapter(items, context, requestManager, mainContext);//여기도 추가
                layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
                friendsRecyclerView.setLayoutManager(layoutManager);
                friendsRecyclerView.setAdapter(customAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //상단 옵션바 생성
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.users_search_item, menu);

        View view = menu.findItem(R.id.users_search_input).getActionView();
        if(view != null){
            friends_search_input_editText = view.findViewById(R.id.users_search_layout_editText);
        }
    }

    @Override//상단 옵션바 메뉴 설정
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.users_search){
            if(friends_search_input_editText.getText() != null){
                String keyword = friends_search_input_editText.getText().toString();
                ArrayList<String> key = new ArrayList<>();
                key.add(keyword);

                FriendsCustomAdapter searchCustomAdapter = new FriendsCustomAdapter(key, context, requestManager, mainContext);//여기도 추가
                friendsRecyclerView.setLayoutManager(layoutManager);
                friendsRecyclerView.setAdapter(searchCustomAdapter);
                friends_search_input_editText.setText("");//검색창 초기화
            }
        }
        return true;
    }
}
