package kr.co.hanbit.chatapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
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
import java.util.Map;
public class FragmentUsers extends Fragment{

    ArrayList<String> items;
    private DatabaseReference databaseReference;
    RecyclerView usersRecyclerView;
    UsersCustomAdapter customAdapter;
    Context context = getContext();
    LinearLayoutManager layoutManager;
    public RequestManager requestManager;
    EditText users_search_input_editText;//옵션바에서 검색어받을 editText

    MainActivity activity;

    int position = 0;//테스트용변수



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("FragmentUsers","onCreate() 호출");
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        requestManager = Glide.with(getActivity());



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        position=0;

        setHasOptionsMenu(true);//상단바 옵션을 ActionBar에 추가
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("사용자목록");

        Log.d("FragmentUsers", "View onCreateView() 실행");
        items = new ArrayList<>();//onDataChange가 데이터가 입력될때마다 콜백메소드로 작동되면 onCreate()로 옮겨도 될거같음...
        getUsersData();
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        activity = (MainActivity) getActivity();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("User", "FragmentUsers items 사이즈 = " + items.size());


                usersRecyclerView = (RecyclerView)view.findViewById(R.id.users_recyclerView);
                customAdapter = new UsersCustomAdapter(items, context, requestManager);
                layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                usersRecyclerView.setLayoutManager(layoutManager);
                usersRecyclerView.setAdapter(customAdapter);

            }
        }, 500);


        return view;

    }



    private void getUsersData() {//사용자 목록 정보를 디비에서 불러오는 함수
        databaseReference.child("ChatApp").child("LoginData").addValueEventListener(new ValueEventListener() {//데이터가 갱신될때마다 콜백호출
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    items.add(dataSnapshot.getKey());
                    position++;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "데이터가 추가실패", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //옵션바 생성
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        Log.d("Users", "onCreateOptionMenu() 호출");
        inflater.inflate(R.menu.users_search_item, menu);


        View view = menu.findItem(R.id.users_search_input).getActionView();
        if(view != null){
            users_search_input_editText = view.findViewById(R.id.users_search_layout_editText);
        }
    }

    //옵션바 메뉴설정


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.users_search){
            Log.d("Users", "돋보기누름");
            if(users_search_input_editText.getText() != null){
                Log.d("Users", "널이아닙니다");
                Log.d("Users", "입력한 검색어 = " + users_search_input_editText.getText().toString());

                String keyword = users_search_input_editText.getText().toString();
                ArrayList<String> key = new ArrayList<>();
                key.add(keyword);

                UsersCustomAdapter searchCustomAdapter = new UsersCustomAdapter(key, context, requestManager);
                usersRecyclerView.setLayoutManager(layoutManager);
                usersRecyclerView.setAdapter(searchCustomAdapter);
                users_search_input_editText.setText("");//검색창 초기화

            }


        }
        return true;
    }

}
